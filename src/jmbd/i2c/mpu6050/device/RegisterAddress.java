package jmbd.i2c.mpu6050.device;

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
public enum RegisterAddress {

    NONE(0),
    // Accelerometer
    ACCEL_XOUT_H(0x3B),
    ACCEL_XOUT_L(0x3C),
    ACCEL_YOUT_H(0x3D),
    ACCEL_YOUT_L(0x3E),
    ACCEL_ZOUT_H(0X3F),
    ACCEL_ZOUT_L(0X40),
    // Gyroscope
    GYRO_XOUT_H(0x43),
    GYRO_XOUT_L(0x44),
    GYRO_YOUT_H(0x45),
    GYRO_YOUT_L(0x46),
    GYRO_ZOUT_H(0x47),
    GYRO_ZOUT_L(0x48),
    // Configuration
    ACCEL_CONFIG(0x1C),
    GYRO_CONFIG(0x1B),
    INT_PIN_CFG(0x37),
    // Rate divider
    SMPRT_DIV(0x19),
    // Datasheet calls this "config" but only deals with Frame Synchronization (FSYNC) the Digital Low Pass Filter (DLPF). Let's keep the same name for consistency..
    CONFIG(0x1A),
    // Interrupt enable & status
    INT_ENABLE(0x38),
    INT_STATUS(0x3A),
    // Power management
    PWR_MNG_CONFIG(0x6B),
    TEMP_OUT_H(0x41),
    TEMP_OUT_L(0x42);

    private final int value;

    private RegisterAddress(int address) {
        this.value = address;
    }

    public int getValue() {
        return value;
    }
}
