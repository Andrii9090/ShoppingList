package com.kasandco.familyfinance.app.user.settings;

import com.kasandco.familyfinance.app.settings.FragmentColorThemeSetting;

import dagger.Subcomponent;

@Subcomponent(modules = {UserSettingsModule.class})
public interface UserSettingsComponent {
    void inject(UserSettingsActivity activity);

    void inject(FragmentColorThemeSetting fragmentColorThemeSetting);
}
