package com.everonet.demo.miniprograms.api;

import android.util.Log;


import com.everonet.demo.miniprograms.BaseApp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by wanny on 16/8/1.
 */
public class ApiClient {
    private static volatile Retrofit retrofit;

    private static final String TAG = ApiClient.class.getSimpleName();

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl("http://mini-demo.everonet.com:8080/")
                            .client(getOkHttpClient())
                            .build();
                }
            }
        }

        return retrofit;
    }

    private static OkHttpClient getOkHttpClient() {
        int[] certificates = {};

        SSLSocketFactory socketFactory = HttpsFactory.getSSLSocketFactory(BaseApp.getAppContext(), certificates);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = chain.request().url();
                        Log.d(TAG, "request url : " + url.toString());
                        Response response = chain.proceed(request);
                        String bodyString = response.body().string();
                        Log.d(TAG, "response: " + bodyString);

                        return response.newBuilder()
                                .body(ResponseBody.create(response.body().contentType(), bodyString)).build();
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder().build();
        builder.addInterceptor(commonInterceptor);
        if (socketFactory != null) {
            builder.sslSocketFactory(socketFactory);
        }


        return builder.build();
    }

    public static MiniAppService getAPI() {
        return getInstance().create(MiniAppService.class);
    }

}
