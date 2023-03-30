package starlight.backend.talent.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TalentUpdateRequest(
        @NotBlank
        @Length(min = 3, max = 64)
        @Pattern(regexp = "^[a-zA-Z]{4,}(?: [a-zA-Z]+){0,2}$", message = "must not contain special characters")
        String fullName,
        LocalDate birthday,
        String avatar,
        String education,
        String experience,
        List<String> positions
) {
}