package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.CarCategory;

/**
 * Created by anhaytananun on 25.12.15.
 */
public interface CarCategoriesListener {
    void onGetCarCategoriesSuccess(CarCategory[] carCategories);
    void onGetCarCategoriesFail();
}
