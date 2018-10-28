package jmbd.i2c.mpu6050.register.measurement;

import jmbd.i2c.mpu6050.device.RegisterAddress;

/**
 * Value of sensor measurement, typically stored as two bytes in two registers.
 *
 * The actual measurement values are offered in two forms:
 *
 * Individually via getHighRegValue() and getLowRegValue() or combined via
 * getValue().
 *
 * INVARIANTS:
 *
 * getValue() == (getHighRegValue() << 8) | (getLowRegValue() & 0xFF)
 *
 * @author savvas
 */
public abstract class MeasurementRegisterValue {

    protected RegisterAddress highRegisterAddr;
    protected RegisterAddress lowRegisterAddr;

    protected byte highRegValue;
    protected byte lowRegValue;

    protected short value;

    /**
     * ENSURES:
     *
     * getHighRegisterAddr() == RegisterAddress.NONE
     *
     * getLowRegisterAddr() == RegisterAddress.NONE
     *
     */
    protected MeasurementRegisterValue() {

        highRegisterAddr = RegisterAddress.NONE;
        lowRegisterAddr = RegisterAddress.NONE;

        assert getHighRegisterAddr() == RegisterAddress.NONE : "highRegisterAddr not set to NONE";
        assert getLowRegisterAddr() == RegisterAddress.NONE : "lowRegisterAddr not set to NONE";
    }

    public RegisterAddress getHighRegisterAddr() {

        return highRegisterAddr;
    }

    /**
     * ENSURES:
     *
     * getHighRegisterAddr() == highRegisterAddr
     *
     * @param highRegisterAddr
     */
    public void setHighRegisterAddr(RegisterAddress highRegisterAddr) {

        this.highRegisterAddr = highRegisterAddr;

        assert getHighRegisterAddr() == highRegisterAddr : "highRegisterAddr was not updated with given value";
    }

    public RegisterAddress getLowRegisterAddr() {

        return lowRegisterAddr;
    }

    /**
     * ENSURES:
     *
     * getLowRegisterAddr() == lowRegisterAddr
     *
     * @param lowRegisterAddr
     */
    public void setLowRegisterAddr(RegisterAddress lowRegisterAddr) {

        this.lowRegisterAddr = lowRegisterAddr;

        assert getLowRegisterAddr() == lowRegisterAddr : "lowRegisterAddr was not updated with given value";
    }

    /**
     * Reads register values and updates getHighRegValue() & getLowRegValue()
     * attributes accordingly.
     *
     * ENSURES:
     *
     * 1) getHighRegValue() == register1_current_value
     *
     * 2) getLowRegValue() == register2_current_value
     *
     */
    public abstract void load();

    /**
     * The "merged" getHighRegValue()/getLowRegValue() result.
     *
     * Can be < = > 0.
     *
     * @return
     */
    public short getValue() {

        return (short) ((getHighRegValue() << 8) | (getLowRegValue() & 0xFF));
    }

    public byte getHighRegValue() {

        return highRegValue;
    }

    public byte getLowRegValue() {

        return lowRegValue;
    }
}
