package com.kasandco.shoplist.app.item.create;

import dagger.Subcomponent;

@ItemCreateScope
@Subcomponent(modules = CreateItemModule.class)
public interface CreateItemComponent {
    void inject(FragmentItemCreate fragment);
}
