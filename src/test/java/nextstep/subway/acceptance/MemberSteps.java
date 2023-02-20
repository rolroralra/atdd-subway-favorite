package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class MemberSteps {

    public static ExtractableResponse<Response> 베어러_인증_로그인_요청(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();
    }

    public static ExtractableResponse<Response> 회원_생성_요청(String email, String password, Integer age) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("age", age + "");

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/members")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("age", age + "");

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(uri)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 베이직_인증으로_내_회원_정보_조회_요청(String username, String password) {
        return RestAssured.given().log().all()
                .auth().preemptive().basic(username, password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/members/me")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        assertThat(response.jsonPath().getString("id")).isNotNull();
        assertThat(response.jsonPath().getString("email")).isEqualTo(email);
        assertThat(response.jsonPath().getInt("age")).isEqualTo(age);
    }

    public static ExtractableResponse<Response> JWT_토큰으로_내_회원_정보_조회_요청(String username, String password) {
        String accessToken = 베어러_인증_로그인_요청(username, password).jsonPath().getString("accessToken");
        return RestAssured.given().log().all()
            .header(new Header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .when().get("/members/me")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public static ExtractableResponse<Response> Github의_access_token을_요청(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);

        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(params)
            .when().post("/login/github")
            .then().log().all()
            .extract();
    }

    public static void 응답_코드가_일치한지_확인(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    public static void 응답에서_id_정보_있는지_확인(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getLong("id")).isNotNull();
    }

    public static void 응답에서_email_정보_확인(ExtractableResponse<Response> response, String email) {
        assertThat(response.jsonPath().getString("email")).isEqualTo(email);
    }

    public static void 응답에서_나이_정보_확인(ExtractableResponse<Response> response, int age) {
        assertThat(response.jsonPath().getLong("age")).isNotNull().isEqualTo(age);
    }

    public static void 응답에서_access_token_존재_여부_확인(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("accessToken")).isNotBlank();
    }
    public static void 응답에서_access_token_일치_여부_확인(ExtractableResponse<Response> response, String accessToken) {
        assertThat(response.jsonPath().getString("accessToken")).isEqualTo(accessToken);
    }
}
