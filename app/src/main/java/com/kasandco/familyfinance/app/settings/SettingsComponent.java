package com.kasandco.familyfinance.app.settings;

import dagger.Subcomponent;

@SettingsScope
@Subcomponent(modules = {SettingsModule.class})
public interface SettingsComponent {
    void inject(FragmentColorThemeSetting fragment);
    void inject(SettingsActivity settingsActivity);
}
