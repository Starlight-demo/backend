package starlight.backend.skill.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import starlight.backend.proof.model.response.ProofWithSkills;
import starlight.backend.skill.model.request.AddSkill;
import starlight.backend.skill.model.request.DeleteIdSkills;
import starlight.backend.skill.model.response.SkillList;
import starlight.backend.skill.model.response.SkillListWithPagination;
import starlight.backend.skill.service.SkillServiceInterface;


@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v1")
@Tag(name = "Skill", description = "Skill API")
public class SkillController {
    private SkillServiceInterface serviceService;

    @Operation(
            summary = "Get list of skill",
            description = "Get all skills."
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
    @GetMapping("/skills")
    public SkillListWithPagination listWithFilter(@RequestParam(defaultValue = "0") @Min(0) int skip,
                                                  @RequestParam(defaultValue = "200") @Positive int limit,
                                                  @RequestParam(defaultValue = "") String filter) {
        log.info("@GetMapping(\"/skills\")");
        return serviceService.getListSkillWithFiltration(filter, skip, limit);
    }

    @Operation(
            summary = "Add skill",
            description = "Add a Skill to a Proof, given the Proof ID and the Talent ID, only if the Proof is in status \"Draft\" and the Talent owns the Proof."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofWithSkills.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/talents/{talent-id}/proofs/{proof-id}/skills")
    public ProofWithSkills addSkillInProof(@PathVariable("talent-id") long talentId,
                                           @PathVariable("proof-id") long proofId,
                                           @RequestBody AddSkill skills,
                                           Authentication auth) {
        log.info("@PostMapping(\"/talents/{talent-id}/proofs/{proof-id}/skills\")");
        return serviceService.addSkillInYourProof(talentId, proofId, auth, skills);
    }

    @Operation(
            summary = "Get all skills of proof",
            description = "On this you can see the skills of a specific proof."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = SkillList.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/proofs/{proof-id}/skills")
    public SkillList getSkillsOfProof(@PathVariable("proof-id") long proofId) {
        log.info("@GetMapping(\"/skills\")");
        return serviceService.getListSkillsOfProof(proofId);
    }

    @Operation(
            summary = "Delete Skill",
            description = "Delete skill."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @DeleteMapping("/talents/{talent-id}/proofs/{proof-id}/skills/{skill-id}")
    public void deleteSkill(@PathVariable("talent-id") long talentId,
                            @PathVariable("proof-id") long proofId,
                            @PathVariable("skill-id") long skillId,
                            Authentication auth) {
        log.info("@DeleteMapping(\"/talents/{talent-id}/proofs/{proof-id}/skills/{skill-id}\")");
        serviceService.deleteSkill(talentId, proofId, skillId, auth);
    }

    @Operation(
            summary = "Delete Array Skill",
            description = "Delete skill array."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @DeleteMapping("/talents/{talent-id}/proofs/{proof-id}/skills")
    public void deleteSkillArray(@PathVariable("talent-id") long talentId,
                                 @PathVariable("proof-id") long proofId,
                                 @RequestBody DeleteIdSkills skillId,
                                 Authentication auth) {
        log.info("@DeleteMapping(\"/talents/{talent-id}/proofs/{proof-id}/skills\")");
        serviceService.deleteSkillArray(talentId, proofId, skillId, auth);
    }

    @Operation(
            summary = "Delete array of skills on talent account",
            description = "Delete array of skills on talent account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @DeleteMapping("talents/{talent-id}/skills")
    public void deleteSkills(@PathVariable("talent-id") long talentId,
                                @RequestBody DeleteIdSkills skillId,
                                Authentication auth) {
        log.info("@DeleteMapping(\"talents/{talent-id}/skills\")");
        serviceService.deleteSkills(talentId,skillId,auth);
    }
}
