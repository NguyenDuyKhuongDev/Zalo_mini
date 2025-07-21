package Services;


import Models.AuthRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import Models.AuthResponse;

public interface ApiServices{
    //không cần thêm thủ công token vào header nữa
    //đã có interceptor trong AuthIntercepter tự động gắn
    @POST("/api/AuthControllers/Login")
    Call<AuthResponse> Login(@Body AuthRequest request);

    @POST("/api/AuthControllers/Register")
    Call<AuthResponse> Register(@Body AuthRequest request);

    @POST("/api/AuthControllers/RefreshToken")
    Call<AuthResponse> RefreshToken(@Body String refreshToken);
}

