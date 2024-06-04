package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class NotificationStatusesDTO {
    private boolean reservationRequestedNotificationsActive;
    private boolean reservationCancelledNotificationsActive;
    private boolean hostRatedNotificationsActive;
    private boolean accommodationRatedNotificationsActive;
    private boolean reservationRequestAnsweredActive;

    public NotificationStatusesDTO() {

    }

    public NotificationStatusesDTO(boolean reservationRequestedNotificationsActive,
                                   boolean reservationCancelledNotificationsActive,
                                   boolean hostRatedNotificationsActive,
                                   boolean accommodationRatedNotificationsActive,
                                   boolean reservationRequestAnsweredActive) {
        this.reservationRequestedNotificationsActive = reservationRequestedNotificationsActive;
        this.reservationCancelledNotificationsActive = reservationCancelledNotificationsActive;
        this.hostRatedNotificationsActive = hostRatedNotificationsActive;
        this.accommodationRatedNotificationsActive = accommodationRatedNotificationsActive;
        this.reservationRequestAnsweredActive = reservationRequestAnsweredActive;
    }
}
