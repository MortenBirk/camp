package com.cac.camp.camp;

/**
 * Created by nikolaiollegaard on 03/12/14.
 */
public class SpotifySingleton {
    private static SpotifySingleton ourInstance = new SpotifySingleton();

    public static SpotifySingleton getInstance() {
        return ourInstance;
    }

    private SpotifySingleton() {
    }
}
