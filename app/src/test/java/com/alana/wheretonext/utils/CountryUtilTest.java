package com.alana.wheretonext.utils;

import static org.junit.Assert.*;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class CountryUtilTest {

    @Test
    public void getCountries() {
        ArrayList<String> countryList = new ArrayList<>();
        Map<String, String> countryAndLang = new HashMap<>();

        Context context = RuntimeEnvironment.application;

        CountryUtil.getCountries(context, countryList, countryAndLang);

        // Checking that the languages are correctly grabbed from random countries
        assertEquals("ps", countryAndLang.get("Afghanistan"));
        assertEquals("el", countryAndLang.get("Greece"));
        assertEquals("my", countryAndLang.get("Myanmar"));
        assertEquals("ko", countryAndLang.get("South Korea"));
    }
}