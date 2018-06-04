package jmbd.i2c.blinkm.admin;

import jmbd.commons.ByteConversion;

/**
 *
 * @author savvas
 */
public class FirmwareVersion {

    private final short major;
    private final short minor;

    public FirmwareVersion(short major, short minor) {

        this.major = major;
        this.minor = minor;
    }

    public short getMajor() {

        return major;
    }

    public short getMinor() {

        return minor;
    }

    @Override
    public String toString() {

        return "FirmwareVersion{" + "major=" + major + ", minor=" + minor + '}';
    }

    /**
     * REQUIRES:
     *
     * 1) r not null
     *
     * 2) r.length == 2
     *
     * @param r
     * @return
     */
    public static FirmwareVersion fromRawValues(byte[] r) {

        short major, minor;
        ByteConversion bc = new ByteConversion();

        bc.setByte(r[0]);
        major = bc.asShort();

        bc.setByte(r[1]);
        minor = bc.asShort();

        return new FirmwareVersion(major, minor);
    }
}
