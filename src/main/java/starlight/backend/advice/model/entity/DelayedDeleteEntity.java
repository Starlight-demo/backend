package starlight.backend.advice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import starlight.backend.advice.model.enums.DeletingEntityType;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "delayed_delete")
public class DelayedDeleteEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long Id;
    @NotNull
    private Long entityId;
    @NotNull
    private UUID userDeletingProcessUuid;
    @NotNull
    private Instant deleteDate;
    @Enumerated(EnumType.STRING)
    private DeletingEntityType deletingEntityType;

}
