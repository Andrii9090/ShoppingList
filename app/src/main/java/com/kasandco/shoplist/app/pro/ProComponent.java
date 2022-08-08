package com.kasandco.shoplist.app.pro;

import dagger.Subcomponent;

@Subcomponent(modules = ProModule.class)
@ProScope
public interface ProComponent {
    void inject(ProActivity activity);
}
