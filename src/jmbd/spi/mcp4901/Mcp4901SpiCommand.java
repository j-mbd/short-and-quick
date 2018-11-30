package jmbd.spi.mcp4901;

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
}
