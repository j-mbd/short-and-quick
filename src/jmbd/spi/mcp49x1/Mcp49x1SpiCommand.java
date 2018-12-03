package jmbd.spi.mcp49x1;

import java.nio.ByteBuffer;

/**
 * INVARIANTS:
 *
 * getConfigurationValue() IN getConfigurationLegalValues()
 *
 * @author savvas
 */
public abstract class Mcp49x1SpiCommand {

    private final static int[] CONFIGURATION_LEGAL_VALUES = {0x0000, 0x1000, 0x2000, 0x3000, 0x4000, 0x5000, 0x6000, 0x7000, 0x8000, 0x9000, 0xA000, 0xB000, 0xC000, 0xD000, 0xE000, 0xF000};

    protected int configurationValue;
    protected int data;

    protected Mcp49x1SpiSlave mcp49x1SpiSlave;

    protected ByteBuffer buffer;

    public Mcp49x1SpiCommand(Mcp49x1SpiSlave mcp49x1SpiSlave) {

        this.mcp49x1SpiSlave = mcp49x1SpiSlave;
        buffer = ByteBuffer.allocateDirect(2);
    }

    public int getConfigurationValue() {

        return configurationValue;
    }

    public int getData() {

        return data;
    }

    /**
     * REQUIRES:
     *
     * data >= getMinDataValue() && data <= getMaxDataValue()
     *
     * @param data
     */
    public void setData(int data) {

        assert (data >= getMinDataValue()) && (data <= getMaxDataValue()) : "Given value outside legal range of [" + getMinDataValue() + " - " + getMaxDataValue() + "]";

        this.data = data;
    }

    public void setMcp49x1SpiSlave(Mcp49x1SpiSlave mcp49x1SpiSlave) {

        this.mcp49x1SpiSlave = mcp49x1SpiSlave;
    }

    public void store() {

        try {
            int merged = mergedCommand();

            byte upper = (byte) (merged >> 8);
            byte lower = (byte) merged;

            buffer.put(upper);
            buffer.put(lower);
            buffer.flip();

            if (buffer.remaining() == 2) {
                mcp49x1SpiSlave.accept(buffer);
            } else {
                System.out.println("Buffer not primed properly..command not sent!");
            }
        } finally {
            buffer.clear();
        }
    }

    /**
     * The combined configuration + data result.
     *
     * @return
     */
    protected abstract int mergedCommand();

    /**
     * Minimum data value that this command can accept.
     *
     * i.e. the device cannot accept any value lower than this
     *
     * @return
     */
    public abstract int getMinDataValue();

    /**
     * Maximum data value that this command can accept.
     *
     * i.e. the device cannot accept any value higher than this
     *
     * @return
     */
    public abstract int getMaxDataValue();

    //********************************* CONFIGURATION *********************************
    /**
     * Configuration: The device will accept this command.
     */
    public void accepted() {

        clearConfigurationBit(15);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: The device will ignore this command.
     */
    public void ignored() {

        setConfigurationBit(15);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: V-ref Input Buffered
     */
    public void buffered() {

        setConfigurationBit(14);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: V-ref Input Unbuffered
     */
    public void unbuffered() {

        clearConfigurationBit(14);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: 1x Output Gain
     */
    public void outputGainX1() {

        setConfigurationBit(13);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: 2x Output Gain
     */
    public void outputGainX2() {

        clearConfigurationBit(13);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: Vout is available
     */
    public void enabled() {

        setConfigurationBit(12);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: Vout is not available
     */
    public void disabled() {

        clearConfigurationBit(12);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * REQUIRES:
     *
     * index >= 12 && index <= 15
     *
     * @param index
     */
    protected void clearConfigurationBit(int index) {

        assert (index >= 12 && index <= 15) : "index [" + index + "] is not within [12 - 15] range";

        configurationValue &= ~(1 << index);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * REQUIRES:
     *
     * index >= 12 && index <= 15
     *
     * @param index
     */
    protected void setConfigurationBit(int index) {

        assert (index >= 12 && index <= 15) : "index [" + index + "] is not within [12 - 15] range";

        configurationValue |= (1 << index);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    private boolean confValueLegal() {

        for (int v : CONFIGURATION_LEGAL_VALUES) {
            if (configurationValue == v) {
                return true;
            }
        }
        return false;
    }
}