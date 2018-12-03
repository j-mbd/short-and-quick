package jmbd.spi.mcp49x1;

import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.spibus.SPIDevice;

/**
 * Access to an MCP-4901 device (via SPI).
 *
 * @author savvas
 */
public class Mcp49x1SpiSlave {

    protected SPIDevice device;
    protected GPIOPin csPin;

    /**
     * REQUIRES:
     *
     * 1) device not null
     *
     * 2) device is open
     *
     * 3) csPin not null
     *
     * 4) csPin configured for output only
     *
     * 5) csPin open
     *
     * 6) (wordSize % 8) == 0
     *
     * @param device
     * @param csPin
     * @param wordSize
     */
    public Mcp49x1SpiSlave(SPIDevice device, GPIOPin csPin) {

        setDevice(device);
        setCsPin(csPin);
    }

    public Mcp49x1SpiSlave() {
    }

    /**
     * REQUIRES:
     *
     * 1) Device not null
     *
     * 2) Device is open
     *
     * @param device
     */
    public void setDevice(SPIDevice device) {

        assert device != null : "SPI device is null";
        assert device.isOpen() : "SPI device is closed";

        this.device = device;
    }

    /**
     * REQUIRES:
     *
     * 1) csPin not null
     *
     * 2) csPin configured for output only
     *
     * 3) csPin open
     *
     * @param csPin
     */
    public void setCsPin(GPIOPin csPin) {

        try {
            assert csPin != null : "GPIO CS device is null";
            assert csPin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY : "GPIO CS device not configured for output only";
            assert csPin.isOpen() : "GPIO CS device is closed";

            this.csPin = csPin;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * 1) payload != null
     *
     * 2) payload.remaining() == 2 (i.e. word length is 16-bits)
     *
     * @param payload
     */
    public void accept(ByteBuffer payload) {

        assert payload != null : "payload is null";

        assert payload.remaining() == 2 : "Payload doesn't contain exactly two bytes to store";

        try {
            startDataTransfer();
            device.write(payload);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            stopDataTransfer();
        }
    }

    protected void startDataTransfer() {

        try {
            // The CS pin must be held low for the duration of a write command.
            csPin.setValue(false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void stopDataTransfer() {

        try {
            csPin.setValue(true);
            flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Moves input-register data to output-register (analog-out pin).
     *
     * (NOP in this version, subclasses may redefine to take into account usage
     * of LDAC pin)
     *
     */
    protected void flush() {
        // NOP
    }
}
