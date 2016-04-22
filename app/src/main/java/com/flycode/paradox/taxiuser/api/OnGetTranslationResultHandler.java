package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.Translation;

/**
 * Created by anhaytananun on 28.03.16.
 */
public interface OnGetTranslationResultHandler {
    void onGetTranslationSuccess(Translation[] translations);
    void onGetTranslationFailure(int status);
}
