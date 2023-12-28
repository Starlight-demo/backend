package starlight.backend.kudos.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starlight.backend.exception.AuthorizationFailureException;
import starlight.backend.exception.YouAreInDeletingProcess;
import starlight.backend.exception.kudos.KudosRequestMustBeNotZeroException;
import starlight.backend.exception.kudos.NotEnoughKudosException;
import starlight.backend.exception.kudos.TalentCanNotAddKudos;
import starlight.backend.exception.kudos.YouCanNotReturnMoreKudosThanGaveException;
import starlight.backend.exception.proof.ProofNotFoundException;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.kudos.model.response.KudosOnProof;
import starlight.backend.kudos.repository.KudosRepository;
import starlight.backend.kudos.service.KudosServiceInterface;
import starlight.backend.proof.ProofRepository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.user.model.enums.Role;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.sponsor.SponsorRepository;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.talent.model.entity.TalentEntity;
import starlight.backend.talent.repository.TalentRepository;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class KudosServiceImpl implements KudosServiceInterface {
    private KudosRepository kudosRepository;
    private ProofRepository proofRepository;
    private TalentRepository talentRepository;
    private SponsorRepository sponsorRepository;
    private SecurityServiceInterface securityService;

    private boolean isProofAlreadyHaveKudosFromUser(long proofId, Authentication auth) {
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        var kudosList = proof.getKudos()
                .stream()
                .filter(k -> k.getOwner()
                        .getSponsorId()
                        .toString()
                        .equals(auth.getName()))
                .toList();
        return !kudosList.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public KudosOnProof getKudosOnProof(long proofId, Authentication auth) {
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        var kudos = proof.getKudos();
        int countKudos = kudos
                .stream()
                .mapToInt(KudosEntity::getCountKudos)
                .sum();
        log.info("countKudos = {}", countKudos);
        if (auth != null) {
            for (GrantedAuthority grantedAuthority : auth.getAuthorities()) {
                if (grantedAuthority.getAuthority().equals(Role.SPONSOR.getAuthority()) &&
                        securityService.isSponsorActive(auth)) {
                    log.info("Is Sponsor = {}", grantedAuthority.getAuthority().equals(Role.SPONSOR.getAuthority()));
                    var kudosFromMeList = kudos.stream()
                            .filter(k -> k.getOwner().getSponsorId().toString().equals(auth.getName()))
                            .toList();
                    int kudosFromMe;
                    if (kudosFromMeList.isEmpty()) {
                        kudosFromMe = 0;
                    } else {
                        kudosFromMe = kudosFromMeList.stream()
                                .mapToInt(KudosEntity::getCountKudos)
                                .sum();
                    }
                    return new KudosOnProof(countKudos, kudosFromMe, isProofAlreadyHaveKudosFromUser(proofId, auth));
                }
            }
        }
        return new KudosOnProof(countKudos, 0, false);
    }

    @Override
    public KudosEntity addKudosOnProof(long proofId, int kudosRequest, Authentication auth) {
        if (!securityService.isSponsorActive(auth)) {
            throw new YouAreInDeletingProcess();
        }
        if (auth == null) {
            throw new AuthorizationFailureException();
        }
        for (GrantedAuthority grantedAuthority : auth.getAuthorities()) {
            if (!grantedAuthority.getAuthority().equals(Role.SPONSOR.getAuthority())) {
                throw new TalentCanNotAddKudos();
            }
        }
        if (kudosRequest == 0) {
            throw new KudosRequestMustBeNotZeroException();
        }
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        var owner = sponsorRepository.findById(Long.valueOf(auth.getName()))
                .orElseThrow(() -> new UserNotFoundException(auth.getName()));
        if (kudosRequest > owner.getUnusedKudos()) {
            throw new NotEnoughKudosException();
        }
        var follower = talentRepository.findById(proof.getTalent().getTalentId())
                .orElseThrow(() -> new UserNotFoundException(auth.getName()));
        updateSponsorUnusedKudos(owner, kudosRequest);
        return updateSponsorKudosField(proof, follower, owner, kudosRequest, proofId);
    }

    private void updateSponsorUnusedKudos(SponsorEntity owner, int kudosRequest) {
        sponsorRepository.findById(owner.getSponsorId()).map(sponsor -> {
            sponsor.setUnusedKudos(owner.getUnusedKudos() - kudosRequest);
            sponsorRepository.save(sponsor);
            return null;
        });
    }

    private KudosEntity updateSponsorKudosField(ProofEntity proof, TalentEntity follower, SponsorEntity owner,
                                                int kudosRequest, long proofId) {
        if (proof.getKudos().stream()
                .filter(kudos1 -> kudos1.getOwner().getSponsorId().equals(owner.getSponsorId()))
                .collect(Collectors.toSet()).isEmpty()) {
            if (kudosRequest < 0) throw new YouCanNotReturnMoreKudosThanGaveException();
            var kudosBuild = KudosEntity.builder()
                    .followerId(follower.getTalentId())
                    .createData(Instant.now())
                    .proof(proof)
                    .owner(owner)
                    .countKudos(kudosRequest)
                    .build();
            kudosRepository.save(kudosBuild);
            return kudosBuild;
        }
        var kudos = kudosRepository.findByOwner_SponsorIdAndProof_ProofId(owner.getSponsorId(), proofId);
        if (kudos.getCountKudos() + kudosRequest < 0) {
            throw new YouCanNotReturnMoreKudosThanGaveException();
        }
        kudos.setCountKudos(kudos.getCountKudos() + kudosRequest);
        kudos.setUpdateData(Instant.now());
        kudosRepository.save(kudos);
        if (kudos.getCountKudos() == 0) {
            kudosRepository.delete(kudos);
        }
        return kudos;
    }
}