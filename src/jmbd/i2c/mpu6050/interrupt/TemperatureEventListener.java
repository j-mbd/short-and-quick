package jmbd.i2c.mpu6050.interrupt;

import jmbd.commons.TimeDelay;
import jmbd.i2c.mpu6050.register.configuration.RegisterValue;
import jmbd.i2c.mpu6050.device.AccelGyroTempSensor;
import jmbd.i2c.mpu6050.device.RegisterAddress;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

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
public class TemperatureEventListener implements PinListener, AutoCloseable {

    protected long temperatureEventCount;
    protected long nonTemperatureEventCount;
    protected RegisterValue registerValue;
    protected AccelGyroTempSensor sensor;
    protected TimeDelay timeDelay;

    public TemperatureEventListener(RegisterValue registerValue, AccelGyroTempSensor sensor) {

        this.registerValue = registerValue;
        this.registerValue.setRegisterAddress(RegisterAddress.INT_STATUS);
        this.sensor = sensor;
        // Can be passed in constructor
        this.timeDelay = new TimeDelay();
    }

    @Override
    public void valueChanged(PinEvent event) {

        registerValue.load();

        if (registerValue.bitValueAt(0)) {

            float tempNow = sensor.getTemperature();
            sendOutTemperatureEvent(tempNow);
            ++temperatureEventCount;
        } else {

            ++nonTemperatureEventCount;
        }
        timeDelay.pauseMillis(5_00);
    }

    public long getTemperatureEventCount() {

        return temperatureEventCount;
    }

    public long getNonTemperatureEventCount() {

        return nonTemperatureEventCount;
    }

    /**
     * Event handling logic goes here.
     *
     * Default is to print temperature in console, override for more specific
     * logic.
     *
     * @param temp
     */
    protected void sendOutTemperatureEvent(float temp) {

        System.out.println("Current Temperature is: " + temp);
    }

    @Override
    public void close() throws Exception {
        // noop
    }

    @Override
    public String toString() {

        return "TemperatureEventListener{" + "Total new temperature events:" + getTemperatureEventCount() + ", total non-temperature events:" + getNonTemperatureEventCount() + "}";
    }
}
