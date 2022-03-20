package com.example.mydoppler;

import com.jasperlu.doppler.Doppler;


public class TheDoppler {
    private static Doppler doppler;

    public static Doppler getDoppler() {
        if (doppler == null) {
            doppler = new Doppler();
        }
        return doppler;
    }
}
