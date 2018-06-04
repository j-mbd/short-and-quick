package jmbd.commons;

/**
 *
 * @author savvas
 */
public class ByteConversion {

    private byte b;

    public ByteConversion(byte b) {

        this.b = b;
    }

    public ByteConversion() {
    }

    public void setByte(byte b) {

        this.b = b;
    }

    public short asShort() {

        // all integral ops are done in integer arithmetic hence the second cast..a bit wierd having to do this (pun intended)
        return (short) (((short) b) & 0xff);
    }

    public int asInt() {

        return (((int) b) & 0xff);
    }
}
