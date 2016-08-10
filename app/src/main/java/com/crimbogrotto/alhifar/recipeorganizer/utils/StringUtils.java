package com.crimbogrotto.alhifar.recipeorganizer.utils;

/**
 * Created by Alhifar on 8/8/2016.
 */
public class StringUtils {
    public static String properCase(String s)
    {
        if (s.length() == 0)
        {
            return "";
        }
        if (s.length() == 1)
        {
            return s.toUpperCase();
        }
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
