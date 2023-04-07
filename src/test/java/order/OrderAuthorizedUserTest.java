package order;

import User.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class OrderAuthorizedUserTest {
    private UserManager userManager;
    private OrderManager orderManager;
    private DataUser authorizedUser;
    private Tokens tokens;
    private CreateOrder order;
    private String ingredient1 = "61c0c5a71d1f82001bdaaa7a";
    private String ingredient2 = "61c0c5a71d1f82001bdaaa79";
    private String ingredient3 = "61c0c5a71d1f82001bdaaa73";

    @BeforeClass

    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured());
    }

    @Before
    public void setUp() {
        userManager = new UserManager();
        authorizedUser = GeneratorUser.getRandom();
        userManager.createUser(authorizedUser);
        orderManager = new OrderManager();
        order = new CreateOrder();
        tokens = userManager.login(LoginUser.from(authorizedUser))
                .extract().body()
                .as(Tokens.class);
    }

    @After
    public void removeUser() {
        if (!(tokens.getAccessToken() == null)) {
            userManager.removeUser(tokens.getAccessToken());
        }
    }
    @Test
    public void createOrderTest() {
        order.addIngredients(ingredient2);
        order.addIngredients(ingredient3);
        orderManager.createOrder(tokens.getAccessToken(), order)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("order", notNullValue());
    }
    @Test
    public void checkOrderIdTest() {
        order.addIngredients(ingredient1);
        order.addIngredients(ingredient2);
        order.addIngredients(ingredient3);
        orderManager.createOrder(tokens.getAccessToken(), order)
                .assertThat()
                .statusCode(200)
                .body("order.ingredients._id", hasItems(ingredient1,ingredient2,ingredient3));

    }
    @Test
    public void checkOrderStatusTest() {
        order.addIngredients(ingredient1);
        order.addIngredients(ingredient2);
        order.addIngredients(ingredient3);
        orderManager.createOrder(tokens.getAccessToken(), order)
                .assertThat()
                .statusCode(200)
                .body("order.status",equalTo("done"));

    }
    @Test
    public void checkOrderOwnerTest() {
        order.addIngredients(ingredient1);
        order.addIngredients(ingredient3);
        orderManager.createOrder(tokens.getAccessToken(), order)
                .assertThat()
                .statusCode(200)
                .body("order.owner.name",equalTo(authorizedUser.getName()))
                .body("order.owner.email",equalTo(authorizedUser.getEmail().toLowerCase()));

    }
    @Test
    public void getUserOrderTest() {
        order.addIngredients(ingredient1);
        orderManager.createOrder(tokens.getAccessToken(), order);
        orderManager.getUserOrders(tokens.getAccessToken())
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue());
    }
    @Test
    public void checkGetUserOrderStatusTest() {
        order.addIngredients(ingredient1);
        orderManager.createOrder(tokens.getAccessToken(), order);
        orderManager.getUserOrders(tokens.getAccessToken())
                .assertThat()
                .statusCode(200)
                .body("orders.status", hasItems("done"));
    }

        @Test
    public void createOrderWithIncorrectIngredientTest() {
        order.addIngredients(RandomStringUtils.randomAlphabetic(10));
        orderManager.createOrder(tokens.getAccessToken(), order)
                .assertThat()
                .statusCode(500);
    }

    @Test
    public void createOrderWithoutIngredientTest() {
        orderManager.createOrder(tokens.getAccessToken(), order)
                .assertThat()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}