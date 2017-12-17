package jmbd.ds1302;

public class BinaryCodedDecimalFormat {

    public int toDecimal(int raw, int mask) {
        int b1, b2;
        raw &= mask;
        b1 = raw & 0x0F;
        b2 = ((raw >> 4) & 0x0F) * 10;
        return b1 + b2;
    }

    public int toBinaryCoded(int dec) {
        return ((dec / 10) << 4) + (dec % 10);
    }
}
