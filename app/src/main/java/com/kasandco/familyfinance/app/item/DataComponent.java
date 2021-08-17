package com.kasandco.familyfinance.app.item;

import dagger.Subcomponent;

@ItemActivityScope
@Subcomponent(modules = DataModule.class)
public interface DataComponent {
    void inject(ItemRepository itemRepository);
}
