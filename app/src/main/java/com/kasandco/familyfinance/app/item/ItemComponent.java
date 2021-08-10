package com.kasandco.familyfinance.app.item;

import dagger.Subcomponent;

@Subcomponent(modules = {ItemModule.class})
@ItemActivityScope
public interface ItemComponent {
    void inject(ItemActivity activity);
}
