package Models;

public class LoginResponse {
    String refreshToken;
    String acessToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAcessToken() {
        return acessToken;
    }

    public void setAcessToken(String acessToken) {
        this.acessToken = acessToken;
    }

    public LoginResponse(String refreshToken, String acessToken) {
        this.refreshToken = refreshToken;
        this.acessToken = acessToken;
    }
}
