package ifer.android.shoplist.api;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ifer on 23/7/2017.
 */

public class AuthenticationInterceptor implements Interceptor {

//    private String authToken;
    private Context context;

    public AuthenticationInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        SessionManager sessionManager = new SessionManager(this.context);
        String authToken = sessionManager.fetchAuthToken();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer " + authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}