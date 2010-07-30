import javax.mail.*;

class PasswordAuth extends Authenticator {
    private String username;
    private String password;

    public PasswordAuth(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
    }
}