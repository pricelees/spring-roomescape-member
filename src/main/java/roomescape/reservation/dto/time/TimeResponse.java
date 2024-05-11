package roomescape.reservation.dto.time;

import java.time.format.DateTimeFormatter;
import roomescape.reservation.domain.ReservationTime;

public record TimeResponse(Long id, String startAt) {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TimeResponse(ReservationTime time) {
        this(time.getId(), time.getStartAt().format(TIME_FORMATTER));
    }
}