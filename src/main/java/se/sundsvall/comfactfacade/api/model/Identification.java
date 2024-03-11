package se.sundsvall.comfactfacade.api.model;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The means of identification to use to identify the signatory.")
public record Identification(
	@NotBlank
	@Schema(description = """ 
		Possible values for Alias type:
		• SmsCode – One Time Password via SMS
		• EmailCode – One Time Password via Email
		• SvensktEId – Swedish e-identification (Swedish BankID)
		""", example = "SmsCode") String alias
) {

}
