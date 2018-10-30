package jmbd.i2c.mpu6050.register.measurement;

import jmbd.i2c.mpu6050.device.RegisterAddress;

/**
 * THIS SOFTWARE IS PROVIDED BY Savvas Moysidis “AS IS” AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Savvas Moysidis BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
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
