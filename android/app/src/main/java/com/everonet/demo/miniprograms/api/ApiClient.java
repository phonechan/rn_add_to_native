package com.everonet.demo.miniprograms.api;

import android.util.Log;

import com.everonet.demo.miniprograms.App;
import com.everonet.demo.miniprograms.R;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static volatile Retrofit retrofitdown;

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

    public static Retrofit getInstanceDown() {
        if (retrofitdown == null) {
            synchronized (ApiClient.class) {
                if (retrofitdown == null) {
                    ExecutorService executorService = Executors.newFixedThreadPool(1);
                    retrofitdown = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl("http://mini-demo.everonet.com:8080/")
                            .client(getOkHttpClient())
                            .callbackExecutor(executorService)
                            .build();
                }
            }
        }

        return retrofitdown;
    }

    public static OkHttpClient getOkHttpClient() {
        int[] certificates = {R.raw.digicert};

        SSLSocketFactory socketFactory = HttpsFactory.getSSLSocketFactory(App.instance, certificates);

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
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder().build();
        builder.addInterceptor(commonInterceptor);
//        if (socketFactory != null) {
//            builder.sslSocketFactory(socketFactory);
//        }

        builder.addNetworkInterceptor(new StethoInterceptor());

        return builder.build();
    }

    public static MiniAppService getAPI() {
        return getInstance().create(MiniAppService.class);
    }

    public static MiniAppService getDownAPI() {
        return getInstanceDown().create(MiniAppService.class);
    }

}
