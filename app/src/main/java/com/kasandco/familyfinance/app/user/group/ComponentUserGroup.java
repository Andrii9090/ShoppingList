package com.kasandco.familyfinance.app.user.group;

import dagger.Subcomponent;

@ScopeUserGroup
@Subcomponent(modules = {ModuleUserGroup.class})
public interface ComponentUserGroup {
    void inject(UserGroupActivity activity);
}
