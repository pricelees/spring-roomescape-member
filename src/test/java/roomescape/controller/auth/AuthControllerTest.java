package roomescape.controller.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.member.MemberController;
import roomescape.dto.auth.LoginRequest;
import roomescape.dto.member.SignupRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/test_schema.sql"})
class AuthControllerTest {

    private static final String DEFAULT_EMAIL = "email@email.com";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_NAME = "name";

    @Autowired
    private MemberController memberController;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("로그인 성공시 200으로 응답한다.")
    @Test
    void login() {
        login(DEFAULT_EMAIL, DEFAULT_PASSWORD).statusCode(200);
    }

    @DisplayName("이메일이 존재하지 않으면 400으로 응답한다.")
    @Test
    void loginWithWrongEmail() {
        login("wrongEmail@email.com", DEFAULT_PASSWORD)
                .statusCode(400)
                .body("message", is("아이디, 비밀번호를 확인해주세요."));
    }

    @DisplayName("이메일 형식이 아닌 입력에 대해 400으로 응답한다.")
    @Test
    void loginWithWrongEmailFormat() {
        login("wrongEmail", DEFAULT_PASSWORD)
                .statusCode(400)
                .body("message", is("형식이 올바르지 않습니다."));
    }

    @DisplayName("비밀번호가 틀리면 400으로 응답한다.")
    @Test
    void loginWithWrongPassword() {
        login(DEFAULT_EMAIL, "wrongPassword")
                .statusCode(400)
                .body("message", is("아이디, 비밀번호를 확인해주세요."));
    }

    @DisplayName("비밀번호를 입력하지 않으면 400으로 응답한다.")
    @Test
    void loginWithEmptyPassword() {
        login(DEFAULT_EMAIL, "")
                .statusCode(400)
                .body("message", is("비밀번호가 입력되지 않았습니다."));
    }

    @DisplayName("로그인한 사용자 정보를 조회한다.")
    @Test
    void loginCheck() {
        // given
        String accessToken = login(DEFAULT_EMAIL, DEFAULT_PASSWORD)
                .extract()
                .cookie("token");

        // when & then
        RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .cookie("token", accessToken)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .body("name", is(DEFAULT_NAME));
    }

    @DisplayName("로그인 상태가 아니라면 400을 응답한다.")
    @Test
    void logoutCheck() {
        RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("로그아웃 하면 토큰 값이 사라진다.")
    @Test
    void logout() {
        // given
        String accessToken = login(DEFAULT_EMAIL, DEFAULT_PASSWORD)
                .extract().cookie("token");

        // when
        String tokenAfterLogout = RestAssured.given().log().all()
                .cookie("token", accessToken)
                .when().post("/logout")
                .then().log().all()
                .statusCode(200)
                .extract().cookie("token");

        // then
        assertThat(tokenAfterLogout).isBlank();
    }

    private ValidatableResponse login(String email, String password) {
        // 기본 멤버 생성
        SignupRequest defaultMember = new SignupRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NAME);
        memberController.createMember(defaultMember);

        return RestAssured.given().log().all()
                .contentType("application/json")
                .body(new LoginRequest(email, password))
                .when().post("/login")
                .then().log().all();
    }
}