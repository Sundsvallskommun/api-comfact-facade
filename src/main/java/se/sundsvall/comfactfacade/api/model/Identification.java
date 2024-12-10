package se.sundsvall.comfactfacade.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "The means of identification to use to identify the signatories.")
public class Identification {

	@OneOf({
		"SmsCode", "EmailCode", "SvensktEId"
	})
	@Schema(description = """
		Possible values for Alias type:
		• SmsCode – One Time Password via SMS
		• EmailCode – One Time Password via Email
		• SvensktEId – Swedish e-identification (Swedish BankID)
		""", example = "SmsCode")
	private String alias;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Identification that = (Identification) o;
		return Objects.equals(alias, that.alias);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias);
	}

	@Override
	public String toString() {
		return "Identification{" +
			"alias='" + alias + '\'' +
			'}';
	}

}
