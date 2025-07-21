package Helpers;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private SharedPrefTokenManager sharedPrefTokenManager;

    public AuthInterceptor(Context context,String phoneNumber) {
        sharedPrefTokenManager = new SharedPrefTokenManager(context,phoneNumber);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
    String token = sharedPrefTokenManager.getAccessToken();
    if(token!=null){
        Request request = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(request);
    }
    return chain.proceed(original);
    }
}
