package starlight.backend.talent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import starlight.backend.talent.model.request.TalentUpdateRequest;
import starlight.backend.talent.model.response.TalentFullInfo;
import starlight.backend.talent.model.response.TalentPagePagination;
import starlight.backend.talent.service.TalentServiceInterface;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v1")
@Tag(name = "Talent", description = "Talent API")
@Slf4j
public class TalentControllerV1 {
    private TalentServiceInterface talentService;

    @Operation(
            summary = "Get all talents",
            description = "Get list of all talents. The response is list of talent objects with fields 'id','full_name', 'position' and 'avatar'."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TalentPagePagination.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Exception.class
                            )
                    )
            )
    })
    @GetMapping("/talents")
    public TalentPagePagination pagination(@RequestParam(defaultValue = "0") @Min(0) int page,
                                           @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("@GetMapping(\"/talents\")");
        return talentService.talentPagination(page, size);
    }

    @Operation(
            summary = "Get talent by id",
            description = "Get a talent from id. The response is talent object with fields 'full_name', 'email', 'birthday', 'avatar', 'education', 'experience', 'positions'.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TalentFullInfo.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/talents/{talent-id}")
    public TalentFullInfo searchTalentById(@PathVariable("talent-id") long talentId) {
        log.info("@GetMapping(\"/talents/{talent-id}\")");
        return talentService.talentFullInfo(talentId);
    }

    @Operation(
            summary = "Update talent by id",
            description = "Update a talent from id." +
                    " The response is talent object with fields 'full_name', 'email', 'birthday', 'avatar', 'education', 'experience'," +
                    " 'positions'[] (Send null if you want clear position list; Empty do nothing), 'skills'[].")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TalentFullInfo.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @PatchMapping("/talents/{talent-id}")
    public TalentFullInfo updateTalentFullInfo(@PathVariable("talent-id") long talentId,
                                               @RequestBody TalentUpdateRequest talentUpdateRequest,
                                               Authentication auth) {
        log.info("@PatchMapping(\"/talents/{talent-id}\")");
        return talentService.updateTalentProfile(talentId, talentUpdateRequest, auth);
    }

    @Operation(summary = "Delete talent by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TalentFullInfo.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @DeleteMapping("/talents/{talent-id}")
    public void deleteTalent(@PathVariable("talent-id") long talentId,
                             Authentication auth) {
        log.info("@DeleteMapping(\"/talents/{talent-id}\")");
        talentService.deleteTalentProfile(talentId, auth);
    }
}