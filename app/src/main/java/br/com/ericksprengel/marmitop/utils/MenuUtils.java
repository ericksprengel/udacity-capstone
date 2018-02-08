package br.com.ericksprengel.marmitop.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MenuUtils {

    public static String getMenuOfTheDay() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        // for tests: return "2018-01-29";
    }
}
