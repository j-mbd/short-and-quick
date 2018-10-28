package jmbd.i2c.mpu6050.register.measurement;

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
 * Reads values of both registers in a single transaction and thus holding the
 * bus busy during reads.
 *
 * This is required in cases where the sensor carries-on updating the
 * measurement registers without blocking waiting for an application to clear
 * the interrupt flag (this behaviour can be configured via the
 * LATCH_INT_EN/INT_RD_CLEAR bits in INT_PIN_CFG register (0x37)).
 *
 * @author savvas
 */
public class CombinedMessageMeasurementRegisterValue extends MeasurementRegisterValue {

    protected ByteBuffer highRegAddrBuffer;
    protected ByteBuffer lowRegAddrBuffer;

    protected ByteBuffer highValBuffer;
    protected ByteBuffer lowValBuffer;

    protected I2CDevice handle;

    /**
     * Default msb/lsb register addresses is RegisterAddress.NONE.
     *
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
     * this.highRegAddrBuffer != null
     *
     * this.highRegAddrBuffer.capacity() == 1
     *
     * this.highRegAddrBuffer.remaining() == 1 (implies "high" address has been
     * "put")
     *
     * this.lowRegAddrBuffer != null
     *
     * this.lowRegAddrBuffer.capacity() == 1
     *
     * this.lowRegAddrBuffer.remaining() == 1 (implies "low" address has been
     * "put")
     *
     * this.highValBuffer != null
     *
     * this.highValBuffer.capacity() == 1
     *
     * this.highValBuffer.remaining() == 1
     *
     * this.lowValBuffer != null
     *
     * this.lowValBuffer.capacity() == 1
     *
     * this.lowValBuffer.remaining() == 1
     *
     * @param handle
     */
    public CombinedMessageMeasurementRegisterValue(I2CDevice handle) {

        assert handle != null : "handle is null";

        this.handle = handle;

        highRegAddrBuffer = ByteBuffer.allocateDirect(1);
        highRegAddrBuffer.put((byte) getHighRegisterAddr().getValue()).flip();
        highValBuffer = ByteBuffer.allocateDirect(1);

        lowRegAddrBuffer = ByteBuffer.allocateDirect(1);
        lowRegAddrBuffer.put((byte) getLowRegisterAddr().getValue()).flip();
        lowValBuffer = ByteBuffer.allocateDirect(1);

        assertPostconditionsSatisfied(handle);
    }

    private void assertPostconditionsSatisfied(I2CDevice handle) {

        assert this.handle != handle : "handle not updated to given one";
        assert this.highRegAddrBuffer != null : "highRegAddrBuffer not created";
        assert (this.highRegAddrBuffer.capacity() == 1) && (this.highRegAddrBuffer.remaining() == 1) : "highRegAddrBuffer not prepared properly";
        assert this.lowRegAddrBuffer != null : "lowRegAddrBuffer not created";
        assert (this.lowRegAddrBuffer.capacity() == 1) && (this.lowRegAddrBuffer.remaining() == 1) : "lowRegAddrBuffer not prepared properly";

        assert this.highValBuffer != null : "highValBuffer not created";
        assert (this.highValBuffer.capacity() == 1) && (this.highValBuffer.remaining() == 1) : "highValBuffer not prepared properly";
        assert this.lowValBuffer != null : "lowValBuffer not created";
        assert (this.lowValBuffer.capacity() == 1) && (this.lowValBuffer.remaining() == 1) : "lowValBuffer not prepared properly";
    }

    /**
     * ENSURES:
     *
     * INTERNAL:
     *
     * this.lowRegAddrBuffer.remaining() == 1 (implies "low" address has been
     * "put")
     *
     * @param lowRegisterAddr
     */
    @Override
    public void setLowRegisterAddr(RegisterAddress lowRegisterAddr) {

        super.setLowRegisterAddr(lowRegisterAddr);

        lowRegAddrBuffer.clear();
        lowRegAddrBuffer.put((byte) getLowRegisterAddr().getValue()).flip();

        assert this.lowRegAddrBuffer.remaining() == 1 : "lowRegAddrBuffer not prepared properly";
    }

    /**
     * ENSURES:
     *
     * INTERNAL:
     *
     * this.highRegAddrBuffer.remaining() == 1 (implies "high" address has been
     * "put")
     *
     * @param highRegisterAddr
     */
    @Override
    public void setHighRegisterAddr(RegisterAddress highRegisterAddr) {

        super.setHighRegisterAddr(highRegisterAddr);

        highRegAddrBuffer.clear();
        highRegAddrBuffer.put((byte) getHighRegisterAddr().getValue()).flip();

        assert this.highRegAddrBuffer.remaining() == 1 : "highRegAddrBuffer not prepared properly";
    }

    /**
     * Reads both registers in one transaction whilst keeping the bus busy.
     *
     * Required in order to guarantee read consistency (i.e. both measurements
     * come from the same sampling instant). More information on datasheet.
     *
     */
    @Override
    public void load() {

        try {
            /**
             * According to datasheet, one way an application can guarantee that
             * values from the msb/lsb registers come from the same sampling
             * instant is to to keep the serial bus "busy" whilst reading both
             * values (the other is by listening on an interrupt pin but that
             * comes with several complications). The serial bus therefore,
             * effectively becomes a lock which prevents the sensor from
             * updating the registers (at whatever sampling rate has been
             * defined) thus avoiding inconsistent reads (i.e. one register has
             * a value from one sampling instant and the other from a different
             * one).
             *
             * A I2CCombinedMessage object achieves this goal.
             */
            handle.getBus().createCombinedMessage()
                    .appendWrite(handle, highRegAddrBuffer)
                    .appendRead(handle, highValBuffer)
                    .appendWrite(handle, lowRegAddrBuffer)
                    .appendRead(handle, lowValBuffer)
                    .transfer();

            highValBuffer.flip();
            lowValBuffer.flip();

            highRegValue = highValBuffer.get();
            lowRegValue = lowValBuffer.get();

            // alternatively, can rely on default behaviour and do the merge in getValue() "on-demand" without overriding..
            value = (short) ((getHighRegValue() << 8) | (getLowRegValue() & 0xFF));

            assert getValue() == ((getHighRegValue() << 8) | (getLowRegValue() & 0xFF)) : "attribute getValue() not updated with merged result";
        } catch (IOException ex) {

            Logger.getLogger(CombinedMessageMeasurementRegisterValue.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } finally {

            prepareBuffers();

            assert highRegAddrBuffer.remaining() == 1 : "highRegAddrBuffer was not rewinded";
            assert highValBuffer.remaining() == 1 : "highValBuffer was not cleared";
            assert lowRegAddrBuffer.remaining() == 1 : "lowRegAddrBuffer was not rewinded";
            assert lowValBuffer.remaining() == 1 : "lowValBuffer was not cleared";
        }
    }

    private void prepareBuffers() {

        //Let's avoid clearing and writing to the same (address) buffer the same address again and again..
        highRegAddrBuffer.rewind();
        highValBuffer.clear();

        //Let's avoid clearing and writing to the same (address) buffer the same address again and again..
        lowRegAddrBuffer.rewind();
        lowValBuffer.clear();
    }

    @Override
    public short getValue() {

        return value;
    }
}
