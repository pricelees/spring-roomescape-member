package roomescape.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTheme;
import roomescape.domain.ReservationTime;

@Repository
public class ReservationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    public List<Reservation> findAll() {
        String findAllSql = """
                        SELECT 
                            r.id as reservation_id, 
                            r.name,     
                            r.date, 
                            t.id as time_id, 
                            t.start_at as time_value, 
                            tm.id as theme_id, 
                            tm.name as theme_name, 
                            tm.description,     
                            tm.thumbnail 
                        FROM reservation AS r 
                        INNER JOIN reservation_time AS t 
                        ON r.time_id = t.id 
                        INNER JOIN theme AS tm 
                        ON r.theme_id = tm.id
                """;
        return jdbcTemplate.query(findAllSql, getReservationRowMapper());
    }

    public Reservation insert(ReservationInsertCondition insertCondition) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", insertCondition.getName())
                .addValue("date", insertCondition.getDate().toString())
                .addValue("time_id", insertCondition.getTimeId())
                .addValue("theme_id", insertCondition.getThemeId());

        Long id = insertActor.executeAndReturnKey(parameters).longValue();

        return new Reservation(id, insertCondition.getName(), insertCondition.getDate(), insertCondition.getTime(),
                insertCondition.getTheme());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Reservation> findById(Long id) {
        String sql = """
                        SELECT 
                            r.id as reservation_id, 
                            r.name,     
                            r.date, 
                            t.id as time_id, 
                            t.start_at as time_value, 
                            tm.id as theme_id, 
                            tm.name as theme_name, 
                            tm.description,     
                            tm.thumbnail 
                        FROM reservation AS r 
                        INNER JOIN reservation_time AS t 
                        ON r.time_id = t.id 
                        INNER JOIN theme AS tm 
                        ON r.theme_id = tm.id
                        WHERE t.id = ?
                """;
        List<Reservation> reservations = jdbcTemplate.query(sql, getReservationRowMapper(), id);
        return Optional.ofNullable(DataAccessUtils.singleResult(reservations));
    }

    public Boolean hasTime(Long timeId) {
        String sql = "SELECT EXISTS(SELECT * FROM reservation WHERE time_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, timeId);
    }

    public Boolean hasTheme(Long themeId) {
        String sql = "SELECT EXISTS(SELECT * FROM reservation WHERE theme_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, themeId);
    }

    public int count(String date, Long timeId, Long themeId) {
        String sql = "SELECT count(*) FROM reservation WHERE time_id = ? AND theme_id = ? AND date = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, timeId, themeId, date);
    }

    public Boolean hasSameReservation(LocalDate date, Long timeId, Long themeId) {
        String sql = "SELECT EXISTS(SELECT * FROM reservation WHERE date = ? AND time_id = ? AND theme_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, date.toString(), timeId, themeId);
    }

    public List<Long> findBestThemeIdInWeek(String from, String to) {
        String sql = "SELECT theme_id, count(*) AS total FROM reservation WHERE date BETWEEN ? AND ? GROUP BY theme_id ORDER BY total DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("theme_id"), from, to);
    }

    private RowMapper<Reservation> getReservationRowMapper() {
        return (resultSet, numRow) -> new Reservation(
                resultSet.getLong("reservation_id"),
                resultSet.getString("name"),
                resultSet.getDate("date").toLocalDate(),
                new ReservationTime(
                        resultSet.getLong("time_id"),
                        resultSet.getTime("time_value").toLocalTime()
                ),
                new ReservationTheme(
                        resultSet.getLong("theme_id"),
                        resultSet.getString("theme_name"),
                        resultSet.getString("description"),
                        resultSet.getString("thumbnail")
                )
        );
    }
}
