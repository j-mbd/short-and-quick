package jmbd.spi.mcp49x1;

/**
 *
 * @author savvas
 */
public class Mcp4901SpiCommand extends Mcp49x1SpiCommand {

    public Mcp4901SpiCommand(Mcp49x1SpiSlave mcp49x1SpiSlave) {

        super(mcp49x1SpiSlave);
    }

    @Override
    protected int mergedCommand() {

        // first four bits of the command are ignored in 4901, so value should be shifted left
        data <<= 4;

        // now merge that with configuration portion (beginning of value must fall right next to configuration)
        return getConfigurationValue() | data;
    }

    @Override
    public int getMinDataValue() {

        return 0;
    }

    @Override
    public int getMaxDataValue() {
        // data portion of register in 4901 is 1-byte so max value must be 255
        return 255;
    }
}
