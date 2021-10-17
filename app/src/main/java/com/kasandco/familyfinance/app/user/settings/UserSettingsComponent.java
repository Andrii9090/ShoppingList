package com.kasandco.familyfinance.app.user.settings;

import dagger.Subcomponent;

@Subcomponent(modules = {UserSettingsModule.class})
public interface UserSettingsComponent {
    void inject(UserSettingsActivity activity);
}
