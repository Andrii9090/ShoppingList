package com.kasandco.shoplist.app.splash;

import dagger.Subcomponent;

@Subcomponent(modules = {SplashModule.class})
public interface SplashComponent {
    void inject(SplashActivity activity);
}
