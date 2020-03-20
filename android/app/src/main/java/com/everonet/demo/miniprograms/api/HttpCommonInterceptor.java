package com.everonet.demo.miniprograms.api;

import android.util.Log;


import com.everonet.demo.miniprograms.model.MiniAppRespone;
import com.everonet.demo.miniprograms.util.GsonUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

public class HttpCommonInterceptor implements Interceptor {

    private static final String TAG = HttpCommonInterceptor.class.getSimpleName();

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String SHA256 = "SHA256";

    private Map<String, String> mHeaderParamsMap = new HashMap<>();
    private String mDateTime;

    private HttpCommonInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request oldRequest = chain.request();

        String transType = getTransType(oldRequest);
        if (transType == null || transType.isEmpty()) {
            return chain.proceed(oldRequest);
        }

        Request.Builder requestBuilder = oldRequest.newBuilder();
        requestBuilder.method(oldRequest.method(), oldRequest.body());

//        requestBuilder.addHeader("Authorization", sign(oldRequest));
        requestBuilder.addHeader("Content-type", "application/json");
        requestBuilder.addHeader("DateTime", mDateTime);
        requestBuilder.addHeader("SignType", SHA256);

        //添加公共参数,添加到header中
        if (mHeaderParamsMap.size() > 0) {
            for (Map.Entry<String, String> params : mHeaderParamsMap.entrySet()) {
                requestBuilder.header(params.getKey(), params.getValue());
            }
        }

        Request newRequest = requestBuilder.build();
        Log.d(TAG, "request body : " + bodyToString(newRequest));
        Response response = chain.proceed(newRequest);

        if (!HttpHeaders.hasBody(response)) {
            Log.e(TAG, "response has no body");
            return response;
        }

        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        String content = buffer.clone().readString(charset);

        MiniAppRespone res = GsonUtils.fromJson(content, MiniAppRespone.class);
        String json = GsonUtils.toJson(res);

        ResponseBody body = ResponseBody.create(contentType, json);
        return response.newBuilder().body(body).build();

    }

    public static class Builder {
        HttpCommonInterceptor mHttpCommonInterceptor;

        public Builder() {
            mHttpCommonInterceptor = new HttpCommonInterceptor();
        }

        public Builder addHeaderParams(String key, String value) {
            mHttpCommonInterceptor.mHeaderParamsMap.put(key, value);
            return this;
        }

        public Builder addHeaderParams(String key, int value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, float value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, long value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, double value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public HttpCommonInterceptor build() {
            return mHttpCommonInterceptor;
        }
    }

    /**
     * 获取 request 的 body content
     *
     * @param request
     * @return
     */
    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * 将http method，URL string，DateTime，Key，http body按顺序使用换行符连接，得到签名字符串。
     *
     * @param oldRequest
     * @return
     */
//    private String sign(Request oldRequest) {
//        String method = oldRequest.method();
//        String url = oldRequest.url().encodedPath();
//        mDateTime = DateUtils.getCurrentTimeZ();
//        String key = UserEnv.getSignKey(EvoCashierApp.getAppContext());
//        RequestBody requestBody = oldRequest.body();
//        String body = bodyToString(requestBody);
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(method).append("\n")
//                .append(url).append("\n")
//                .append(mDateTime).append("\n")
//                .append(key).append("\n")
//                .append(body);
//        return EncoderUtil.encrypt(sb.toString(), SHA256);
//    }

    /**
     * 根据 url 获取交易类型 (inqy, rfdp, canc, paut, purc)
     * 并且转为大写
     * /scanpay/mer/S237018/purc/v0
     *
     * @param oldRequest
     * @return
     */
    private String[] strings = {"inqy", "rfdp", "canc", "paut", "purc", "wpay", "chin"};
    private List<String> list = Arrays.asList(strings);

    private String getTransType(Request oldRequest) {
        String url = oldRequest.url().encodedPath();
        String[] array = url.split("/");
        if (array.length < 5 || array[4].isEmpty())
            return null;
        if (!list.contains(array[4]))
            return null;
        return array[4].toUpperCase();
    }

    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
