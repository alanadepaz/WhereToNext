package com.alana.wheretonext.utils;

import com.alana.wheretonext.data.network.TranslationClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TranslationClientTest {

    String textToTranslate = "Hello";
    String languageToTranslateTo = "es";
    String translationResponse = "";

    @Mock
    private TranslationClient mockTranslationClient;

    @Before
    public void setUp() {
        mockTranslationClient = Mockito.mock(TranslationClient.class);
    }

    @Test
    public void testGetTranslation() {

        when(mockTranslationClient.getTranslation(textToTranslate, languageToTranslateTo)).thenReturn(translationResponse);

        translationResponse = mockTranslationClient.getTranslation(textToTranslate, languageToTranslateTo);

        verify(mockTranslationClient).getTranslation(textToTranslate, languageToTranslateTo);
    }
}
