package com.kasandco.shoplist.app.user.settings;

import com.kasandco.shoplist.app.settings.FragmentColorThemeSetting;

import dagger.Subcomponent;

@Subcomponent(modules = {UserSettingsModule.class})
@UserSettingScope
public interface UserSettingsComponent {
    void inject(UserSettingsActivity activity);

    void inject(FragmentColorThemeSetting fragmentColorThemeSetting);
}
