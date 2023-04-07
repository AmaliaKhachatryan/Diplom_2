package User;

public class Tokens {
  private String accessToken;
   private String refreshToken;

    public Tokens(String accessToken) {
        this.accessToken = accessToken;
    }
    public Tokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
