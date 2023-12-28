package starlight.backend.admin.model.emtity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import starlight.backend.user.model.entity.UserEntity;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "admin")
public class AdminEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long adminId;
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @OneToOne(mappedBy = "admin")
    private UserEntity user;
}
