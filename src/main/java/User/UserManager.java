package User;

import databaseuri.BaseURI;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserManager extends BaseURI {

    public ValidatableResponse createUser(DataUser user) {

        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(CREATE_USER)
                .then();
    }

    public ValidatableResponse login(LoginUser userLogin) {
        return given()
                .spec(getBaseReqSpec())
                .body(userLogin)
                .when()
                .post(USER_LOGIN)
                .then();
    }

    public ValidatableResponse changeDataUser(String token, DataUser user) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .and()
                .body(user)
                .when()
                .patch(USER_ACTIONS)
                .then();
    }
    public ValidatableResponse changeDataUserWithoutToken(DataUser user) {
        return given()
                .spec(getBaseReqSpec())
                .and()
                .body(user)
                .when()
                .patch(USER_ACTIONS)
                .then();
    }

    public ValidatableResponse getInfoUser(String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .get(USER_ACTIONS)
                .then();
    }

    public ValidatableResponse logoutUser(Logout token) {
        return given()
                .spec(getBaseReqSpec())
                .body(token)
                .when()
                .post(USER_LOGOUT)
                .then();
    }

    public ValidatableResponse removeUser(String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .delete(USER_ACTIONS)
                .then();
    }
}


