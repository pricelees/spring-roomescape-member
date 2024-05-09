package roomescape.service;

import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.dao.ReservationDao;
import roomescape.dao.ReservationThemeDao;
import roomescape.dao.ReservationTimeDao;
import roomescape.dao.condition.TimeInsertCondition;
import roomescape.domain.ReservationTime;
import roomescape.dto.time.BookableTimeResponse;
import roomescape.dto.time.TimeRequest;
import roomescape.dto.time.TimeResponse;

@Service
public class ReservationTimeService {

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ReservationThemeDao reservationThemeDao;

    public ReservationTimeService(ReservationDao reservationDao, ReservationTimeDao reservationTimeDao,
                                  ReservationThemeDao reservationThemeDao) {
        this.reservationDao = reservationDao;
        this.reservationTimeDao = reservationTimeDao;
        this.reservationThemeDao = reservationThemeDao;
    }

    public TimeResponse insertTime(TimeRequest timeRequest) {
        validateDuplicate(timeRequest.startAt());
        TimeInsertCondition insertCondition = new TimeInsertCondition(timeRequest.startAt());
        ReservationTime inserted = reservationTimeDao.insert(insertCondition);

        return new TimeResponse(inserted);
    }

    private void validateDuplicate(LocalTime time) {
        if (reservationTimeDao.hasSameTime(time)) {
            throw new IllegalArgumentException("동일한 시간이 존재합니다.");
        }
    }

    public List<TimeResponse> getAllTimes() {
        return reservationTimeDao.findAll().stream()
                .map(TimeResponse::new)
                .toList();
    }

    public List<BookableTimeResponse> getAllBookableTimes(String date, Long themeId) {
        if (!reservationThemeDao.isExist(themeId)) {
            throw new IllegalArgumentException("해당 테마는 존재하지 않습니다.");
        }
        return reservationTimeDao.findAll().stream()
                .map(time -> new BookableTimeResponse(time, isBooked(date, time.getId(), themeId)))
                .toList();
    }

    private Boolean isBooked(String date, Long timeId, Long themeId) {
        return reservationDao.hasSameReservation(date, timeId, themeId);
    }

    public void deleteTime(Long id) {
        if (reservationDao.hasReservationForTimeId(id)) {
            throw new IllegalStateException("예약이 존재하는 시간은 삭제할 수 없습니다.");
        }
        reservationTimeDao.deleteById(id);
    }
}
