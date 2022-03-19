package com.kasandco.shoplist.app.user.group;

import dagger.Subcomponent;

@ScopeUserGroup
@Subcomponent(modules = {ModuleUserGroup.class})
public interface ComponentUserGroup {
    void inject(UserGroupActivity activity);
}
