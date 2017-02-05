package ru.mail.tp.callbackpal.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.mail.tp.callbackpal.api.models.CommutateSubscribersResult;


public interface CommutationService {
	@GET("api/callback/?function=commutateSubscribers")
	Call<CommutateSubscribersResult> requestCommutation(
			@Query("numberA") String phoneA,
			@Query("numberB") String phoneB
	);

	Retrofit retrofit = new Retrofit.Builder()
			.baseUrl("http://komitsky.xyz/")
			.addConverterFactory(GsonConverterFactory.create())
			.build();
}
