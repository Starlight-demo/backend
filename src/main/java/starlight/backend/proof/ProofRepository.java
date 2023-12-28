package starlight.backend.proof;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.proof.model.enums.Status;

import java.util.List;

@Repository
public interface ProofRepository extends JpaRepository<ProofEntity, Long> {

    boolean existsByTalent_TalentIdAndSkills_SkillId(Long talentId, Long skillId);

    List<ProofEntity> findByTalent_TalentIdAndSkills_SkillId(Long talentId, Long skillId);

    List<ProofEntity> findByTalent_TalentIdAndSkills_SkillIdAndStatus(Long talentId, Long skillId, Status status);

    Page<ProofEntity> findByTalent_TalentIdAndStatus(Long talentId, Status status, Pageable pageable);

    Page<ProofEntity> findByStatus(Status status, Pageable pageable);

    boolean existsByTalent_TalentIdAndProofId(Long talentId, Long proofId);

    Page<ProofEntity> findByTalent_TalentId(long talentId, PageRequest of);

    List<ProofEntity> findByTalent_TalentId(Long talentId);
}
