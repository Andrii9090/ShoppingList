package com.kasandco.familyfinance.app.list;


import com.kasandco.familyfinance.app.icon.AdapterIcon;

import dagger.Subcomponent;

@Subcomponent(modules = {ListModule.class})
@ListActivityScope
public interface ListActivityComponent {
    void inject(ListActivity activity);
    void inject(AdapterIcon icon);
}
