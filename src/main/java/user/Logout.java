package user;

public class Logout {
    private String token;
    public static Logout from(Tokens token) {
        return new Logout(token.getRefreshToken());
    }

    public String getToken() {
        return token;
    }
    public Logout(String token) {
        this.token = token;
    }
}
