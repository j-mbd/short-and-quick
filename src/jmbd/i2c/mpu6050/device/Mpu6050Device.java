package jmbd.i2c.mpu6050.device;

import jmbd.commons.CommonOperationsMIDlet;
import jmbd.commons.TimeDelay;
import jmbd.i2c.mpu6050.configuration.InterruptSource;
import jmbd.i2c.mpu6050.configuration.Mpu6050DeviceConfiguration;
import jmbd.i2c.mpu6050.interrupt.TemperatureEventListener;
import jmbd.i2c.mpu6050.register.configuration.I2CRegisterValue;
import jmbd.i2c.mpu6050.register.configuration.RegisterValue;
import jmbd.i2c.mpu6050.register.measurement.CombinedMessageMeasurementRegisterValue;
import jmbd.i2c.mpu6050.register.measurement.MultiReadMeasurementRegisterValue;
import jmbd.i2c.mpu6050.interrupt.UdpDispatchTemperatureEventListener;
import java.io.IOException;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

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
public class Mpu6050Device extends CommonOperationsMIDlet {

    private static final int SENSOR_ADDRESS = 0x68;
    private static final int IC2_BUS_NUMBER = 1;
    private static final int DEFAULT_ADDRESS_SIZE = 7;
    private static final int INTERRUPT_PIN = 17;

    private I2CDevice mpu6050;

    private RegisterValue regValue;
    private Mpu6050DeviceConfiguration configuration;
    private Mpu6050Sensor sensor;
    private TimeDelay timeDelay;

    private GPIOPin interruptPin;
    private TemperatureEventListener interruptListener;

    @Override
    public void startApp() {

        try {
            mpu6050 = buildDevice();

            regValue = new I2CRegisterValue(mpu6050);
            configuration = new Mpu6050DeviceConfiguration(regValue);
            sensor = new Mpu6050Sensor(new CombinedMessageMeasurementRegisterValue(mpu6050));
            timeDelay = new TimeDelay();

            // Not much can be done unless we wake device up..
            configuration.wakeUp();

            //turnOffInterrupts();
            timeDelay.pauseMillis(2_00);

            turnOnInterrupts();
            timeDelay.pauseMillis(5_00);

            System.out.println("Starting listener..");
            //interruptPin = buildInputPin();

            // Driver will complain if sensor values are read in multiple places so either uncomment the listener or the below but not both..
            printMeasurements(200);
        } catch (IOException ex) {

            throw new RuntimeException(ex);
        }
    }

    private void turnOnInterrupts() {

        regValue.setRegisterAddress(RegisterAddress.INT_PIN_CFG);
        regValue.load();
        System.out.println("Current value of INT_PIN_CFG is " + regValue);
        // From datasheet: "When this bit is equal to 1, the INT pin is held high until the interrupt is cleared"
        regValue.setBit(5);
        regValue.store();
        System.out.println("Current value of INT_PIN_CFG is " + regValue);

        // interrupts related stuff
        regValue.setRegisterAddress(RegisterAddress.INT_ENABLE);
        regValue.load();

        regValue.setRegisterAddress(RegisterAddress.SMPRT_DIV);
        regValue.wipeOut();
        // make SMPLRT_DIV == 7 so that sampling rate becomes 1KHz (see register SMPRT_DIV(0x19) for details)
        // WARNING: Any rate > 1KHz will kill the runtime almost instantly (a "kworker" process is keeping rather busy too..)
        regValue.setBit(0);
        regValue.setBit(1);
        regValue.setBit(2);

        regValue.store();
        // let's print what we just stored
        regValue.load();
        System.out.println("SMPRT_DIV val: " + regValue);

        // CONFIG - 0x1A
        regValue.setRegisterAddress(RegisterAddress.CONFIG);
        regValue.load();
        System.out.println("CONFIG val: " + regValue);
        // zero-out all bits
        regValue.wipeOut();
        regValue.store();
        System.out.println("CONFIG val: " + regValue);

        System.out.println("Starting interrupts..");

        configuration.enableInterruptsForSource(InterruptSource.DATA_RDY);
        //timeDelay.pauseMillis(300_000);
    }

    private void turnOffInterrupts() {

        System.out.println("Stopping interrupts..");

        configuration.disableInterruptsForSource(InterruptSource.DATA_RDY);
    }

    private void printMeasurements(int times) {

        for (int i = 0; i < times; i++) {
            System.out.println("Accelerometer measurements");

            System.out.println("\t\tx-axis: " + sensor.getXAccel());
            System.out.println("\t\ty-axis: " + sensor.getYAccel());
            System.out.println("\t\tz-axis: " + sensor.getZAccel());

            System.out.println();

            System.out.println("Gyroscope measurements");
            System.out.println("\t\tx-axis: " + sensor.getXGyro());
            System.out.println("\t\ty-axis: " + sensor.getYGyro());
            System.out.println("\t\tz-axis: " + sensor.getZGyro());

            System.out.println();

            System.out.println("Current temperature is: " + sensor.getTemperature());

            System.out.println("*****************************************************************");

            timeDelay.pauseMillis(5_00);
        }
    }

    @Override
    public void destroyApp(boolean unconditional) {

        closeIgnoringExceptions(mpu6050);
        closeIgnoringExceptions(interruptPin);
        closeIgnoringExceptions(interruptListener);
        turnOffInterrupts();
    }

    private I2CDevice buildDevice() throws IOException {

        I2CDeviceConfig.Builder b = new I2CDeviceConfig.Builder();
        b.setAddress(SENSOR_ADDRESS, DEFAULT_ADDRESS_SIZE).setControllerNumber(IC2_BUS_NUMBER);

        return DeviceManager.open(b.build());
    }

    private GPIOPin buildInputPin() throws IOException {

        GPIOPinConfig.Builder b = new GPIOPinConfig.Builder();
        b.setPinNumber(INTERRUPT_PIN);
        b.setDirection(GPIOPinConfig.DIR_INPUT_ONLY);
        b.setTrigger(GPIOPinConfig.TRIGGER_BOTH_EDGES);
        //b.setDriveMode(GPIOPinConfig.MODE_INPUT_PULL_UP);

        GPIOPin p = DeviceManager.open(b.build());

        // Can use "transactional" reads or serialised individual reads depending on sensor configuration
        //Mpu6050Sensor s = new Mpu6050Sensor(new CombinedMessageMeasurementRegisterValue(mpu6050));
        Mpu6050Sensor s = new Mpu6050Sensor(new MultiReadMeasurementRegisterValue(new I2CRegisterValue(mpu6050)));
        p.setInputListener(new UdpDispatchTemperatureEventListener(new I2CRegisterValue(mpu6050), s));

        return p;
    }
}
