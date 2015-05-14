package me.madhvani.dwells.api.module;

import dagger.Module;
import dagger.Provides;
import me.madhvani.dwells.api.KotServiceAPI;
import retrofit.RestAdapter;

/**
 * Created by anthony on 15.14.5.
 */

@Module(includes = RestAdapterModule.class)
public class KotServiceModule {
    @Provides
    KotServiceAPI provideKotService(RestAdapter restAdapter){
        KotServiceAPI service = restAdapter.create(KotServiceAPI.class);
        return service;
    }
}
