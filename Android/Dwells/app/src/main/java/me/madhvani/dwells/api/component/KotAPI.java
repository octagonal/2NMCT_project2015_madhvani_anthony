package me.madhvani.dwells.api.component;

import javax.inject.Singleton;

import dagger.Component;
import me.madhvani.dwells.api.KotServiceAPI;
import me.madhvani.dwells.api.module.KotServiceModule;

/**
 * Created by anthony on 15.14.5.
 */
@Component(modules = KotServiceModule.class)
@Singleton
public interface KotAPI {
    public KotServiceAPI service();
}
