package com.flycode.paradox.taxiuser.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.flycode.paradox.taxiuser.settings.AppSettings;

import java.util.Hashtable;

/**
 * Created by macbook on 12/20/15.
 */
public class TypefaceUtils {
    public static class AVAILABLE_FONTS {
        public static final String ICOMOON = "icomoon.ttf";
        public static final String ROBOTO_THIN = "Roboto-Thin.ttf";
        public static final String ROBOTO_REGULAR = "Roboto-Regular.ttf";
    }

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface getTypeface(Context context, String typefaceName){
        synchronized(cache){
            if(!cache.containsKey(typefaceName)){
                if (AppSettings.sharedSettings(context).getLanguage().equals(AppSettings.LANGUAGES.HY)
                    && typefaceName.contains("Roboto")) {
                    typefaceName = "Noh45_Am.ttf";
                }

                Typeface typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);

                cache.put(typefaceName, typeface);
            }
            return cache.get(typefaceName);
        }
    }
}
