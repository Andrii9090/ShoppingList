package com.kasandco.familyfinance.app.list;

import dagger.Subcomponent;
@ListActivityScope
@Subcomponent(modules = {DataListModule.class})
public interface DataListComponent {
    void inject(ListRepository repository);
}
