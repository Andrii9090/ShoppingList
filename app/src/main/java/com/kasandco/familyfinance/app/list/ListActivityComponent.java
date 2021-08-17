package com.kasandco.familyfinance.app.list;


import com.kasandco.familyfinance.app.icon.AdapterIcon;
import com.kasandco.familyfinance.app.list.CreateList.FragmentCreateListComponent;
import com.kasandco.familyfinance.app.list.CreateList.FragmentCreateModule;

import dagger.Subcomponent;

@Subcomponent(modules = {ListActivityModule.class})
@ListActivityScope
public interface ListActivityComponent {
    void inject(ListActivity activity);
    void inject(AdapterIcon icon);

    FragmentCreateListComponent plus(FragmentCreateModule module);
}
