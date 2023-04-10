package user;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class CreateUserTest {
    private UserManager manager;
    private Tokens token;
    private DataUser user;
    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured());
    }

    @Before
    public void setUp() {
        manager = new UserManager();
    }

    @After
    @Description("Удалить клиента")
    public void removeUser() {
        token = manager.login(LoginUser.from(user))
                .extract().body()
                .as(Tokens.class);
        if (!(token.getAccessToken() == null)) {
            manager.removeUser(token.getAccessToken());
        }
    }

    @Test
    @Description("Создать клиента")
    public void createUserTest() {
        user = GeneratorUser.getRandom();
        manager.createUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue())
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @Description("Создать клиента без имени")
    public void createUserWithoutNameTest() {
        user = GeneratorUser.getRandomWithoutName();
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Создать клиента без имени Email")
    public void createUserWithoutEmailTest() {
        user = GeneratorUser.getRandomWithoutEmail();
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Создать клиента без имени пароля")
    public void createUserWithoutPassword() {
        user = GeneratorUser.getRandomWithoutPassword();
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @Description("Создать клиента без данный")
    public void createNullUser() {
        user = new DataUser();
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Создать клиента уже зарегистрированными данными")
    public void createUserAlreadyExist() {
        user = GeneratorUser.getRandom();
        manager.createUser(user);
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("User already exists"));
    }
}
