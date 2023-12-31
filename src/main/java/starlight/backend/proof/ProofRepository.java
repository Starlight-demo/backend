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
    boolean existsByUser_UserIdAndSkills_SkillId(Long userId, Long skillId);

    List<ProofEntity> findByUser_UserIdAndSkills_SkillId(Long userId, Long skillId);

    List<ProofEntity> findByUser_UserIdAndSkills_SkillIdAndStatus(Long userId, Long skillId, Status status);

    Page<ProofEntity> findByUser_UserIdAndStatus(Long userId, Status status, Pageable pageable);

    Page<ProofEntity> findByStatus(Status status, Pageable pageable);

    boolean existsByUser_UserIdAndProofId(Long userId, Long proofId);

    Page<ProofEntity> findByUser_UserId(long talentId, PageRequest of);

    List<ProofEntity> findByUser_UserId(long talentId);
}
