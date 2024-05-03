package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Reservation;
import roomescape.dto.reservation.ReservationRequestDto;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/test_schema.sql", "/test_data.sql"})
public class ReservationServiceTest {

    @Autowired
    private final ReservationService reservationService;

    @Autowired
    public ReservationServiceTest(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @DisplayName("모든 예약을 조회한다.")
    @Test
    void getAllReservationsTest() {
        List<Reservation> reservations = reservationService.getAllReservations();

        assertThat(reservations.size()).isEqualTo(1);
    }

    @DisplayName("예약을 추가한다.")
    @Test
    void insertReservationTest() {
        ReservationRequestDto reservationRequestDto = new ReservationRequestDto("test", LocalDate.now().plusDays(1), 1L, 1L);
        Reservation reservation = reservationService.insertReservation(reservationRequestDto);

        assertThat(reservation.getId()).isEqualTo(2L);
        assertThat(reservation.getName()).isEqualTo(reservationRequestDto.name());
        assertThat(reservation.getDate()).isEqualTo(reservationRequestDto.date().toString());
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void deleteReservationTest() {
        int sizeBeforeDelete = reservationService.getAllReservations().size();
        assertThatCode(() -> reservationService.deleteReservation(1L)).doesNotThrowAnyException();
        assertThat(reservationService.getAllReservations().size()).isEqualTo(sizeBeforeDelete - 1);
    }

    @DisplayName("현재 시간 이전의 시간으로 예약할 수 없다.")
    @Test
    void invalidDateTimeTest() {
        LocalDate localDate = LocalDate.now().minusDays(2);

        ReservationRequestDto reservationRequestDto = new ReservationRequestDto("test", localDate, 1L, 1L);

        assertThatThrownBy(() -> reservationService.insertReservation(reservationRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지나간 날짜와 시간에 대한 예약 생성은 불가능합니다.");
    }

    @DisplayName("같은 날짜, 시간, 테마에 대한 중복 예약은 불가능하다.")
    @Test
    void duplicatedReservationTest() {
        ReservationRequestDto reservationRequestDto = new ReservationRequestDto("test", LocalDate.now().plusDays(2), 1L, 1L);

        reservationService.insertReservation(reservationRequestDto);

        assertThatThrownBy(() -> reservationService.insertReservation(reservationRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 시간에 예약이 존재합니다.");
    }
}
