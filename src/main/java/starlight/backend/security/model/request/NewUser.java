package starlight.backend.security.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import starlight.backend.user.model.enums.Role;


@Builder
@Validated
public record NewUser(
        @NotBlank
        @Length(min = 3, max = 64)
        @Pattern(regexp = "^[A-Za-z\\s'-]+$", message = "must not contain special characters")
        @JsonProperty("full_name")
        String fullName,

        @NotBlank
        @Length(max = 254)
        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$", message = "must be a valid email")
        String email,

        @NotBlank
        @Length(min = 8, max = 128)
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)\\S+$",
                message = "must be between 8 and 128 characters, must contain at least one letter and one number")
        String password
) {
}
