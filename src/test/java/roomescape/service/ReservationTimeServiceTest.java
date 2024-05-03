package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.ReservationTime;
import roomescape.dto.time.TimeRequest;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"/test_schema.sql", "/test_data.sql"})
public class ReservationTimeServiceTest {

    @Autowired
    private final ReservationTimeService reservationTimeService;

    @Autowired
    public ReservationTimeServiceTest(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @DisplayName("모든 예약 시간을 조회한다.")
    @Test
    void getAllReservationTimesTest() {
        List<ReservationTime> reservationTimes = reservationTimeService.getAllReservationTimes();

        assertThat(reservationTimes.size()).isEqualTo(1);
    }

    @DisplayName("예약 시간을 추가한다.")
    @Test
    void insertReservationTimeTest() {
        TimeRequest timeRequest = new TimeRequest(LocalTime.parse("01:01"));
        ReservationTime reservationTime = reservationTimeService.insertReservationTime(timeRequest);

        assertThat(reservationTime.getStartAt()).isEqualTo("01:01");
    }

    @DisplayName("예약 시간 ID를 이용하여 시간을 삭제한다.")
    @Test
    void deleteReservationTimeTest() {
        TimeRequest timeRequest = new TimeRequest(LocalTime.parse("01:01"));
        ReservationTime reservationTime = reservationTimeService.insertReservationTime(timeRequest);
        int sizeBeforeDelete = reservationTimeService.getAllReservationTimes().size();
        assertThatCode(() -> reservationTimeService.deleteReservationTime(reservationTime.getId())).doesNotThrowAnyException();
        assertThat(reservationTimeService.getAllReservationTimes().size()).isEqualTo(sizeBeforeDelete - 1);
    }

    @DisplayName("예약이 존재하는 시간은 삭제할 수 없다.")
    @Test
    void deleteInvalidTimeIdTest() {
        assertThatThrownBy(() -> reservationTimeService.deleteReservationTime(1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("예약이 가능한 시간인지 확인한다.")
    @Test
    void isBookedTest() {
        boolean actualIsBooked = reservationTimeService.isBooked("2024-01-01", 1L, 1L);

        assertThat(actualIsBooked).isEqualTo(true);
    }
}
