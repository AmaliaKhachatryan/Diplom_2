package user;

import databaseuri.BaseURI;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserManager extends BaseURI {
    @Step("Создать клиента")
    public ValidatableResponse createUser(DataUser user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(CREATE_USER)
                .then();
    }
    @Step("Авторизация клиента")
    public ValidatableResponse login(LoginUser userLogin) {
        return given()
                .spec(getBaseReqSpec())
                .body(userLogin)
                .when()
                .post(USER_LOGIN)
                .then();
    }
    @Step("Изменить все данные авторизованного клиента")
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
    @Step("Изменить все данные неавторизованного клиента")
    public ValidatableResponse changeDataUserWithoutToken(DataUser user) {
        return given()
                .spec(getBaseReqSpec())
                .and()
                .body(user)
                .when()
                .patch(USER_ACTIONS)
                .then();
    }
    @Step("Получить данные авторизованного клиента")
    public ValidatableResponse getInfoUser(String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .get(USER_ACTIONS)
                .then();
    }
    @Step("Выход авторизованного клиента")
    public ValidatableResponse logoutUser(Logout token) {
        return given()
                .spec(getBaseReqSpec())
                .body(token)
                .when()
                .post(USER_LOGOUT)
                .then();
    }
    @Step("Удаление авторизованного клиента")
    public ValidatableResponse removeUser(String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .delete(USER_ACTIONS)
                .then();
    }
}


