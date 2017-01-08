package ru.mail.tp.callbackpal.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.mail.tp.callbackpal.api.models.ValidationCode;

/**
 * Created by Martin on 05.01.2017.
 */

public interface ValidationService {
    @GET("api/callback/?function=generatePin")
    Call<ValidationCode> requestValidationCode(
            @Query("number") String phone
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://komitsky.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
