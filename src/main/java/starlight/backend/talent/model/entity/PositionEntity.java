package starlight.backend.talent.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name  = "position")
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long positionId;

    private String position;

    @ManyToMany(mappedBy = "positions")
    @JsonBackReference
    private Set<TalentEntity> users;

    public PositionEntity(String position) {
        this.position = position;
    }
}
