package me.madhvani.dwells.api.module;

import android.os.Build;

import dagger.Module;
import dagger.Provides;
import me.madhvani.dwells.BuildConfig;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by anthony on 15.14.5.
 */
@Module
public class RestAdapterModule {
    private static final String USER_AGENT = "Dwells/" + BuildConfig.VERSION_NAME + " (Android" + "; cd:" + Build.VERSION.CODENAME + "; si:" + Build.VERSION.SDK_INT + "; rv:" + Build.VERSION.RELEASE + ")";

    @Provides RestAdapter provideRestAdapter(){
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", USER_AGENT);
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(me.madhvani.dwells.api.Constants.ENDPOINT)
                .setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter;
    }
}
