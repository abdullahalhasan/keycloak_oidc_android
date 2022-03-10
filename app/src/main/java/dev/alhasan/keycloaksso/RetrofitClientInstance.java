package dev.alhasan.keycloaksso;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    private static Retrofit retrofit;
    //http://127.0.0.1:8080/
    private static final String BASE_URL = "http://192.168.202.79:8080/";
    //private static final String BASE_URL = "http://127.0.0.1:8080/";

    public static Retrofit getRetrofitInstance(){
        if (retrofit == null){
         retrofit = new Retrofit.Builder()
                 .baseUrl(BASE_URL)
                 .addConverterFactory(GsonConverterFactory.create())
                 .build();
        }
        return retrofit;
    }
}
