package jmbd.i2c.blinkm.morsecode;

import jmbd.i2c.blinkm.colour.RgbBlinkMColour;

/**
 *
 * @author savvas
 */
public class BlinkMMorseCodeTransmission extends MorseCodeTransmission {

    private final RgbBlinkMColour rgbBlinkMColour;

    public BlinkMMorseCodeTransmission(RgbBlinkMColour rgbBlinkMColour) {

        this.rgbBlinkMColour = rgbBlinkMColour;
    }

    @Override
    protected void signalOn() {

        rgbBlinkMColour.makeWhite();
        rgbBlinkMColour.apply();
    }

    @Override
    protected void signalOff() {

        rgbBlinkMColour.makeBlack();
        rgbBlinkMColour.apply();
    }
}
