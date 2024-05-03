package roomescape.dto.reservation;

import java.time.LocalDate;

public record ReservationRequestDto(String name, LocalDate date, Long timeId, Long themeId) {

    public ReservationRequestDto {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력하여야 합니다.");
        }
    }
}
