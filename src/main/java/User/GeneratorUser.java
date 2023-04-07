package User;

import org.apache.commons.lang3.RandomStringUtils;
public class GeneratorUser {
    public static DataUser getRandom() {
        String email = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        return new DataUser(email + "@yandex.ru", password, name);
    }

    public static DataUser getRandomWithoutName() {
        String email = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        return new DataUser(email + "@yandex.ru", password);
    }

    public static DataUser getRandomWithoutEmail() {
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        return new DataUser(password, name);
    }

    public static DataUser getRandomWithoutPassword() {
        String email = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        return new DataUser(email + "@yandex.ru", name);
    }
}
