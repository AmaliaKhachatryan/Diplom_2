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
public class ChangeDataUserTest {
    UserManager manager;
    DataUser authorizedUser;
    Tokens token;
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
        authorizedUser = GeneratorUser.getRandom();
        manager.createUser(authorizedUser);
        token = manager.login(LoginUser.from(authorizedUser))
                .extract()
                .body()
                .as(Tokens.class);
    }

    @After
    @Description("Удалить клиента")
    public void removeUser() {
        token = manager.login(LoginUser.from(authorizedUser))
                .extract().body()
                .as(Tokens.class);
        if (!(token.getAccessToken() == null)) {
            manager.removeUser(token.getAccessToken());
        }
    }

    @Test
    @Description("Изменить все данные авторизованного клиента.")
    public void changeAllDataUserTest() {
        authorizedUser = GeneratorUser.getRandom();
        manager.changeDataUser(token.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue());
    }

    @Test
    @Description("Изменить имя авторизованного клиента.")
    public void changeNameUserTest() {
        authorizedUser = new DataUser(authorizedUser.getEmail(), authorizedUser.getPassword(),
                RandomStringUtils.randomAlphabetic(10));
        manager.changeDataUser(token.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue())
                .body("user.name", equalTo(authorizedUser.getName()));
    }
    @Test
    @Description("Изменить email авторизованного клиента.")
    public void changeEmailUserTest() {
        authorizedUser = new DataUser(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru",
                authorizedUser.getPassword(),
                authorizedUser.getName());
        manager.changeDataUser(token.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue())
                .body("user.email", equalTo(authorizedUser.getEmail().toLowerCase()));
    }
    @Test
    @Description("Изменить  email авторизованного клиента существующим email-ом.")
    public void changeEmailAlreadyExistUserTest() {
        DataUser newUser = GeneratorUser.getRandom();
        manager.createUser(newUser);
        manager.changeDataUser(token.getAccessToken(), newUser)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User with such email already exists"));
        token = manager.login(LoginUser.from(newUser))
                .extract().body()
                .as(Tokens.class);
        if (!(token.getAccessToken() == null)) {
            manager.removeUser(token.getAccessToken());
        }

    }
    @Test
    @Description("Изменить пароль авторизованного клиента.")
    public void changePasswordUserTest() {
        authorizedUser = new DataUser(authorizedUser.getEmail(),
                RandomStringUtils.randomAlphabetic(10),
                authorizedUser.getName());
        manager.changeDataUser(token.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue());
    }

    @Test
    @Description("Изменить все данные неавторизованного клиента.")
    public void changeDataUserWithoutTokenTest() {
        manager.changeDataUserWithoutToken(authorizedUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
