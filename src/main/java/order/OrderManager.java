package order;

import databaseuri.BaseURI;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderManager extends BaseURI {
    @Step("Cоздание заказа авторизованным клиентом")
    public ValidatableResponse createOrder(String token, CreateOrder order) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .body(order)
                .when()
                .post(USER_ORDER)
                .then();
    }
    @Step("Cоздание заказа неавторизованным клиентом")
    public ValidatableResponse createOrderWithoutToken(CreateOrder order) {
        return given()
                .spec(getBaseReqSpec())
                .body(order)
                .when()
                .post(USER_ORDER)
                .then();
    }
    @Step("Получить данные заказа авторизованным клиента")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .get(USER_ORDER)
                .then();
    }
    @Step("Получить данные заказа неавторизованным клиента")
    public ValidatableResponse getUserOrdersWithoutToken() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(USER_ORDER)
                .then();
    }
    @Step("Получить данные заказов")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(GET_ORDERS)
                .then();
    }
    @Step("Получить данные ингредиентов")
    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }
}
