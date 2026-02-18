package se.sundsvall.comfactfacade.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@Builder(setterPrefix = "with")
@Schema(description = "Signing response.")
@NoArgsConstructor
@AllArgsConstructor
public class SigningsResponse {

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData pagingAndSortingMetaData;

	@Schema(description = "The signing instances.")
	private List<SigningInstance> signingInstances;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final SigningsResponse that = (SigningsResponse) o;
		return Objects.equals(pagingAndSortingMetaData, that.pagingAndSortingMetaData) && Objects.equals(signingInstances, that.signingInstances);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pagingAndSortingMetaData, signingInstances);
	}

	@Override
	public String toString() {
		return "SigningsResponse{" +
			"pagingAndSortingMetaData=" + pagingAndSortingMetaData +
			", signingInstances=" + signingInstances +
			'}';
	}

}
