package jmbd.spi.mcp49x1;

import java.io.IOException;
import javax.microedition.midlet.MIDletStateChangeException;
import jdk.dio.Device;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.spibus.SPIDevice;
import jdk.dio.spibus.SPIDeviceConfig;
import jmbd.commons.CommonOperationsMIDlet;
import jmbd.commons.TimeDelay;

/**
 *
 * @author savvas
 */
public class MCP49x1Device extends CommonOperationsMIDlet {

    // Using configuration explicitly (but that's the device id in case we need it)
    private static final int SPI_DEVICE_ID = 300;
    private static final int CS_PIN_NUM = 8;
    private static final int LDAC_PIN_NUM = 18;

    private static final int SPI_CONTROLLER_NUMBER = 0;
    private static final int SPI_CONTROLLER_ADDRESS = 0;
    private static final int MCP4901_CLOCK_FREQUENCY = 20_000_000;
    private static final int MCP4901_CLOCK_MODE = 0;
    private static final int MCP4901_WORD_LENGTH = 16;

    // Default command configuration: write/unbuffered/1x-gain/active
    private static final int MCP4901_CONFIGURATION_COMMAND = 0b0011000000000000;

    private SPIDevice mcp4901Device;
    private GPIOPin csPin;

    private Mcp49x1SpiCommand mcp49x1SpiCommand;
    private TimeDelay timeDelay;

    @Override
    protected void startApp() throws MIDletStateChangeException {
        try {
            mcp4901Device = mcp4901Device();
            csPin = outPin(CS_PIN_NUM);
            timeDelay = new TimeDelay();

            mcp49x1SpiCommand = mcp4901SpiCommand();

            for (int d = mcp49x1SpiCommand.getMinDataValue(); d <= mcp49x1SpiCommand.getMaxDataValue(); d++) {

                if (d >= mcp49x1SpiCommand.getMinDataValue() && d <= mcp49x1SpiCommand.getMaxDataValue()) {
                    mcp49x1SpiCommand.setData(d);
                    mcp49x1SpiCommand.store();
                } else {
                    System.out.println("data value \"" + d + "\" not within [" + mcp49x1SpiCommand.getMinDataValue() + " - " + mcp49x1SpiCommand.getMaxDataValue() + "] range. Will not be written to device");
                }

                timeDelay.pauseMillis(2_000);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Mcp4901SpiCommand mcp4901SpiCommand() {

        Mcp4901SpiCommand cmd = new Mcp4901SpiCommand(new Mcp49x1SpiSlave(mcp4901Device, csPin));

        cmd.accepted();
        cmd.unbuffered();
        cmd.outputGainX1();
        cmd.enabled();

        return cmd;
    }

    private SPIDevice mcp4901Device() throws IOException {

        SPIDeviceConfig.Builder b = new SPIDeviceConfig.Builder();
        b.setClockFrequency(MCP4901_CLOCK_FREQUENCY);
        b.setClockMode(MCP4901_CLOCK_MODE);
        b.setWordLength(MCP4901_WORD_LENGTH);
        b.setAddress(SPI_CONTROLLER_ADDRESS);
        b.setControllerNumber(SPI_CONTROLLER_NUMBER);
        b.setBitOrdering(Device.BIG_ENDIAN);
        b.setCSActiveLevel(SPIDeviceConfig.CS_ACTIVE_HIGH);

        return DeviceManager.open(SPI_DEVICE_ID);
    }

    private GPIOPin outPin(int pinNum) throws IOException {

        GPIOPinConfig.Builder b = new GPIOPinConfig.Builder();
        b.setPinNumber(pinNum);
        b.setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY);
        b.setInitValue(true);

        return DeviceManager.open(b.build());
    }

    @Override
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {

        closeIgnoringExceptions(mcp4901Device);
        closeIgnoringExceptions(csPin);
    }
}
