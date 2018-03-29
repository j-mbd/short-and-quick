package jmbd.gpio.ds1302;

import jmbd.commons.TimeDelay;
import java.io.IOException;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 *
 *
 */
public class DS1302SerialInterface {

    private GPIOPin clockPin;
    private GPIOPin dataPin;
    private GPIOPin cePin;

    private final TimeDelay timeDelay;

    private long pulseDuration;
    private boolean dataTransferEnabled;

    public DS1302SerialInterface() {
        timeDelay = new TimeDelay();
    }

    /**
     * REQUIRES:
     *
     * 1) Pin not null. 2) Pin not closed. 3) Pin configured for output only.
     *
     *
     * @param clockPin
     */
    public void setClockPin(GPIOPin clockPin) {

        try {
            assert clockPin != null : "Clock pin null";
            assert clockPin.isOpen() : "Clock pin closed";
            assert clockPin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY : "Clock pin not output only";

            this.clockPin = clockPin;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * 1) Pin not null. 2) Pin not closed. 3) Pin configured for output only.
     *
     *
     * @param cePin
     */
    public void setCePin(GPIOPin cePin) {

        try {
            assert cePin != null : "CE pin null";
            assert cePin.isOpen() : "CE pin closed";
            assert cePin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY : "CE pin not output only";

            this.cePin = cePin;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * 1) Pin not null. 2) Pin not closed. 3) Pin configured both for input &
     * output.
     *
     * @param dataPin
     */
    public void setDataPin(GPIOPin dataPin) {

        try {
            assert dataPin != null : "Data pin null";
            assert dataPin.isOpen() : "Data pin closed";
            assert (dataPin.getDirection() == GPIOPinConfig.DIR_BOTH_INIT_OUTPUT) || (dataPin.getDirection() == GPIOPinConfig.DIR_BOTH_INIT_INPUT) : "Data pin not input or output";

            this.dataPin = dataPin;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Clock toggling period in nanoseconds.
     *
     * REQUIRES: pulse duration > 0
     *
     * @param pulseDuration
     */
    public void setPulseDuration(long pulseDuration) {

        assert pulseDuration > 0 : "Pulse duration negative";

        this.pulseDuration = pulseDuration;
    }

    /**
     * Clocks given data in.
     *
     * REQUIRES: Data transfer enabled.
     *
     * ENSURES:
     *
     * 1) Clock/data pins not closed.
     *
     * @param data
     */
    public void clockIn(int data) {

        assert dataTransferEnabled() : "Data transfer disabled";

        try {
            dataPin.setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY);

            for (int i = 0; i < 8; i++) {

                boolean bit = (data & 0x01) == 1;
                dataPin.setValue(bit);
                timeDelay.pauseNanos(pulseDuration);

                toggleClock();
                data >>= 1;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            assert clockPinOpen() : "Clock pin was closed";
            assert dataPinOpen() : "Data pin was closed";
        }
    }

    /**
     * Clocks data out.
     *
     * REQUIRES: Data transfer enabled.
     *
     * ENSURES:
     *
     * 1) Clock/data pins not closed.
     *
     * @return
     */
    public int clockOut() {

        assert dataTransferEnabled() : "Data transfer disabled";

        try {
            dataPin.setDirection(GPIOPinConfig.DIR_INPUT_ONLY);

            int result = 0;

            for (int i = 0; i < 8; i++) {

                int bit = dataPin.getValue() ? 1 : 0;
                result |= (bit << i);
                timeDelay.pauseNanos(pulseDuration);

                toggleClock();
            }
            return result;
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        } finally {
            assert clockPinOpen() : "Clock pin was closed";
            assert dataPinOpen() : "Data pin was closed";
        }
    }

    private void toggleClock() throws IOException {

        clockPin.setValue(true);
        timeDelay.pauseNanos(pulseDuration);
        clockPin.setValue(false);
        timeDelay.pauseNanos(pulseDuration);
    }

    public void enableDataTransfer() {

        try {
            // clock must be low when CE is driven high
            clockPin.setValue(false);
            timeDelay.pauseNanos(pulseDuration);
            cePin.setValue(true);
            timeDelay.pauseNanos(pulseDuration);

            dataTransferEnabled = true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void disableDataTransfer() {

        try {
            cePin.setValue(false);
            timeDelay.pauseNanos(pulseDuration);

            dataTransferEnabled = false;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Can we start writing & reading data?
    public boolean dataTransferEnabled() {

        return dataTransferEnabled;
    }

    // Is clock pin open?
    private boolean clockPinOpen() {

        return clockPin != null && clockPin.isOpen();
    }

    // Is data pin open?
    private boolean dataPinOpen() {

        return dataPin != null && dataPin.isOpen();
    }
}
