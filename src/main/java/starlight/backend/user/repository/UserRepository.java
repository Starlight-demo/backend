package starlight.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import starlight.backend.user.model.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByAdmin_Email(String email);
    UserEntity findByAdmin_Email(String email);
    UserEntity findByTalent_Email(String email);

    UserEntity findBySponsor_Email(String email);

    boolean existsByTalent_Email(String email);

    boolean existsBySponsor_Email(String email);
}