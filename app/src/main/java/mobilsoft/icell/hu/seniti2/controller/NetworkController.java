package mobilsoft.icell.hu.seniti2.controller;

import android.util.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import mobilsoft.icell.hu.seniti2.BuildConfig;
import mobilsoft.icell.hu.seniti2.controller.dao.EventRequest;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkController {
    private static final String TAG = NetworkController.class.getSimpleName();
    private static NetworkController instance = new NetworkController();
    public final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    //
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    //
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };
    private Retrofit retrofit;
    private EndPoints endPoints;

    private NetworkController() {
        initRetrofit();
    }

    public static NetworkController getInstance() {
        return instance;
    }

    private void initRetrofit() {
        OkHttpClient.Builder okHttpClient;
        okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.readTimeout(60, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(60, TimeUnit.SECONDS);
        okHttpClient.retryOnConnectionFailure(true);
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            okHttpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Log.d(TAG, "MESSAGE", e);
        }
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient.hostnameVerifier((hostname, session) -> true);
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.addInterceptor(logging).build())
                .build();
    }

    private Retrofit getRetrofit() {
        return retrofit;
    }

    private EndPoints getEndpoint() {
        if (endPoints == null) {
            endPoints = getRetrofit().create(EndPoints.class);
        }
        return endPoints;
    }

    public Call<Void> sendRequest(EventRequest request) {
        return getEndpoint().sendResult(request);
    }

}

