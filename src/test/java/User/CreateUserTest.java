package User;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class CreateUserTest {
    private UserManager manager;
    private Tokens tokens;
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
    public void removeUser() {
        tokens = manager.login(LoginUser.from(user))
                .extract().body()
                .as(Tokens.class);
        if (!(tokens.getAccessToken() == null)) {
            manager.removeUser(tokens.getAccessToken());
        }
    }

    @Test
    public void createUserTest() {
        user = GeneratorUser.getRandom();
        manager.createUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user", notNullValue());
    }

    @Test
    public void checkUserEmailTest() {
        user = GeneratorUser.getRandom();
        manager.createUser(user)
                .assertThat()
                .statusCode(200)
                .body("user.email", equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    public void checkUserNameTest() {
        user = GeneratorUser.getRandom();
        manager.createUser(user)
                .assertThat()
                .statusCode(200)
                .body("user.name", equalTo(user.getName()));
    }

    @Test
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
    public void createUserWithoutEmailTest() {
        user = GeneratorUser.getRandomWithoutEmail();
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void createUserWithoutPassword() {
        user = GeneratorUser.getRandomWithoutPassword();
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    public void createNullUser() {
        user = new DataUser();
        manager.createUser(user)
                .assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    public void logoutUserTest() {
        user = GeneratorUser.getRandom();
       tokens = manager.createUser(user)
               .extract().body()
               .as(Tokens.class);
        Logout logout=new Logout(tokens.getRefreshToken());
        manager.logoutUser(logout)
                .assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("message", equalTo("Successful logout"));
    }
    @Test
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
