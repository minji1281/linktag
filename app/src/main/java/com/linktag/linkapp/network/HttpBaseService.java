package com.linktag.linkapp.network;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpBaseService {

    public enum TYPE {
        GET,
        POST,
        PUT,
        DELETE,
        UPDATE
    }

    private static final long CONNECT_TIMEOUT = 30;
    public static final String CHARSET_NAME = "utf-8";
    public static final String POST_PARAM = "param";
    public static final String TYPE_SUBTYPE = "application/x-www-form-urlencoded;charset=UTF-8";


    public static Object retrofit(Class<?> className) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = null;

        try {

            client = new OkHttpClient.Builder()
                    .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .addNetworkInterceptor(loggingInterceptor)
                    .addInterceptor(new Interceptor() {

                        @Override
                        public Response intercept(Chain chain) throws IOException {

                            Request request = chain.request();
                            Request.Builder requestBuilder = request.newBuilder();
                            String postBodyString = HttpBaseService.toString(request.body());

                            if (0 >= postBodyString.trim().length()) {
                                chain.proceed(request);
                            }

                            postBodyString = URLDecoder.decode(postBodyString, CHARSET_NAME);
                            String param = getJsonStringParameter(postBodyString);

                            RequestBody formBody = new FormBody.Builder().add(POST_PARAM, param).build();
                            request = requestBuilder.post(RequestBody.create(MediaType.parse(TYPE_SUBTYPE), HttpBaseService.toString(formBody))).build();
                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(new DecryptionInterceptor())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseConst.URL_HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(className);

    }

    public static Object retrofit(Class<?> className, final TYPE type) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = null;

        try {

            client = new OkHttpClient.Builder()
                    .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .addNetworkInterceptor(loggingInterceptor)
                    .addInterceptor(new Interceptor() {

                        @Override
                        public Response intercept(Chain chain) throws IOException {

                            Request request = chain.request();
                            Request.Builder requestBuilder = request.newBuilder();

                            //----------
                            // GET
                            //----------
                            if (type.equals(TYPE.GET)) {
                                request = requestBuilder.get().build();
                                return chain.proceed(request);
                            }

                            String postBodyString = HttpBaseService.toString(request.body());

                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(new DecryptionInterceptor())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseConst.URL_HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(className);

    }

    public static String toString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            if (request != null) {
                request.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "";
        }
    }

    public static String getJsonStringParameter(String param) {

        if (null == param || 0 > param.trim().length()) return "";
        String[] pairs = param.split("&");
        if (0 >= pairs.length) return "";
        JSONObject obj = new JSONObject();

        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                Object val = (0 == pair.substring(idx + 1).length()) ? JSONObject.NULL : pair.substring(idx + 1);
                obj.put(pair.substring(0, idx), val);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        return (null != obj) ? obj.toString() : "";

    }


    public static class DecryptionInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Response response = chain.proceed(chain.request());

            if (response.isSuccessful()) {

                String str = response.body().string();

                try {

                    //----------
                    // 개행처리 - HTML
                    //----------
                    if (str.toLowerCase().contains("<p>")) {
                        str = str.replaceAll("(\\\\r\\\\n|\\\\n\\\\r|\\\\r|\\\\n)", "<br/>");
                    }

                    //----------
                    // 로그아웃 확인
                    //----------
                    if (str.contains("result")) {
                        JSONObject obj = new JSONObject(str);
                        JSONObject result = obj.getJSONObject("result");
                        int code = result.getInt("code");

                        if (code == 902) {
//                            callLoginStatus("");
                        }
                    }

                    //----------
                    // 토큰만료 확인
                    //----------
                    if (str.contains("result")) {
                        JSONObject obj = new JSONObject(str);
                        JSONObject result = obj.getJSONObject("result");
                        int code = result.getInt("code");

                        if (code == 903) {
//                            callExpireStatus("");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Response.Builder newResponse = response.newBuilder();
                String contentType = "application/json";
                String decrypted = str;
                newResponse.body(ResponseBody.create(MediaType.parse(contentType), decrypted));

                return newResponse.build();
            }

            return response;
        }
    }
}