package com.kasandco.familyfinance.app.item.fragmentCreate;

import dagger.Subcomponent;

@ItemCreateScope
@Subcomponent(modules = CreateItemModule.class)
public interface CreateItemComponent {
    void inject(FragmentItemCreate fragment);
}
