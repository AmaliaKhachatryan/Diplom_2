package order;

import databaseuri.BaseURI;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderManager extends BaseURI {
    public ValidatableResponse createOrder(String token, CreateOrder order) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .body(order)
                .when()
                .post(USER_ORDER)
                .then();
    }

    public ValidatableResponse createOrderWithoutToken(CreateOrder order) {
        return given()
                .spec(getBaseReqSpec())
                .body(order)
                .when()
                .post(USER_ORDER)
                .then();
    }

    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .get(USER_ORDER)
                .then();
    }

    public ValidatableResponse getUserOrdersWithoutToken() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(USER_ORDER)
                .then();
    }

    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(GET_ORDERS)
                .then();
    }

    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }
}
