package com.kasandco.shoplist.app.item;

import com.kasandco.shoplist.app.item.create.CreateItemComponent;
import com.kasandco.shoplist.app.item.create.CreateItemModule;

import dagger.Subcomponent;

@ItemActivityScope
@Subcomponent(modules = {ItemModule.class})
public interface ItemComponent {
    void inject(ItemActivity activity);

    CreateItemComponent plus(CreateItemModule module);
}
