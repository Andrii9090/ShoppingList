package com.kasandco.shoplist.app.list;


import com.kasandco.shoplist.core.icon.AdapterIcon;

import dagger.Subcomponent;

@Subcomponent(modules = {ListModule.class})
@ListActivityScope
public interface ListActivityComponent {
    void inject(ListActivity activity);
    void inject(AdapterIcon icon);
}
