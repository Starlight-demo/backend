package starlight.backend.talent.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import starlight.backend.proof.model.entity.ProofEntity;
import starlight.backend.skill.model.entity.SkillEntity;
import starlight.backend.user.model.entity.UserEntity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "talent")
public class TalentEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long talentId;
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    private LocalDate birthday;
    @URL
    private String avatar;
    @Length(max = 255)
    private String education;
    @Length(max = 255)
    private String experience;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "talent_position",
            joinColumns = @JoinColumn(name = "talent_id"),
            inverseJoinColumns = @JoinColumn(name = "position_id"))
    @JsonManagedReference
    private Set<PositionEntity> positions;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "talent_skill",
            joinColumns = @JoinColumn(name = "talent_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @JsonManagedReference
    private List<SkillEntity> talentSkills;

    @OneToMany(mappedBy = "talent")
    @JsonManagedReference
    private Set<ProofEntity> proofs;

    @OneToOne(mappedBy = "talent")
    private UserEntity user;
}
