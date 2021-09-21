package com.kasandco.familyfinance.app.user.registration;

import dagger.Subcomponent;

@Subcomponent(modules = {RegistrationModule.class})
@RegistrationScope
public interface RegistrationComponent {
    void inject(RegistrationActivity activity);
}
