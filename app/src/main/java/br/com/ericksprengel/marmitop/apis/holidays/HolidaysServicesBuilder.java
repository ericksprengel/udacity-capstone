package br.com.ericksprengel.marmitop.apis.holidays;

import android.content.Context;

import com.readystatesoftware.chuck.ChuckInterceptor;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;

import br.com.ericksprengel.marmitop.BuildConfig;
import br.com.ericksprengel.marmitop.R;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class HolidaysServicesBuilder {

    // It's a temporary key. It'll expire soon.
    private static final String API_KEY = "ZXJpY2suc3ByZW5nZWxAZ21haWwuY29tJmhhc2g9MTc3Mjk5MzIz";

    private static Retrofit mRetrofit;

    private static void initRetrofit(Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // adding api key to query
        httpClient.addInterceptor(new Interceptor() {
                      @Override
                      public Response intercept(Chain chain) throws IOException {
                          Request originalRequest = chain.request();
                          HttpUrl url = originalRequest.url().newBuilder()
                                  .addQueryParameter("token", API_KEY)
                                  .build();
                          Request request = originalRequest.newBuilder()
                                  .url(url)
                                  .build();
                          return chain.proceed(request);
                      }
                  });


        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(new ChuckInterceptor(context))
                    .build();
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.holidays_api_base_url))
                .addConverterFactory(
                        SimpleXmlConverterFactory.createNonStrict(
                                new Persister(new AnnotationStrategy())))
                .client(httpClient.build())
                .build();

    }

    public static HolidaysServices build(Context context) {
        if (mRetrofit == null) {
            initRetrofit(context);
        }
        return mRetrofit.create(HolidaysServices.class);
    }

}
