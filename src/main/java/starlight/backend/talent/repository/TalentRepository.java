package starlight.backend.talent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.talent.model.entity.TalentEntity;

import java.util.Optional;

@Repository
public interface TalentRepository extends JpaRepository<TalentEntity, Long> {
    boolean existsByTalentSkills_SkillId(Long skillId);
    boolean existsByEmail(String email);

    Optional<TalentEntity> findByEmail(String email);
}
