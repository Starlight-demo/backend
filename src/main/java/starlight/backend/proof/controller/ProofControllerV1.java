package starlight.backend.proof.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import starlight.backend.proof.model.request.ProofAddRequest;
import starlight.backend.proof.model.request.ProofUpdateRequest;
import starlight.backend.proof.model.response.ProofFullInfo;
import starlight.backend.proof.model.response.ProofPagePagination;
import starlight.backend.proof.service.ProofServiceInterface;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v1")
@Tag(name = "Proof", description = "Proof API")
public class ProofControllerV1 {
    private ProofServiceInterface proofService;

    @Operation(
            summary = "Get all proofs",
            description = "Get list of all proofs. The response is list of talent objects with fields 'id','title', 'description' and 'dateCreated'."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofPagePagination.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/proofs")
    public ProofPagePagination pagination(@RequestParam(defaultValue = "0") @Min(0) int page,
                                          @RequestParam(defaultValue = "5") @Positive int size,
                                          @RequestParam(defaultValue = "true") boolean sort) {
        log.info("@GetMapping(\"/proofs\")");
        return proofService.proofsPagination(page, size, sort);
    }

    @Operation(
            summary = "Add proof in status draft",
            description = "Adding a proof for a specific talent, issuing a link in Location to a page with a full description of the proof."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ResponseEntity.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })

    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/talents/{talent-id}/proofs")
    public ResponseEntity<?> addProofFullInfo(@PathVariable("talent-id") long talentId,
                                              @RequestBody ProofAddRequest proofAddRequest,
                                              Authentication auth) {
        log.info("@PostMapping(\"/talents/{talent-id}/proofs\")");
        return proofService.getLocation(talentId, proofAddRequest, auth);
    }

    @Operation(summary = "Delete proof by proof_id and talent_id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    @DeleteMapping("/talents/{talent-id}/proofs/{proof-id}")
    public void deleteTalent(@PathVariable("talent-id") long talentId,
                             @PathVariable("proof-id") long proofId,
                             Authentication auth) {
        log.info("@DeleteMapping(\"/talents/{talent-id}/proofs/{proof-id}\")");
        proofService.deleteProof(talentId, proofId, auth);
    }

    @Operation(
            summary = "Update proof in status draft",
            description = "Update proof args title, description, link." +
                    "It is possible to change general information in Proofs with " +
                    "the \"DRAFT\" status. Changing Proof status from Draft to another " +
                    "is only possible on PUBLISHED or HIDDEN. It is not possible to " +
                    "return from the \"PUBLISHED\" or \"HIDDEN\" status to the \"DRAFT\" status.\n" +
                    "Evidence with status \"DRAFT\" or \"HIDDEN\" is only listed in your own profile."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofFullInfo.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PatchMapping("/talents/{talent-id}/proofs/{proof-id}")
    @PreAuthorize("hasRole('TALENT') or hasRole('ADMIN')")
    public ProofFullInfo updateProofFullInfo(@PathVariable("talent-id") long talentId,
                                             @PathVariable("proof-id") long proofId,
                                             @RequestBody ProofUpdateRequest proofUpdateRequest,
                                             Authentication auth) {
        log.info("@PatchMapping(\"/talents/{talent-id}/proofs/{proof-id}\")");
        return proofService.proofUpdateRequest(talentId, proofId, proofUpdateRequest, auth);
    }

    @Operation(summary = "Return list of all proofs for talent by talent_id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofPagePagination.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/talents/{talent-id}/proofs")
    public ProofPagePagination getTalentProofs(@PathVariable("talent-id") long talentId,
                                               Authentication auth,
                                               @RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "5") @Positive int size,
                                               @RequestParam(defaultValue = "true") boolean sort,
                                               @RequestParam(defaultValue = "ALL") String status) {
        log.info("@GetMapping(\"/talents/{talent-id}/proofs\")");
        return proofService.getTalentAllProofs(auth, talentId, page, size, sort, status);
    }

    @Operation(summary = "Return Proof information for an authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofFullInfo.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/proofs/{proof-id}")
    public ProofFullInfo getFullProof(@PathVariable("proof-id") long proofId,
                                      Authentication auth) {
        log.info("@GetMapping(\"/proofs/{proof-id}\")");
        return proofService.getProofFullInfo(auth, proofId);
    }
}
