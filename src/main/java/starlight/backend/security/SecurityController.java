package starlight.backend.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import starlight.backend.security.model.request.NewUser;
import starlight.backend.security.model.response.SessionInfo;
import starlight.backend.security.service.SecurityServiceInterface;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "Security", description = "Security related endpoints")
public class SecurityController {
    private SecurityServiceInterface service;

    @Operation(
            summary = "Login in system",
            description = "Login in system",
            tags = {"Security"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SessionInfo.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/talents/login")
    @ResponseStatus(HttpStatus.OK)
    public SessionInfo login(Authentication auth) {
        log.info("@PostMapping(\"/talents/login\")");
        return service.loginInfo(auth);
    }

    @Operation(
            summary = "Create a new talent",
            description = "Create a new talent",
            tags = {"Security"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = SessionInfo.class
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Conflict")
            }
    )
    @PostMapping("/talents")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionInfo register(@Valid @RequestBody NewUser newUser) {
        log.info("@PostMapping(\"/talents\")");
        return service.register(newUser);
    }

    @Operation(
            summary = "Login in system",
            description = "Login in system",
            tags = {"Security"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SessionInfo.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/sponsors/login")
    @ResponseStatus(HttpStatus.OK)
    public SessionInfo loginSponsor(Authentication auth) {
        log.info("@PostMapping(\"/sponsors/login\")");
        return service.loginInfoSponsor(auth);
    }

    @Operation(
            summary = "Create a new sponsor",
            description = "Create a new sponsor",
            tags = {"Security"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = SessionInfo.class
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Conflict")
            }
    )
    @PostMapping("/sponsors")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionInfo registerSponsor(@Valid @RequestBody NewUser newUser) {
        log.info("@PostMapping(\"/sponsors\")");
        return service.registerSponsor(newUser);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionInfo registerAdmin(@Valid @RequestBody NewUser newUser) {
        log.info("@PostMapping(\"/admin\")");
        return service.registerAdmin(newUser);
    }

    @PostMapping("/admin/login")
    public SessionInfo loginAdmin(Authentication auth) {
        log.info("@PostMapping(\"/admin/login\")");
        return service.loginInfoAdmin(auth);
    }
}
