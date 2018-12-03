package jmbd.i2c.mpu6050.configuration;

import jmbd.i2c.mpu6050.register.configuration.RegisterValue;
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
 * @author savvas
 */
public class Mpu6050DeviceConfiguration {

    protected RegisterValue registerValue;

    /**
     * REQUIRES:
     *
     * registerValue not null
     *
     * @param registerValue
     */
    public Mpu6050DeviceConfiguration(RegisterValue registerValue) {

        assert registerValue != null : "registerValue is null";

        this.registerValue = registerValue;
    }

    /**
     * REQUIRES:
     *
     * registerValue not null
     *
     * @param registerValue
     */
    public void setRegisterValue(RegisterValue registerValue) {

        assert registerValue != null : "registerValue is null";

        this.registerValue = registerValue;
    }

    /**
     * REQUIRES:
     *
     * newRange not null
     *
     * @param newRange
     */
    public void updateAccelFullScaleRange(AccelFullScaleRange newRange) {

        assert newRange != null : "newRange is null";

        registerValue.setRegisterAddress(RegisterAddress.ACCEL_CONFIG);
        registerValue.load();

        switch (newRange) {
            // All current non-AFS_SEL values need to be preserved so just modify the AFS_SEL bits
            case G_2:
                // precondition met
                registerValue.clearBit(4);
                registerValue.clearBit(3);
                break;
            case G_4:
                // precondition met
                registerValue.clearBit(4);
                registerValue.setBit(3);
                break;
            case G_8:
                // precondition met
                registerValue.setBit(4);
                registerValue.clearBit(3);
                break;
            case G_16:
                // precondition met
                registerValue.setBit(4);
                registerValue.setBit(3);
                break;
        }
        registerValue.store();
    }

    /**
     * REQUIRES:
     *
     * newRange not null
     *
     * @param newRange
     */
    public void updateGyroFullScaleRange(GyroFullScaleRange newRange) {

        assert newRange != null : "newRange is null";

        registerValue.setRegisterAddress(RegisterAddress.GYRO_CONFIG);
        registerValue.load();

        switch (newRange) {
            // All current non-AFS_SEL values need to be preserved so just modify the AFS_SEL bits
            case PLUS_MINUS_250:
                // precondition met
                registerValue.clearBit(4);
                registerValue.clearBit(3);
                break;
            case PLUS_MINUS_500:
                // precondition met
                registerValue.clearBit(4);
                registerValue.setBit(3);
                break;
            case PLUS_MINUS_1000:
                // precondition met
                registerValue.setBit(4);
                registerValue.clearBit(3);
                break;
            case PLUS_MINUS_2000:
                // precondition met
                registerValue.setBit(4);
                registerValue.setBit(3);
                break;
        }
        registerValue.store();
    }

    /**
     * Device first starts-up in "sleep" mode so this method needs to be called
     * if we want to do anything meaningful at all..
     *
     */
    public void wakeUp() {

        registerValue.setRegisterAddress(RegisterAddress.PWR_MNG_CONFIG);
        registerValue.load();
        registerValue.clearBit(6);
        registerValue.store();
    }

    /**
     * REQUIRES:
     *
     * source not null
     *
     * @param source
     */
    public void enableInterruptsForSource(InterruptSource source) {

        assert source != null : "source is null";

        registerValue.setRegisterAddress(RegisterAddress.INT_ENABLE);
        registerValue.load();

        switch (source) {

            case DATA_RDY:
                // precondition met
                registerValue.setBit(0);
                break;
            case I2C_MST_INT:
                // precondition met
                registerValue.setBit(3);
                break;
            case FIFO_OFLOW:
                // precondition met
                registerValue.setBit(4);
                break;
        }
        registerValue.store();
    }

    /**
     * REQUIRES:
     *
     * source not null
     *
     * @param source
     */
    public void disableInterruptsForSource(InterruptSource source) {

        assert source != null : "source is null";

        registerValue.setRegisterAddress(RegisterAddress.INT_ENABLE);

        registerValue.load();

        switch (source) {

            case DATA_RDY:
                // precondition met
                registerValue.clearBit(0);
                break;
            case I2C_MST_INT:
                // precondition met
                registerValue.clearBit(3);
                break;
            case FIFO_OFLOW:
                // precondition met
                registerValue.clearBit(4);
                break;
        }
        registerValue.store();
    }
}
