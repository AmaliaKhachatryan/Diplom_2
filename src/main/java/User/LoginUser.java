package User;

public class LoginUser {
    private String email;
    private String password;

    public static LoginUser from(DataUser user) {
        return new LoginUser(user.getEmail(), user.getPassword());
    }

    public LoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getUserEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
