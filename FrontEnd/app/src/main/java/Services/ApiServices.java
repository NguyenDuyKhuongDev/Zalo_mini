package Services;


import Models.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import Models.LoginResponse;

public interface ApiServices{
    //không cần thêm thủ công token vào header nữa
    //đã có interceptor trong AuthIntercepter tự động gắn
    @POST("/api/AuthControllers/Login")
    Call<LoginResponse> Login(@Body LoginRequest request);
}

