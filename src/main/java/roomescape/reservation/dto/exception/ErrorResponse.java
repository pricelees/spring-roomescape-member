package roomescape.reservation.dto.exception;

public record ErrorResponse(String url, String method, String message) {
}