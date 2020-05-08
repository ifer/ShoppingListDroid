package ifer.android.shoplist.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import ifer.android.shoplist.AppController;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by ifer on 20/6/2017.
 */

// When run on the emulator, localhost server address is http://10.0.2.2

public class ApiClient {
//    private static final String API_BASE_URL = AppController.getApiDomain();
    private static Gson gson = new GsonBuilder()
                                    .setDateFormat("dd/MM/yyyy")
                                    .create();

    private static Retrofit.Builder builder ;
    public static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient;

//    public static <S> S createNoAuthService(Class<S> serviceClass) {
//        Retrofit retrofit = builder.build();
//        return (retrofit.create(serviceClass));
//    }


    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass);
    }

    private static void setupRetrofit () throws Exception {
        try {
            builder = new Retrofit.Builder()
                    .baseUrl(AppController.getApiDomain())
                    .addConverterFactory(ScalarsConverterFactory.create())      //in order to accept String values as response
                    .addConverterFactory(GsonConverterFactory.create(gson));    //GSON convereer, gson value can be removed if not needed
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        retrofit = builder.build();
        httpClient = new OkHttpClient.Builder();
    }

    public static <S> S createService(Class<S> serviceClass, String username, String password) throws Exception {

        setupRetrofit();

        if (!TextUtils.isEmpty(username)  && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createService(serviceClass, authToken);
        }

        return(null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =  new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}