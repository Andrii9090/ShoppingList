package com.kasandco.shoplist.core;

import com.kasandco.shoplist.app.item.DataComponent;
import com.kasandco.shoplist.app.item.DataModule;
import com.kasandco.shoplist.app.item.ItemComponent;
import com.kasandco.shoplist.app.item.ItemModule;
import com.kasandco.shoplist.app.list.ListActivityComponent;
import com.kasandco.shoplist.app.list.ListModule;
import com.kasandco.shoplist.app.settings.SettingsComponent;
import com.kasandco.shoplist.app.settings.SettingsModule;
import com.kasandco.shoplist.app.splash.SplashComponent;
import com.kasandco.shoplist.app.splash.SplashModule;
import com.kasandco.shoplist.app.user.group.ComponentUserGroup;
import com.kasandco.shoplist.app.user.group.ModuleUserGroup;
import com.kasandco.shoplist.app.user.login.LoginComponent;
import com.kasandco.shoplist.app.user.login.LoginModule;
import com.kasandco.shoplist.app.user.settings.UserSettingsComponent;
import com.kasandco.shoplist.app.user.settings.UserSettingsModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {
    ListActivityComponent plus(ListModule module);
    ItemComponent plus(ItemModule module);
    DataComponent plus(DataModule module);
    SplashComponent plus(SplashModule module);
    LoginComponent plus(LoginModule module);
    UserSettingsComponent plus(UserSettingsModule module);
    SettingsComponent plus(SettingsModule module);
    ComponentUserGroup plus(ModuleUserGroup module);
}
