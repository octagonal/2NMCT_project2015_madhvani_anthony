package me.madhvani.dwells.api;

import java.util.List;

import me.madhvani.dwells.model.Kot;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by anthony on 15.22.4.
 */
public interface KotService {
        @GET("/kot")
        void getKotByCity(@Query("city") String sort, Callback<List<Kot>> cb);
}
