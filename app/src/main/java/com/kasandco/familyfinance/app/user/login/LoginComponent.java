package com.kasandco.familyfinance.app.user.login;

import dagger.Subcomponent;

@Subcomponent(modules = {LoginModule.class})

public interface LoginComponent {
    void inject(LoginActivity activity);
}
