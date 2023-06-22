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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import starlight.backend.proof.model.response.ProofListWithSkills;
import starlight.backend.skill.model.response.SkillListWithPagination;
import starlight.backend.skill.service.SkillServiceInterface;
import starlight.backend.talent.model.response.TalentPagePagination;
import starlight.backend.talent.model.response.TalentPagePaginationWithFilterSkills;
import starlight.backend.talent.model.response.TalentWithSkills;
import starlight.backend.talent.service.TalentServiceInterface;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v2")
@Tag(name = "Talent v2", description = "Talent API v2 (with skills")
@Slf4j
public class TalentControllerV2 {
    private SkillServiceInterface skillService;
    private TalentServiceInterface talentService;

    @Operation(
            summary = "Get talent with skills",
            description = "On this you can see the skills of a specific talent."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = SkillListWithPagination.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })

    @GetMapping("/talents/{talent-id}")
    public TalentWithSkills getSkillsOfTalent(@PathVariable("talent-id") long talentId,
                                              Authentication auth) {

        log.info("@GetMapping(\"/talents/{talent-id}/skills\")");
        return skillService.getListSkillsOfTalent(talentId, auth);
    }


    @Operation(
            summary = "Get proofs of skill",
            description = "On this you can see the proofs of a specific skill and status of proofs."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofListWithSkills.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/talents/{talent-id}/skills/{skill-id}/proofs")
    public ProofListWithSkills getProofsOfSkill(@PathVariable("talent-id") long talentId,
                                           @PathVariable("skill-id") long skillId,
                                           @RequestParam(defaultValue = "ALL") String status,
                                           Authentication auth){

        log.info("@GetMapping(\"/talents/{talent-id}/skills/{skills-id}/proofs\")");
        return skillService.getListProofsOfSkill(talentId, skillId, status, auth);
    }

    @Operation(
            summary = "Get all talents",
            description = "Get list of all talents. The response is list of talent objects with fields 'id','full_name', 'position', 'avatar' and '[skills]'. \nEmpty field of filter return all skills."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TalentPagePaginationWithFilterSkills.class
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
    public TalentPagePaginationWithFilterSkills paginationWithFilterSkills(@RequestParam(defaultValue = "0") @Min(0) int skip,
                                                           @RequestParam(defaultValue = "10") @Positive int limit,
                                                           @RequestParam(defaultValue = "") String filter) {

        log.info("@GetMapping(\"v2/talents\")");
        return talentService.talentPaginationWithFilter(filter, skip, limit);
    }
}