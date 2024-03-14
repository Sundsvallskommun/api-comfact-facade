package se.sundsvall.comfactfacade.api.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "A reminder for a signature request.")
public class Reminder {

	@Schema(description = "The message of the reminder")
	private NotificationMessage message;

	@Schema(description = "If the reminder is enabled", example = "true")
	private boolean enabled;

	@Schema(description = "The interval in hours between each reminder", example = "24")
	private int intervalInHours;

	@Schema(description = "The date and time when the first reminder should be sent.", example = "2021-12-31T23:59:59Z")
	private OffsetDateTime startDateTime;

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Reminder reminder = (Reminder) o;
		return enabled == reminder.enabled && intervalInHours == reminder.intervalInHours && Objects.equals(message, reminder.message) && Objects.equals(startDateTime, reminder.startDateTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, enabled, intervalInHours, startDateTime);
	}

	@Override
	public String toString() {
		return "Reminder{" +
			"message=" + message +
			", enabled=" + enabled +
			", intervalInHours=" + intervalInHours +
			", startDateTime=" + startDateTime +
			'}';
	}

}
