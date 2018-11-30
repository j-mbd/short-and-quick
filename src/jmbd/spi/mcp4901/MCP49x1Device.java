package jmbd.spi.mcp4901;

import java.io.IOException;
import java.nio.ByteBuffer;
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

    private Mcp49x1SpiSlave mcp49x1SpiSlave;
    private ByteBuffer transferBuffer;
    private TimeDelay timeDelay;

    @Override
    protected void startApp() throws MIDletStateChangeException {
        try {
            mcp4901Device = mcp4901Device();
            //csPin = outPin(CS_PIN_NUM);
            transferBuffer = ByteBuffer.allocateDirect(MCP4901_WORD_LENGTH);
            timeDelay = new TimeDelay();

            mcp49x1SpiSlave = new Mcp49x1SpiSlave(mcp4901Device, csPin, MCP4901_WORD_LENGTH);

            for (int i = 0; i <= 255; i++) {

                prepareBufferWith4901Value(i);
                mcp49x1SpiSlave.store(i);

//                while (transferBuffer.hasRemaining()) {
//                    System.out.println("command for value " + i + " is " + transferBuffer.getShort());
//                }
                timeDelay.pauseMillis(2_000);
                System.out.println("--------------------------------------------------------");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * 1) val>=0 && val <= 255
     *
     * @param val
     */
    private void prepareBufferWith4901Value(int val) {

        assert val >= 0 && val <= 255 : "val not between [0 - 255] range";

        transferBuffer.clear();

        // first four bits of the command are ignored so value should be shifted left
        val <<= 4;

        // now merge that with configuration portion (beginning of value must fall right next to configuration)
        int completeCommand = MCP4901_CONFIGURATION_COMMAND | val;

        // split in two bytes
        byte msb = (byte) (completeCommand >> 8);
        byte lsb = (byte) completeCommand;

        transferBuffer.put(msb);
        transferBuffer.put(lsb);
        transferBuffer.flip();
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
