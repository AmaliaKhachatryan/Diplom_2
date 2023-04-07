package User;

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

public class LoginUserTest {
    private DataUser user;
    private LoginUser loginUser;
    private UserManager manager;
    private Tokens tokens;

    @BeforeClass

    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured());
    }

    @Before
    public void setUp() {
        manager = new UserManager();
        user = GeneratorUser.getRandom();
        manager.createUser(user);
    }

    @After
    public void removeUser() {
        tokens = manager.login(LoginUser.from(user))
                .extract().body()
                .as(Tokens.class);
        if (!(tokens.getAccessToken() == null)) {
            manager.removeUser(tokens.getAccessToken());
        }
    }

    @Test
    public void loginUserTest() {
        manager.login(LoginUser.from(user))
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .and()
                .body("user", notNullValue());
    }

    @Test
    public void loginUserEmailTest() {
        manager.login(LoginUser.from(user))
                .assertThat()
                .statusCode(200)
                .body("user.email",equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    public void loginUserNameTest() {
        manager.login(LoginUser.from(user))
                .assertThat()
                .statusCode(200)
                .body("user.name", equalTo(user.getName()));
    }

    @Test
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
    public void loginWithIncorrectPasswordTest() {
        loginUser = new LoginUser(user.getEmail(), RandomStringUtils.randomAlphabetic(10));
        manager.login(loginUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));

    }
}
