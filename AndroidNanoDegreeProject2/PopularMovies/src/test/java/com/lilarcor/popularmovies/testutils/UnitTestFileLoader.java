package com.lilarcor.popularmovies.testutils;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Marcel Braghetto on 27/07/15.
 */
public final class UnitTestFileLoader {
    private UnitTestFileLoader() { }

    /**
     * Given a class type to use as a path and a file name,
     * load and return the contents of the text file for the
     * given file name.
     *
     * @param clazz type to use to resolve the path to load file from.
     * @param textFileName of text file to load.
     *
     * @return string content of the loaded text file.
     */
    public static String loadTextFile(@NonNull Class clazz, @NonNull String textFileName) {
        InputStream inputStream = null;
        BufferedInputStream buffer = null;
        String textContent = null;

        try {
            inputStream = clazz.getResourceAsStream(textFileName);
            buffer = new BufferedInputStream(inputStream);

            byte[] bytes = new byte[buffer.available()];
            buffer.read(bytes);
            textContent = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return textContent;
    }
}