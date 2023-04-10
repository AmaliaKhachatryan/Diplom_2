package order;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import jdk.jfr.Description;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class OrderNotAuthorizedUserTest {
    private OrderManager orderManager;
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
        orderManager = new OrderManager();
        order = new CreateOrder();
    }
    @Test
    @Description("Создать заказа неавторизованным клиентом")
    public void createOrderTest() {
        order.addIngredients(ingredient1);
        order.addIngredients(ingredient2);
        order.addIngredients(ingredient3);
        orderManager.createOrderWithoutToken(order)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("order", notNullValue())
                .body("order.number", notNullValue());
    }
    @Test
    @Description("Получить данные заказа неавторизованного клиента")
    public void getUserOrdersWithoutTokenTest() {
        order.addIngredients(ingredient1);
        orderManager.getUserOrdersWithoutToken()
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
   }
    @Test
    @Description("Создать заказ с невалидным ингредиентом неавторизованным клиентом.")
    public void createOrderWithIncorrectIngredientTest() {
        order.addIngredients(RandomStringUtils.randomAlphabetic(10));
        orderManager.createOrderWithoutToken(order)
                .assertThat()
                .statusCode(500);
    }
    @Test
    @Description("Создать заказ без ингредиента неавторизованным клиентом.")
    public void createOrderWithoutIngredientTest() {
        orderManager.createOrderWithoutToken(order)
                .assertThat()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}
