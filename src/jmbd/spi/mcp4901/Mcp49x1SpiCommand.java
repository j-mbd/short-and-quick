package jmbd.spi.mcp4901;

/**
 * INVARIANTS:
 *
 * 1) getConfigurationValue() IN getConfigurationLegalValues()
 *
 * 2) getDataLength() >=1 && getDataLength() <= 32
 *
 * @author savvas
 */
public abstract class Mcp49x1SpiCommand {

    private final int[] configurationLegalValues = {0x0000, 0x1000, 0x2000, 0x3000, 0x4000, 0x5000, 0x6000, 0x7000, 0x8000, 0x9000, 0xA000, 0xB000, 0xC000, 0xD000, 0xE000, 0xF000};

    protected int configurationValue;
    protected int data;
    protected int dataLength = 1;

    protected Mcp49x1SpiSlave mcp49x1SpiSlave;

    public Mcp49x1SpiCommand(Mcp49x1SpiSlave mcp49x1SpiSlave) {

        this.mcp49x1SpiSlave = mcp49x1SpiSlave;
    }

    public int[] getConfigurationLegalValues() {

        return configurationLegalValues;
    }

    public int getConfigurationValue() {

        return configurationValue;
    }

    public int getData() {

        return data;
    }

    public void setData(int data) {

        this.data = data;
    }

    public int getDataLength() {

        return dataLength;
    }

    /**
     * REQUIRES:
     *
     * dataLength >=1 && dataLength <= 32
     *
     * @param dataLength
     */
    public void setDataLength(int dataLength) {

        assert (dataLength >= 1 && dataLength <= 32) : "dataLength not between [1 - 32] range";

        this.dataLength = dataLength;
    }

    public void setMcp49x1SpiSlave(Mcp49x1SpiSlave mcp49x1SpiSlave) {

        this.mcp49x1SpiSlave = mcp49x1SpiSlave;
    }

    /**
     * Configuration: The device will accept this command.
     */
    public void accepted() {

        configurationValue = configurationValue & (1 << 16);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: The device will ignore this command.
     */
    public void ignored() {

        configurationValue = configurationValue | (1 << 16);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: V-ref Input Buffered
     */
    public void buffered() {

        configurationValue = configurationValue | (1 << 15);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: V-ref Input Unbuffered
     */
    public void unbuffered() {

        configurationValue = configurationValue & (1 << 15);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: 1x Output Gain
     */
    public void outputGainX1() {

        configurationValue = configurationValue | (1 << 14);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: 2x Output Gain
     */
    public void outputGainX2() {

        configurationValue = configurationValue & (1 << 14);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: Vout is available
     */
    public void enabled() {

        configurationValue = configurationValue | (1 << 13);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    /**
     * Configuration: Vout is not available
     */
    public void disabled() {

        configurationValue = configurationValue & (1 << 13);

        assert confValueLegal() : "Current configuration value not in legal-values range";
    }

    public void store() {

        int merged = mergedCommand();

        mcp49x1SpiSlave.store(merged);
    }

    /**
     * i.e. configuration + data
     *
     * @return
     */
    protected abstract int mergedCommand();

    private boolean confValueLegal() {

        for (int v : getConfigurationLegalValues()) {
            if (configurationValue == v) {
                return true;
            }
        }
        return false;
    }
}
