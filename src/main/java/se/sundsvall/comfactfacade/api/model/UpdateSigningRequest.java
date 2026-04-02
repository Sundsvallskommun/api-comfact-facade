package se.sundsvall.comfactfacade.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.comfactfacade.api.validation.ValidStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Request for updating a signing instance.")
public class UpdateSigningRequest {

	@Schema(description = "The date and time when the signing request expires.", examples = "2026-11-22T15:30:00+02:00")
	private OffsetDateTime expires;

	@ValidStatus
	@Schema(description = "The status of the signing instance.", examples = {
		"active", "withdrawn"
	})
	private String status;
}
