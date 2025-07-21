package Models;

public class AuthResponse {
    String refreshToken;
    String accessToken;
    long accessTokenExp;
    long refreshTokenExp;

    public AuthResponse(String refreshToken, String accessToken, long accessTokenExp, long refreshTokenExp) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.accessTokenExp = accessTokenExp;
        this.refreshTokenExp = refreshTokenExp;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessTokenExp() {
        return accessTokenExp;
    }

    public void setAccessTokenExp(long accessTokenExp) {
        this.accessTokenExp = accessTokenExp;
    }

    public long getRefreshTokenExp() {
        return refreshTokenExp;
    }

    public void setRefreshTokenExp(long refreshTokenExp) {
        this.refreshTokenExp = refreshTokenExp;
    }
}
