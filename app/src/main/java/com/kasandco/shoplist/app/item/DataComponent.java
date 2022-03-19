package com.kasandco.shoplist.app.item;

import dagger.Subcomponent;

@ItemActivityScope
@Subcomponent(modules = DataModule.class)
public interface DataComponent {
    void inject(ItemRepository itemRepository);
}
