package starlight.backend.sponsor.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.advice.config.AdviceConfiguration;
import starlight.backend.advice.model.entity.DelayedDeleteEntity;
import starlight.backend.advice.model.enums.DeletingEntityType;
import starlight.backend.advice.repository.DelayedDeleteRepository;
import starlight.backend.advice.service.impl.AdviceServiceImpl;
import starlight.backend.email.service.impl.EmailServiceImpl;
import starlight.backend.exception.SponsorAlreadyOnDeleteList;
import starlight.backend.exception.SponsorCanNotSeeAnotherSponsor;
import starlight.backend.exception.SponsorNotFoundException;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.sponsor.SponsorRepository;
import starlight.backend.sponsor.model.response.SponsorFullInfo;
import starlight.backend.sponsor.model.enums.SponsorStatus;
import starlight.backend.sponsor.model.response.UnusableKudos;
import starlight.backend.sponsor.service.SponsorServiceInterface;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Service
public class SponsorServiceImpl implements SponsorServiceInterface {
    private SponsorRepository sponsorRepository;
    private SecurityServiceInterface securityService;
    private DelayedDeleteRepository delayedDeleteRepository;
    private AdviceConfiguration adviceConfiguration;
    private SecurityServiceInterface serviceService;
    private EmailServiceImpl emailServiceImpl;
    @Override
    public UnusableKudos getUnusableKudos(long sponsorId) {
        var sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        return new UnusableKudos(sponsor.getUnusedKudos());
    }

    @Override
    public SponsorFullInfo getSponsorFullInfo(long sponsorId, Authentication auth) {
        var sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        isItMyAccount(sponsorId, auth);
        return SponsorFullInfo.builder()
                .fullName(sponsor.getFullName())
                .avatar(sponsor.getAvatar())
                .company(sponsor.getCompany())
                .build();
    }

    private void isItMyAccount(long sponsorId, Authentication auth) {
        if (!serviceService.checkingLoggedAndToken(sponsorId, auth)) {
            throw new SponsorCanNotSeeAnotherSponsor();
        }
    }

    @Override
    @Transactional
    public void deleteSponsor(long sponsorId, Authentication auth, HttpServletRequest request) {
        if (!sponsorRepository.existsBySponsorId(sponsorId)) {
            throw new SponsorNotFoundException(sponsorId);
        }
        if (!securityService.checkingLoggedAndToken(sponsorId, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot delete this sponsor");
        }

        sponsorRepository.findById(sponsorId).ifPresent(sponsor -> {
            if (delayedDeleteRepository.existsByEntityID(sponsorId)){
                throw new SponsorAlreadyOnDeleteList(sponsorId);
            }
            delayedDeleteRepository.save(
                    DelayedDeleteEntity.builder()
                            .entityID(sponsorId)
                            .deletingEntityType(DeletingEntityType.SPONSOR)
                            .deleteDate(Instant.now().plus(adviceConfiguration.delayDays(), ChronoUnit.DAYS))
                            .userDeletingProcessUUID(emailServiceImpl.recoveryAccount(request, sponsor.getEmail()))
                            .build()
            );


            sponsor.setStatus(SponsorStatus.DELETING);
            sponsorRepository.save(sponsor);

        });
    }
}
