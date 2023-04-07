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
public class ChangeDataUserTest {
    UserManager manager;
    DataUser authorizedUser;
    Tokens tokens;

    @BeforeClass

    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured());
    }

    @Before
    public void setUp() {
        manager = new UserManager();
        authorizedUser = GeneratorUser.getRandom();
        manager.createUser(authorizedUser);
        tokens = manager.login(LoginUser.from(authorizedUser))
                .extract()
                .body()
                .as(Tokens.class);
    }

    @After
    public void removeUser() {
        tokens = manager.login(LoginUser.from(authorizedUser))
                .extract().body()
                .as(Tokens.class);
        if (!(tokens.getAccessToken() == null)) {
            manager.removeUser(tokens.getAccessToken());
        }
    }

    @Test
    public void changeAllDataUserTest() {
        authorizedUser = GeneratorUser.getRandom();
        manager.changeDataUser(tokens.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .and()
                .body("user", notNullValue());
    }

    @Test
    public void changeNameUserTest() {
        authorizedUser = new DataUser(authorizedUser.getEmail(), authorizedUser.getPassword(),
                RandomStringUtils.randomAlphabetic(10));
        manager.changeDataUser(tokens.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .and()
                .body("user", notNullValue());
    }

    @Test
    public void checkNameUserTest() {
        authorizedUser = new DataUser(authorizedUser.getEmail(), authorizedUser.getPassword(),
                RandomStringUtils.randomAlphabetic(10));
        manager.changeDataUser(tokens.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .and()
                .body("user.name", equalTo(authorizedUser.getName()));
    }

    @Test
    public void changeEmailUserTest() {
        authorizedUser = new DataUser(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru",
                authorizedUser.getPassword(),
                authorizedUser.getName());
        manager.changeDataUser(tokens.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue());
    }

    @Test
    public void checkEmailUserTest() {
        authorizedUser = new DataUser(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru",
                authorizedUser.getPassword(),
                authorizedUser.getName());
        manager.changeDataUser(tokens.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(authorizedUser.getEmail().toLowerCase()));
    }

    @Test
    public void changeEmailAlreadyExistUserTest() {
        DataUser newUser = GeneratorUser.getRandom();
        manager.createUser(newUser);
        manager.changeDataUser(tokens.getAccessToken(), newUser)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User with such email already exists"));
        tokens = manager.login(LoginUser.from(newUser))
                .extract().body()
                .as(Tokens.class);
        if (!(tokens.getAccessToken() == null)) {
            manager.removeUser(tokens.getAccessToken());
        }

    }
    @Test
    public void changePasswordUserTest() {
        authorizedUser = new DataUser(authorizedUser.getEmail(),
                RandomStringUtils.randomAlphabetic(10),
                authorizedUser.getName());
        manager.changeDataUser(tokens.getAccessToken(), authorizedUser)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue());
    }

    @Test
    public void changeDataUserWithoutTokenTest() {
        manager.changeDataUserWithoutToken(authorizedUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
