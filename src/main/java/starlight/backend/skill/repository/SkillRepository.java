package starlight.backend.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.skill.model.entity.SkillEntity;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {

    boolean existsBySkillIdAndProofs_ProofIdAndProofs_Talent_TalentId(Long skillId, Long proofId, Long talentId);


    SkillEntity findBySkillIgnoreCase(String skill);

    boolean existsBySkillIgnoreCase(String skill);
}