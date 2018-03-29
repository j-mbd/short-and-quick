package jmbd.gpio.shiftregister;

import java.io.IOException;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 * Interface for a shift-register device.
 *
 * INVARIANTS:
 *
 * 1) bitsRemaining >= 0
 *
 * 2) bitsRemaining <= maxLoadBits
 *
 */
public class ShiftRegister {

    private static final int DEFAULT_MAX_LOAD_BITS = 8;

    protected GPIOPin dataPin;
    private GPIOPin latchPin;
    private GPIOPin clockPin;

    protected int maxLoadBits = DEFAULT_MAX_LOAD_BITS;
    protected int bitsRemaining;

    /**
     * REQUIRES:
     *
     * 1) Pin not null
     *
     * 2) Pin configured for output only
     *
     *
     * @param dataPin
     */
    public void setDataPin(GPIOPin dataPin) {

        try {
            assert dataPin != null : "Data pin is null";
            assert dataPin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY : "Data pin is not output only";

            this.dataPin = dataPin;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * 1) Pin not null
     *
     * 2) Pin configured for output only
     *
     * ENSURES:
     *
     * 1) Pin value is "low"
     *
     * @param latchPin
     */
    public void setLatchPin(GPIOPin latchPin) {

        try {
            assert latchPin != null : "Latch pin is null";
            assert latchPin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY : "Latch pin is not output only";

            this.latchPin = latchPin;
            this.latchPin.setValue(false);

            assert !latchPin.getValue() : "Initial latch pin value is \"high\" ";
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * 1) pin not null
     *
     * 2) pin configured for output only
     *
     * ENSURES:
     *
     * 1) Pin value is "low"
     *
     * @param clockPin
     */
    public void setClockPin(GPIOPin clockPin) {

        try {
            assert clockPin != null : "Clock pin is null";
            assert clockPin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY : "Clock pin is not output only";

            this.clockPin = clockPin;
            this.clockPin.setValue(false);

            assert !clockPin.getValue() : "Initial clock pin value is \"high\" ";
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * maxLoadBits > 0
     *
     * @param maxLoadBits
     */
    public void maxLoadBits(int maxLoadBits) {

        assert maxLoadBits > 0 : "maxLoadBits is zero or negative";

        this.maxLoadBits = maxLoadBits;
        bitsRemaining = this.maxLoadBits;
    }

    // What is the register's size?
    public int maxLoadBits() {
        return maxLoadBits;
    }

    /**
     * Shifts-in given bit.
     *
     * REQUIRES:
     *
     * fullyLoaded() == false
     *
     * ENSURES:
     *
     * new bitsRemaining == old bitsRemaining - 1
     *
     * @param bit
     */
    public void load(boolean bit) {

        assert !fullyLoaded() : "Shift register is full";
        int bitsRemainingOld = bitsRemaining;

        try {
            dataPin.setValue(bit);
            toggleClock();

            bitsRemaining--;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        assert bitsRemaining == (bitsRemainingOld - 1) : "Bits remaining to fill wasn't decremented by one";
    }

    /**
     * Latches-out current register content.
     *
     * ENSURES: bitsRemaining == maxLoadBits
     *
     * Content may or may not be fullyLoaded (i.e. a number of bits equal to its
     * capacity have been loaded).
     *
     */
    public void unload() {

        try {
            toggleLatch();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            bitsRemaining = maxLoadBits;
        }

        assert bitsRemaining == maxLoadBits : "Shift-in counter wasn't reset to max load bits after latching out register";
    }

    /**
     * Sets all out pins to "low".
     */
    public void clearLatchOutput() {

        bitsRemaining = maxLoadBits;

        while (!fullyLoaded()) {
            load(false);
        }
        unload();
    }

    private void toggleLatch() throws IOException {

        latchPin.setValue(true);
        latchPin.setValue(false);
    }

    // Have we reached the register's capacity?
    public boolean fullyLoaded() {

        return bitsRemaining == 0;
    }

    private void toggleClock() throws IOException {

        clockPin.setValue(true);
        clockPin.setValue(false);
    }
}
