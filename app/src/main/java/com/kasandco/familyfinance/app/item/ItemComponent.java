package com.kasandco.familyfinance.app.item;

import com.kasandco.familyfinance.app.item.fragmentCreate.CreateItemComponent;
import com.kasandco.familyfinance.app.item.fragmentCreate.CreateItemModule;

import dagger.Subcomponent;

@ItemActivityScope
@Subcomponent(modules = {ItemModule.class})
public interface ItemComponent {
    void inject(ItemActivity activity);

    CreateItemComponent plus(CreateItemModule module);
}
