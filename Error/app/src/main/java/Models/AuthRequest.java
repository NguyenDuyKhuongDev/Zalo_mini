package Models;

public class AuthRequest {
    String phoneNumber;
    String email;
    String emailOtp;

    public AuthRequest(String phoneNumber, String email, String emailOtp) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.emailOtp = emailOtp;
    }
}
