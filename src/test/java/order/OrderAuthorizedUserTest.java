package order;

import jdk.jfr.Description;
import user.*;
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
    private Tokens token;
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
    @Description("Создать клиента")
    public void setUp() {
        userManager = new UserManager();
        authorizedUser = GeneratorUser.getRandom();
        userManager.createUser(authorizedUser);
        orderManager = new OrderManager();
        order = new CreateOrder();
        token = userManager.login(LoginUser.from(authorizedUser))
                .extract().body()
                .as(Tokens.class);
    }

    @After
    @Description("Удалить клиента")
    public void removeUser() {
        if (!(token.getAccessToken() == null)) {
            userManager.removeUser(token.getAccessToken());
        }
    }

    @Test
    @Description("Создать заказа авторизованным клиентом")
    public void createOrderTest() {
        order.addIngredients(ingredient1);
        order.addIngredients(ingredient2);
        order.addIngredients(ingredient3);
        orderManager.createOrder(token.getAccessToken(), order)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("order", notNullValue())
                .body("order.ingredients._id", hasItems(ingredient1, ingredient2, ingredient3))
                .body("order.status", equalTo("done"))
                .body("order.owner.name", equalTo(authorizedUser.getName()))
                .body("order.owner.email", equalTo(authorizedUser.getEmail().toLowerCase()));
    }

    @Test
    @Description("Получить данные заказа авторизованного клиента")
    public void getUserOrderTest() {
        order.addIngredients(ingredient1);
        orderManager.createOrder(token.getAccessToken(), order);
        orderManager.getUserOrders(token.getAccessToken())
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue())
                .body("orders.status", hasItems("done"));
    }

    @Test
    @Description("Создать заказ с невалидным ингредиентом авторизованным клиентом.")
    public void createOrderWithIncorrectIngredientByUserTest() {
        order.addIngredients(RandomStringUtils.randomAlphabetic(10));
        orderManager.createOrder(token.getAccessToken(), order)
                .assertThat()
                .statusCode(500);
    }

    @Test
    @Description("Создать заказ без ингредиента авторизованным клиентом.")
    public void createOrderWithoutIngredientByUserTest() {
        orderManager.createOrder(token.getAccessToken(), order)
                .assertThat()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}