package com.kasandco.familyfinance.app.user.login;

import dagger.Subcomponent;

@Subcomponent(modules = {LoginModule.class})
@LoginScope
public interface LoginComponent {
    void inject(LoginActivity activity);
}
