package jmbd.i2c.mpu6050.device;

import jmbd.i2c.mpu6050.configuration.AccelFullScaleRange;
import jmbd.i2c.mpu6050.configuration.GyroFullScaleRange;
import jmbd.i2c.mpu6050.register.measurement.MeasurementRegisterValue;

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
public class Mpu6050Sensor implements AccelGyroTempSensor {

    private MeasurementRegisterValue registerValue;

    private AccelFullScaleRange accelFullScale;
    private GyroFullScaleRange gyroFullScale;

    /**
     * REQUIRES:
     *
     * registerValue not null
     *
     * ENSURES:
     *
     * getAccelFullScale() == AccelFullScaleRange.G_2
     *
     * getGyroFullScale() == GyroFullScaleRange.PLUS_MINUS_250
     *
     * INTERNAL:
     *
     * this.registerValue = registerValue
     *
     * @param registerValue
     */
    public Mpu6050Sensor(MeasurementRegisterValue registerValue) {

        assert registerValue != null : "registerValue is null";

        this.registerValue = registerValue;
        accelFullScale = AccelFullScaleRange.G_2;
        gyroFullScale = GyroFullScaleRange.PLUS_MINUS_250;

        assert this.registerValue == registerValue : "registerValue not set to the given one";
        assert getAccelFullScale() == AccelFullScaleRange.G_2 : "AccelFullScale not set to its default value";
        assert getGyroFullScale() == GyroFullScaleRange.PLUS_MINUS_250 : "GyroFullScale not set to its default value";
    }

    /**
     * REQUIRES:
     *
     * registerValue not null
     *
     * ENSURES:
     *
     * INTERNAL:
     *
     * this.registerValue = registerValue
     *
     * @param registerValue
     */
    public void setRegisterValue(MeasurementRegisterValue registerValue) {

        assert registerValue != null : "registerValue is null";

        this.registerValue = registerValue;

        assert this.registerValue == registerValue : "registerValue not set to the given one";
    }

    /**
     * Acceleration on the X-axis.
     *
     * @return
     */
    @Override
    public short getXAccel() {

        return readFromAccelRegisters(RegisterAddress.ACCEL_XOUT_H, RegisterAddress.ACCEL_XOUT_L);
    }

    /**
     * Acceleration on the Y-axis.
     *
     * @return
     */
    @Override
    public short getYAccel() {

        return readFromAccelRegisters(RegisterAddress.ACCEL_YOUT_H, RegisterAddress.ACCEL_YOUT_L);
    }

    /**
     * Acceleration on the Z-axis.
     *
     * @return
     */
    @Override
    public short getZAccel() {

        return readFromAccelRegisters(RegisterAddress.ACCEL_ZOUT_H, RegisterAddress.ACCEL_ZOUT_L);
    }

    /**
     * Current temperature.
     *
     * @return
     */
    @Override
    public float getTemperature() {

        registerValue.setHighRegisterAddr(RegisterAddress.TEMP_OUT_H);
        registerValue.setLowRegisterAddr(RegisterAddress.TEMP_OUT_L);

        registerValue.load();
        // not magic numbers..just part of the formula really...
        return ((float) registerValue.getValue() / 340) + 36.53f;
    }

    @Override
    public short getXGyro() {

        return readFromGyroRegisters(RegisterAddress.GYRO_XOUT_H, RegisterAddress.GYRO_XOUT_L);
    }

    @Override
    public short getYGyro() {

        return readFromGyroRegisters(RegisterAddress.GYRO_YOUT_H, RegisterAddress.GYRO_YOUT_L);
    }

    @Override
    public short getZGyro() {

        return readFromGyroRegisters(RegisterAddress.GYRO_ZOUT_H, RegisterAddress.GYRO_ZOUT_L);
    }

    private short readFromGyroRegisters(RegisterAddress high, RegisterAddress low) {

        registerValue.setHighRegisterAddr(high);
        registerValue.setLowRegisterAddr(low);

        registerValue.load();

        float scaled = registerValue.getValue() / gyroFullScale.getSensitivity();

        return (short) scaled;
    }

    private short readFromAccelRegisters(RegisterAddress high, RegisterAddress low) {

        registerValue.setHighRegisterAddr(high);
        registerValue.setLowRegisterAddr(low);

        registerValue.load();

        int scaled = registerValue.getValue() / accelFullScale.getSensitivity();

        return (short) scaled;
    }

    /**
     * Default is 2G.
     *
     * REQUIRES:
     *
     * accelerometerFullScale not null
     *
     * ENSURES:
     *
     * getAccelFullScale() == accelFullScale
     *
     * @param accelFullScale
     */
    public void setAccelFullScaleRange(AccelFullScaleRange accelFullScale) {

        assert accelFullScale != null : "accelerometerScale is null";

        this.accelFullScale = accelFullScale;

        assert getAccelFullScale() == accelFullScale : "accelFullScale not set to given value";
    }

    public AccelFullScaleRange getAccelFullScale() {

        return accelFullScale;
    }

    /**
     * Default is (+-)250/s.
     *
     * REQUIRES:
     *
     * accelerometerFullScale not null
     *
     * ENSURES:
     *
     * getGyroFullScale() == gyroFullScale
     *
     * @param gyroFullScale
     */
    public void setGyroFullScaleRange(GyroFullScaleRange gyroFullScale) {

        assert gyroFullScale != null : "gyroscopeScale is null";

        this.gyroFullScale = gyroFullScale;

        assert getGyroFullScale() == gyroFullScale : "gyroFullScale not set to given value";
    }

    public GyroFullScaleRange getGyroFullScale() {

        return gyroFullScale;
    }
}
