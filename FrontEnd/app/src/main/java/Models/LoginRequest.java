package Models;

public class LoginRequest {
    String phoneNumber;
    String email;
    String emailOtp;

    public LoginRequest(String phoneNumber, String email, String emailOtp) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.emailOtp = emailOtp;
    }
}
