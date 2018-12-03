package jmbd.i2c.mpu6050.register.configuration;

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
 * Access, editing and storage of an 8-bit register value.
 *
 * @author savvas
 */
public abstract class RegisterValue {

    protected RegisterAddress registerAddress;

    protected byte value;

    /**
     * ENSURES:
     *
     * getRegisterAddress() == RegisterAddress.NONE
     *
     */
    protected RegisterValue() {

        registerAddress = RegisterAddress.NONE;

        assert getRegisterAddress() == RegisterAddress.NONE : "registerAddress not set to RegisterAddress.NONE";
    }

    public RegisterAddress getRegisterAddress() {

        return registerAddress;
    }

    /**
     * REQUIRES:
     *
     * registerAddress != null
     *
     * ENSURES:
     *
     * getRegisterAddress() == registerAddress
     *
     * @param registerAddress
     */
    public void setRegisterAddress(RegisterAddress registerAddress) {

        assert registerAddress != null : "registerAddress is null";

        this.registerAddress = registerAddress;

        assert getRegisterAddress() == registerAddress : "registerAddress of current object was not updated";
    }

    /**
     * Read or modified value.
     *
     * @return
     */
    public byte getValue() {

        return value;
    }

    /**
     * Desired value to store.
     *
     * WARNING: Be extremely careful when calling this method as it will
     * override the value that will potentially be store()'d later on..Might be
     * worth considering if only parts of the register value should be
     * clearBit()'ed or setBit()'ed instead.
     *
     * ENSURES:
     *
     * getValue() == value
     *
     * @param value
     */
    public void setValue(byte value) {

        this.value = value;

        assert getValue() == value : "value of this object was not updated";
    }

    /**
     * Writes current value back to device register.
     */
    public abstract void store();

    /**
     * Loads current value from register and updates getValue() attribute with
     * result.
     *
     * ENSURES:
     *
     * getValue() == current_register_value
     *
     */
    public abstract void load();

    /**
     * Turns bit in the given index off.
     *
     * REQUIRES:
     *
     * index BETWEEN [0 - 7]
     *
     * @param index
     */
    public void clearBit(int index) {

        assert index >= 0 && index <= 7 : "bit index not in [0 - 7] range";

        value &= ~(1 << index);
    }

    /**
     * Turns bit in the given index on.
     *
     * REQUIRES:
     *
     * index BETWEEN [0 - 7]
     *
     * @param index
     */
    public void setBit(int index) {

        assert index >= 0 && index <= 7 : "bit index not in [0 - 7] range";

        value |= (1 << index);
    }

    /**
     * Is the value of the bit at given index "high" or "low"?
     *
     * REQUIRES:
     *
     * index BETWEEN [0 - 7]
     *
     * @param index
     * @return
     */
    public boolean bitValueAt(int index) {

        assert index >= 0 && index <= 7 : "bit index not in [0 - 7] range";

        int mask = (1 << index);

        return !((value & mask) == 0);
    }

    /**
     * Sets register value to 0.
     *
     * WARNING: Be extremely careful when calling this method as it will
     * zero-out the value that will potentially be store()'d later on..Might be
     * worth considering if only parts of the register value should be
     * clearBit()'ed or setBit()'ed instead.
     *
     * ENSURES:
     *
     * getValue() == 0
     */
    public void wipeOut() {

        setValue((byte) 0);

        assert getValue() == 0 : "value not set to \"0\"";
    }

    /**
     * String representation in binary format of getValue().
     *
     * ENSURES:
     *
     * toString().length() == 8
     *
     * @return
     */
    @Override
    public String toString() {

        String asString = Integer.toBinaryString(getValue());
        StringBuilder sb = new StringBuilder();

        if (asString.length() > 8) {
            // a negative number - get rid of sign extention
            sb.append(asString.substring(asString.length() - 8));
        } else if (asString.length() < 8) {
            // a positive number - zero-prefix if necessary (cause Java has lopped-off all leading zeros..)
            sb.append(asString);
            while (sb.length() < 8) {
                sb.insert(0, "0");
            }
        }

        String result = sb.toString();

        assert result.length() == 8 : "String representation not 8 characters long";

        return sb.toString();
    }
}
