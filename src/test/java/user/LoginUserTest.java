package user;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import jdk.jfr.Description;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {
    private DataUser user;
    private LoginUser loginUser;
    private UserManager manager;
    private Tokens token;
    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured());
    }

    @Before
    @Description("Создать клиента")
    public void setUp() {
        manager = new UserManager();
        user = GeneratorUser.getRandom();
        manager.createUser(user);
    }

    @After
    @Description("Удалить клиента.")
    public void removeUser() {
        token = manager.login(LoginUser.from(user))
                .extract().body()
                .as(Tokens.class);
        if (!(token.getAccessToken() == null)) {
            manager.removeUser(token.getAccessToken());
        }
    }

    @Test
    @Description("Успешная авторизация клиента.")
    public void loginUserTest() {
        manager.login(LoginUser.from(user))
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue())
                .body("user.email",equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @Description("Авторизация клиента с неверным данными.")
    public void loginWithIncorrectEmailAndPasswordTest() {
        loginUser = new LoginUser(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru",
                RandomStringUtils.randomAlphabetic(10));
        manager.login(loginUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @Description("Авторизация клиента с неверным данными.")
    public void loginWithIncorrectEmailAndPassword1Test() {
        loginUser = new LoginUser(user.getEmail(),
                RandomStringUtils.randomAlphabetic(10));
        manager.login(loginUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @Description("Авторизация клиента с неверным Email .")
    public void loginWithIncorrectEmailTest() {
        loginUser = new LoginUser(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru", user.getPassword());
        manager.login(loginUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @Description("Авторизация клиента  под существующим Email.")
    public void loginWithAlreadyExistEmailTest() {
        loginUser = new LoginUser(user.getEmail(), RandomStringUtils.randomAlphabetic(10));
        manager.login(loginUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @Description("Выход клиента из личного кабинета.")
    public void logoutUserTest() {
        token = manager.login(LoginUser.from(user))
                .extract().body()
                .as(Tokens.class);
        Logout logout=new Logout(token.getRefreshToken());
        manager.logoutUser(logout)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("message", equalTo("Successful logout"));
    }
}
