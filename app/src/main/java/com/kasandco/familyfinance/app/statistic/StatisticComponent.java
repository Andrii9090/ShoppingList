package com.kasandco.familyfinance.app.statistic;

import dagger.Subcomponent;
@StatisticScope
@Subcomponent(modules = {StatisticModule.class})
public interface StatisticComponent {
    void inject(StatisticActivity activity);
}
