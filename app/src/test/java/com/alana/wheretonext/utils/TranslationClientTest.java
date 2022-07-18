package com.alana.wheretonext.utils;

import static org.junit.Assert.assertEquals;

import com.alana.wheretonext.data.network.TranslationClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import okhttp3.MediaType;

@RunWith(RobolectricTestRunner.class)
public class TranslationClientTest {

    String correctTranslationResponse = "Hola";
    String textToTranslate = "Hello";
    String languageToTranslateTo = "es";

    @Mock
    final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Mock
    private TranslationClient translationClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        translationClient = new TranslationClient();
    }

    @Test
    public void getTranslation() {
        String requestBody = "{'q': '" + textToTranslate + "', 'target': '" + languageToTranslateTo + "'}";

        String translationResponse = TranslationClient.getTranslation(textToTranslate, languageToTranslateTo);

        assertEquals(correctTranslationResponse, translationResponse);
    }
}
