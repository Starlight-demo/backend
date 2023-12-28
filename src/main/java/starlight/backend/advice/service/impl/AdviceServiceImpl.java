package starlight.backend.advice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starlight.backend.advice.repository.DelayDeleteRepository;
import starlight.backend.advice.service.AdviceService;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.exception.user.sponsor.SponsorNotFoundException;
import starlight.backend.sponsor.SponsorRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AdviceServiceImpl implements AdviceService {
    private DelayDeleteRepository delayDeleteRepository;
    private SponsorRepository sponsorRepository;

    @Override
    @Scheduled(cron = "0 12 * * *") // 12:00 every day UTC
    public void deleteAccounts() {
        var accounts = delayDeleteRepository.findAll();

        for (var account : accounts) {
            if (account.getDeleteDate().isAfter(Instant.now())) {
                //Если дата удаления аккаунта больше текущего момента, то пропускаем
                continue;
            }
            //Тут можно имплементировать распределение логики на удаления аккаунтов с разными ролями в системе
            var accountEntityID = account.getEntityId();
            if (sponsorRepository.findBySponsorId(accountEntityID).isEmpty()) {
                //Если спонсора уже нету в системе, то удаляем из DelayDeleteRepository
                delayDeleteRepository.deleteById(accountEntityID);
                continue;
            }

            log.info("Deleting user with id: {}", accountEntityID);
            //Вытягиваем спонсора
            var sponsor = sponsorRepository.findBySponsorId(accountEntityID)
                    .orElseThrow(() -> new SponsorNotFoundException(accountEntityID));
            //Чистим связи с другими таблицами.
            //Чистим роли
            if (!sponsor.getKudos().isEmpty()) {
                //Если есть кудосы, то очищяем владельцев каждого из них
                for (var kudos : sponsor.getKudos()) {
                    kudos.setOwner(null);
                }
            }
            //Удаляем спонсора из SponsorRepository & DelayDeleteRepository
            sponsorRepository.deleteById(accountEntityID);
            delayDeleteRepository.deleteById(accountEntityID);
        }
    }

    @Override
    public UUID getUUID(long entityId) {
        if (!delayDeleteRepository.existsByEntityId(entityId)) {
            throw new UserNotFoundException(entityId);
        }
        return delayDeleteRepository.findByEntityId(entityId).orElseThrow(() -> new UserNotFoundException(entityId)).getUserDeletingProcessUuid();
    }
}