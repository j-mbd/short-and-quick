package jmbd.i2c.mpu6050.register.configuration;

import jmbd.commons.ByteBufferAccessOptimisation;
import jmbd.i2c.mpu6050.device.RegisterAddress;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.i2cbus.I2CDevice;

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
 * @author savvas
 */
public class I2CRegisterValue extends RegisterValue {

    protected static final int DEFAULT_REG_ADDRR_SIZE = 1;

    protected I2CDevice handle;
    protected ByteBuffer buf;

    protected int regAddrSizeBytes = DEFAULT_REG_ADDRR_SIZE;

    /**
     * Default register registerAddress is 0.
     *
     * REQUIRES:
     *
     * handle not null
     *
     * @param handle
     */
    public I2CRegisterValue(I2CDevice handle) {

        // precondition will be checked in callee method..
        setDevice(handle);
        registerAddress = RegisterAddress.NONE;
    }

    /**
     * REQUIRES:
     *
     * handle not null
     *
     * ENSURES:
     *
     * INTERNAL:
     *
     * 1) handle != null
     *
     * 2) buf != null
     *
     *
     * @param handle
     */
    public void setDevice(I2CDevice handle) {

        assert handle != null : "I2C handle is null";

        this.handle = handle;
        ByteBufferAccessOptimisation o = new ByteBufferAccessOptimisation(this.handle);

        buf = o.optimised(ByteBuffer.allocateDirect(1));

        assert this.handle != null : "handle was not set to given handle";
        assert this.buf != null : "buffer was not allocated";
        assert this.buf.capacity() == 1 : "buffer capacity of \"" + buf.capacity() + "\" is incorrect";
    }

    /**
     * How many bytes of the configured registerAddress should be used to
     * calculate an effective registerAddress?
     *
     * @return
     */
    public int getRegAddrSizeBytes() {

        return regAddrSizeBytes;
    }

    /**
     * Number of bytes in the configured registerAddress that should be used to
     * calculate an effective registerAddress.
     *
     * REQUIRES:
     *
     * 1) regAddrSizeBytes > 0
     *
     * 2) regAddrSizeBytes BETWEEN 1 AND 4
     *
     * ENSURES:
     *
     * getRegAddrSizeBytes() == regAddrSizeBytes
     *
     * @param regAddrSizeBytes
     */
    public void setRegAddrSizeBytes(int regAddrSizeBytes) {

        assert regAddrSizeBytes > 0 : "regAddrSizeBytes is negative";

        assert regAddrSizeBytes > 0 && regAddrSizeBytes < 5 : "regAddrSizeBytes is not within [1 - 4] range";

        this.regAddrSizeBytes = regAddrSizeBytes;

        assert getRegAddrSizeBytes() == regAddrSizeBytes : "regAddrSizeBytes of current object was not updated";
    }

    /**
     * REQUIRES:
     *
     * handle not null
     *
     * ENSURES:
     *
     * INTERNAL:
     *
     * this.handle != null
     *
     * @param handle
     */
    public void setHandle(I2CDevice handle) {

        assert handle != null : "I2C handle is null";

        this.handle = handle;

        assert this.handle != null : "handle was not set";
    }

    /**
     * ENSURES
     *
     * INTERNAL:
     *
     * buf.remaining() == 8
     */
    @Override
    public void store() {

        try {

            buf.put(value).flip();
            handle.write(getRegisterAddress().getValue(), getRegAddrSizeBytes(), buf);
        } catch (IOException ex) {
            Logger.getLogger(I2CRegisterValue.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } finally {

            buf.clear();

            assert buf.remaining() == 8 : "buffer not cleared";
        }
    }

    /**
     * Loads current value from register and updates getValue() attribute with
     * the result.
     *
     * ENSURES:
     *
     * getValue() == current_register_value
     *
     * INTERNAL:
     *
     * buf.remaining() == 8
     *
     */
    @Override
    public void load() {

        try {

            handle.read(getRegisterAddress().getValue(), getRegAddrSizeBytes(), buf);
            buf.flip();

            byte result = buf.get();

            value = result;

            assert getValue() == result : "value not updated with load result";
        } catch (IOException ex) {

            Logger.getLogger(I2CRegisterValue.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } finally {

            buf.clear();

            assert buf.remaining() == 8 : "buffer not cleared";
        }
    }
}
