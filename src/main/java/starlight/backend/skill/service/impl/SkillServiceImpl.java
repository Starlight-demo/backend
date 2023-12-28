package starlight.backend.skill.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.exception.proof.ProofNotFoundException;
import starlight.backend.exception.proof.UserAccesDeniedToProofException;
import starlight.backend.exception.proof.UserCanNotEditProofNotInDraftException;
import starlight.backend.exception.skill.SkillNotFoundException;
import starlight.backend.exception.user.UserCanNotEditThisProfile;
import starlight.backend.exception.user.UserNotFoundException;
import starlight.backend.exception.user.talent.TalentNotFoundException;
import starlight.backend.proof.ProofMapper;
import starlight.backend.proof.ProofRepository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.proof.model.enums.Status;
import starlight.backend.proof.model.response.ProofListWithSkills;
import starlight.backend.proof.model.response.ProofWithSkills;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.skill.SkillMapper;
import starlight.backend.skill.model.entity.SkillEntity;
import starlight.backend.skill.model.request.AddSkill;
import starlight.backend.skill.model.request.DeleteIdSkills;
import starlight.backend.skill.model.response.SkillList;
import starlight.backend.skill.model.response.SkillListWithPagination;
import starlight.backend.skill.repository.SkillRepository;
import starlight.backend.skill.service.SkillServiceInterface;
import starlight.backend.talent.model.response.TalentWithSkills;
import starlight.backend.talent.repository.TalentRepository;
import starlight.backend.talent.service.TalentServiceInterface;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class SkillServiceImpl implements SkillServiceInterface {
    private ProofMapper proofMapper;
    private final String filterParam = "skill";
    private SkillRepository skillRepository;
    private SkillMapper skillMapper;
    private SecurityServiceInterface securityService;
    private ProofRepository proofRepository;
    private TalentRepository talentRepository;
    private TalentServiceInterface talentService;

    @Override
    public SkillListWithPagination getListSkillWithFiltration(String filter, int skip, int limit) {
        var skillStream = skillRepository.findAll().stream();

        if (filter != null && !filter.isEmpty()) {
            skillStream = skillStream.filter(skill -> skill.getSkill()
                    .toLowerCase()
                    .contains(filter.toLowerCase()));
        }
        Sort sort = Sort.by(Sort.Order.asc(filterParam));

        var pageable = PageRequest.of(skip, limit, sort);

        List<SkillEntity> sortedSkills = skillStream
                .sorted(Comparator.comparing(SkillEntity::getSkill))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        return skillMapper.toSkillListWithPagination(sortedSkills, skillRepository.count());
    }

    @Override
    public ProofWithSkills addSkillInYourProof(long talentId, long proofId,
                                               Authentication auth, AddSkill skills) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserAccesDeniedToProofException();
        }
        if (!proofRepository.existsByTalent_TalentIdAndProofId(talentId, proofId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t have that talent");
        }
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        if (!proof.getStatus().equals(Status.DRAFT)) {
            throw new UserCanNotEditProofNotInDraftException();
        } else {
            proof.setDateLastUpdated(Instant.now());
            proof.setSkills(existsSkill(
                    proof.getSkills(),
                    skills.skills()));
            proofRepository.save(proof);
        }
        return skillMapper.toProofWithSkills(proof);
    }

    @Override
    public SkillList getListSkillsOfProof(long proofId) {
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        return skillMapper.toSkillList(proof.getSkills().stream().toList());
    }

    @Override
    public List<SkillEntity> existsSkill(List<SkillEntity> proofSkill,
                                         List<String> skills) {
        if (skills != null && !skills.isEmpty()) {
            Set<SkillEntity> newSkills = skills.stream()
                    .map(this::skillValidation)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            newSkills.addAll(proofSkill);
            return newSkills.stream().distinct().collect(Collectors.toList());
        }
        return proofSkill.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public SkillEntity skillValidation(String skill) {
        return skillRepository.existsBySkillIgnoreCase(skill) ?
                skillRepository.findBySkillIgnoreCase(skill) :
                null;
    }

    @Override
    public void deleteSkill(long talentId, long proofId, long skillId, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserAccesDeniedToProofException();
        }
        if (!skillRepository.existsBySkillIdAndProofs_ProofIdAndProofs_Talent_TalentId(skillId, proofId, talentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found skill by talentId and proofId!");
        }
        var skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found this skill"));
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        proof.getSkills().remove(skill);
    }

    @Override
    public void deleteSkillArray(long talentId, long proofId, DeleteIdSkills deleteSkillId, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserAccesDeniedToProofException();
        }
        for (long skillId : deleteSkillId.skillsId()) {
            if (!skillRepository.existsBySkillIdAndProofs_ProofIdAndProofs_Talent_TalentId(skillId, proofId, talentId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found skill by talentId and proofId!");
            }
            var skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found this skill"));
            var proof = proofRepository.findById(proofId)
                    .orElseThrow(() -> new ProofNotFoundException(proofId));
            proof.getSkills().remove(skill);
        }
    }

    @Override
    public void deleteSkills(long talentId, DeleteIdSkills deleteSkillId, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserCanNotEditThisProfile(talentId);
        }
        for (long skillId : deleteSkillId.skillsId()) {
            if (!talentRepository.existsByTalentSkills_SkillId(skillId)) {
                throw new SkillNotFoundException(skillId);
            }
            var skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "don`t found this skill"));
            var user = talentRepository.findById(talentId)
                    .orElseThrow(() -> new UserNotFoundException(talentId));
            user.getTalentSkills().remove(skill);
        }
    }

    @Override
    public TalentWithSkills addSkillToTalent(long talentId, AddSkill skills, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserAccesDeniedToProofException();
        }
        var talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new TalentNotFoundException(talentId));
        talent.setTalentSkills(existsSkill(
                talent.getTalentSkills(),
                skills.skills()));
        talentRepository.save(talent);
        return skillMapper.toTalentWithSkills(talent);
    }

    @Override
    public ProofWithSkills addSkillInYourProofV2(long talentId, long proofId, Authentication auth, AddSkill skills) {
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            throw new UserAccesDeniedToProofException();
        }
        var proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new ProofNotFoundException(proofId));
        var talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new ProofNotFoundException(talentId));
        if (!proof.getStatus().equals(Status.DRAFT)) {
            throw new UserCanNotEditProofNotInDraftException();
        } else {
            talent.setTalentSkills(existsSkill(
                    talent.getTalentSkills(),
                    skills.skills()));
            talentRepository.save(talent);
            proof.setSkills(existsSkill(
                    proof.getSkills(),
                    skills.skills()));
            proofRepository.save(proof);
        }
        return skillMapper.toProofWithSkills(proof);
    }

    @Override
    public TalentWithSkills getListSkillsOfTalent(long talentId, Authentication auth) {
        var talent = talentRepository.findById(talentId)
                .orElseThrow(() -> new UserNotFoundException(talentId));
        return skillMapper.toTalentWithSkills(talent);
    }

    @Override
    @Transactional(readOnly = true)
    public ProofListWithSkills getListProofsOfSkill(long talentId, long skillId, String requestedStatus, Authentication auth) {
        talentService.isStatusCorrect(requestedStatus);
        Status status = Status.valueOf(requestedStatus);
        List<ProofEntity> proofs;
        if (!proofRepository.existsByTalent_TalentIdAndSkills_SkillId(talentId, skillId)) {
            return ProofListWithSkills.builder().data(Collections.emptyList()).build();
        }
        if (!securityService.checkingLoggedAndToken(talentId, auth)) {
            proofs = proofRepository.findByTalent_TalentIdAndSkills_SkillIdAndStatus(talentId, skillId, Status.PUBLISHED);
            return proofMapper.toProofListWithSkills(proofs);
        }
        if (requestedStatus.equals(Status.ALL.getStatus())) {
            proofs = proofRepository.findByTalent_TalentIdAndSkills_SkillId(talentId, skillId);
            return proofMapper.fromFulltoProofListWithSkills(proofs);
        }
        proofs = proofRepository.findByTalent_TalentIdAndSkills_SkillIdAndStatus(talentId, skillId, status);
        return proofMapper.fromFulltoProofListWithSkills(proofs);
    }
}