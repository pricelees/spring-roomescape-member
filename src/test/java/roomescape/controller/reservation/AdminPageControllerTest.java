package roomescape.controller.reservation;

import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/test_schema.sql", "/test_admin_member.sql"})
public class AdminPageControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("어드민 유저가 아니라면 로그인 페이지로 이동한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/admin", "/admin/reservation", "/admin/time", "/admin/theme"})
    void pageWithNotAdminMember(String url) {
        RestAssured.given().log().all()
                .when().get(url)
                .then().log().all()
                .statusCode(200)
                .body(containsString("<title>Login</title>"));
    }

    @DisplayName("어드민 유저가 접속하는 경우 정상적으로 응답한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/admin", "/admin/reservation", "/admin/time", "/admin/theme"})
    void pageWithAdminMember(String url) {
        Map<String, Object> param = Map.of(
                "email", "admin@email.com",
                "password", "admin_password"
        );

        String accessToken = RestAssured.given().log().all()
                .contentType("application/json")
                .body(param)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract().cookie("token");

        RestAssured.given().log().all()
                .cookie("token", accessToken)
                .when().get(url)
                .then().log().all()
                .statusCode(200)
                .body(containsString("<title>방탈출 어드민</title>"));
    }
}