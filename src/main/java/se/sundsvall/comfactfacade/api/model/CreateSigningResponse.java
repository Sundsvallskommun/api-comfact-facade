package se.sundsvall.comfactfacade.api.model;

import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class CreateSigningResponse {

	private String signingId;

	private Map<String, String> signatoryUrls;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final CreateSigningResponse that = (CreateSigningResponse) o;
		return Objects.equals(signingId, that.signingId) && Objects.equals(signatoryUrls, that.signatoryUrls);
	}

	@Override
	public int hashCode() {
		return Objects.hash(signingId, signatoryUrls);
	}

	@Override
	public String toString() {
		return "CreateSigningResponse{" +
			"signingId='" + signingId + '\'' +
			", signatoryUrls=" + signatoryUrls +
			'}';
	}

}
