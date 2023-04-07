package User;

public class DataUser {
    private String email;
    private String password;
    private String name;

    public DataUser() {
    }

    public DataUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public DataUser(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setUserEmail(String userEmail) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
