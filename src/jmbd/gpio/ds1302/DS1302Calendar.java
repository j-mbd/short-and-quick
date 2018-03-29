package jmbd.gpio.ds1302;

import jmbd.commons.BinaryCodedDecimalFormat;
import jmbd.commons.TimeDelay;
import java.io.IOException;
import java.util.Calendar;
import javax.microedition.midlet.MIDlet;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

public class DS1302Calendar extends MIDlet {

    private static final int CLOCK_PIN_NUMBER = 18;
    private static final int DATA_PIN_NUMBER = 17;
    private static final int CE_PIN_NUMBER = 24;

    private static final int PULSE_DURATION_NANOS = 1_000;

    private static final int RTC_SECS_REG = 0;
    private static final int RTC_MIN_REG = 1;
    private static final int RTC_HOUR_REG = 2;

    private final int[] REGISTER_MASKS = {0x7F, 0x7F, 0x3F, 0x3F, 0x1F, 0x07, 0x00};

    private GPIOPin dataPin;
    private GPIOPin clockPin;
    private GPIOPin cePin;

    private DS1302SerialInterface dS1302SerialInterface;
    private DS1302Command dS1302Command;
    private BinaryCodedDecimalFormat bcdFormat;
    private TimeDelay timeDelay;

    @Override
    public void startApp() {

        try {
            dataPin = biDirectionalPin(DATA_PIN_NUMBER);
            clockPin = outPin(CLOCK_PIN_NUMBER);
            cePin = outPin(CE_PIN_NUMBER);

            dS1302SerialInterface = dS1302SerialInterface();
            dS1302Command = new DS1302Command(dS1302SerialInterface);
            bcdFormat = new BinaryCodedDecimalFormat();
            timeDelay = new TimeDelay();

            testRamRegisters();
            setCurrentTime();
            printTimeInLoop();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void testRamRegisters() {

        // five-bits allocated for RAM registers so 31 addressable registers
        for (int i = 0; i < 31; i++) {
            testRamRegister(i, i);
        }
    }

    /**
     *
     * Writes given value to given RAM register and asserts read value is same.
     *
     * @param reg five bits after LSB allocated for register number.
     * @param value
     */
    private void testRamRegister(int reg, int value) {

        dS1302Command.writeToRam(reg, value);
        int read = dS1302Command.readFromRam(reg);

        assert read == value : "read: " + read + ", expected: " + value;
    }

    /**
     * Sets device time with current time.
     */
    private void setCurrentTime() {

        // Java-8+ Date&Time API..?
        Calendar cal = Calendar.getInstance();
        int sec = cal.get(Calendar.SECOND);
        int min = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        dS1302Command.writeToRtc(RTC_SECS_REG, bcdFormat.toBinaryCoded(sec));
        dS1302Command.writeToRtc(RTC_MIN_REG, bcdFormat.toBinaryCoded(min));
        dS1302Command.writeToRtc(RTC_HOUR_REG, bcdFormat.toBinaryCoded(hour));
    }

    private void printTimeInLoop() {

        while (true) {
            int secRaw = dS1302Command.readFromRtc(RTC_SECS_REG);
            int minRaw = dS1302Command.readFromRtc(RTC_MIN_REG);
            int hourRaw = dS1302Command.readFromRtc(RTC_HOUR_REG);

            System.out.println("The time now is: " + bcdFormat.toDecimal(hourRaw, REGISTER_MASKS[2]) + " hours " + bcdFormat.toDecimal(minRaw, REGISTER_MASKS[1]) + " mins and " + bcdFormat.toDecimal(secRaw, REGISTER_MASKS[0]) + " seconds");

            timeDelay.pauseMillis(5_000);
        }
    }

    private GPIOPin biDirectionalPin(int pinNumber) throws IOException {

        GPIOPinConfig.Builder pBuilder = new GPIOPinConfig.Builder();

        pBuilder.setPinNumber(pinNumber);
        pBuilder.setDirection(GPIOPinConfig.DIR_BOTH_INIT_OUTPUT);

        return DeviceManager.open(pBuilder.build());
    }

    private GPIOPin outPin(int pinNumber) throws IOException {

        GPIOPinConfig.Builder pBuilder = new GPIOPinConfig.Builder();

        pBuilder.setPinNumber(pinNumber);
        pBuilder.setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY);
        pBuilder.setDriveMode(GPIOPinConfig.MODE_OUTPUT_PUSH_PULL);

        return DeviceManager.open(pBuilder.build());
    }

    private DS1302SerialInterface dS1302SerialInterface() {

        DS1302SerialInterface com = new DS1302SerialInterface();

        com.setPulseDuration(PULSE_DURATION_NANOS);
        com.setDataPin(dataPin);
        com.setClockPin(clockPin);
        com.setCePin(cePin);

        return com;
    }

    @Override
    public void destroyApp(boolean unconditional) {

        closeIgnoringExceptions(dataPin);
        closeIgnoringExceptions(clockPin);
        closeIgnoringExceptions(cePin);
    }

    private void closeIgnoringExceptions(AutoCloseable ac) {

        if (ac != null) {
            try {
                ac.close();
            } catch (Exception ex) {
                // Ignore
            }
        }
    }
}
