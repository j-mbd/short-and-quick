package jmbd.spi.mcp49x1;

import java.io.IOException;
import javax.microedition.midlet.MIDletStateChangeException;
import jdk.dio.Device;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.spibus.SPIDevice;
import jdk.dio.spibus.SPIDeviceConfig;
import jmbd.commons.CommonOperationsMIDlet;
import jmbd.commons.TimeDelay;

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
 *
 * @author savvas
 */
public class MCP49x1Device extends CommonOperationsMIDlet {

    private static final int SPI_DEVICE_ID = 300;
    private static final int CS_PIN_NUM = 17;
    private static final int LDAC_PIN_NUM = 18;

    // Configuration values in case the device id is not used
    private static final int SPI_CONTROLLER_NUMBER = 0;
    private static final int SPI_CONTROLLER_ADDRESS = 0;
    private static final int MCP4901_CLOCK_FREQUENCY = 20_000_000;
    private static final int MCP4901_CLOCK_MODE = 0;
    private static final int MCP4901_WORD_LENGTH = 16;

    private SPIDevice mcp4901Device;
    private GPIOPin csPin;

    private Mcp49x1SpiCommand mcp49x1SpiCommand;
    private TimeDelay timeDelay;

    @Override
    protected void startApp() throws MIDletStateChangeException {
        try {
            mcp4901Device = mcp4901Device();
            csPin = outPin(CS_PIN_NUM);
            timeDelay = new TimeDelay();

            mcp49x1SpiCommand = mcp4901SpiCommand();

            //  step-up to 5V
            for (int d = mcp49x1SpiCommand.getMinDataValue(); d <= mcp49x1SpiCommand.getMaxDataValue(); d++) {
                if (d >= mcp49x1SpiCommand.getMinDataValue() && d <= mcp49x1SpiCommand.getMaxDataValue()) {
                    mcp49x1SpiCommand.setData(d);
                    mcp49x1SpiCommand.write();
                    System.out.println("Data with value \"" + d + "\" written...");
                } else {
                    System.out.println("data value \"" + d + "\" not within [" + mcp49x1SpiCommand.getMinDataValue() + " - " + mcp49x1SpiCommand.getMaxDataValue() + "] range. Will not be written to device");
                }
                timeDelay.pauseMillis(2_000);
            }

            // now step-down to 0V again..
            for (int d = mcp49x1SpiCommand.getMaxDataValue(); d >= mcp49x1SpiCommand.getMinDataValue(); d--) {
                if (d >= mcp49x1SpiCommand.getMinDataValue() && d <= mcp49x1SpiCommand.getMaxDataValue()) {
                    mcp49x1SpiCommand.setData(d);
                    mcp49x1SpiCommand.write();
                    System.out.println("Data with value \"" + d + "\" written...");
                } else {
                    System.out.println("data value \"" + d + "\" not within [" + mcp49x1SpiCommand.getMinDataValue() + " - " + mcp49x1SpiCommand.getMaxDataValue() + "] range. Will not be written to device");
                }
                timeDelay.pauseMillis(2_000);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Mcp4901SpiCommand mcp4901SpiCommand() {

        Mcp4901SpiCommand cmd = new Mcp4901SpiCommand(new Mcp49x1SpiSlave(mcp4901Device, csPin));

        cmd.accepted();
        cmd.unbuffered();
        cmd.outputGainX1();
        cmd.enabled();

        return cmd;
    }

    private SPIDevice mcp4901Device() throws IOException {

        // Not really using configuration (using id) by in case we need to...
        SPIDeviceConfig.Builder b = new SPIDeviceConfig.Builder();
        b.setClockFrequency(MCP4901_CLOCK_FREQUENCY);
        b.setClockMode(MCP4901_CLOCK_MODE);
        b.setWordLength(MCP4901_WORD_LENGTH);
        b.setAddress(SPI_CONTROLLER_ADDRESS);
        b.setControllerNumber(SPI_CONTROLLER_NUMBER);
        b.setBitOrdering(Device.BIG_ENDIAN);
        b.setCSActiveLevel(SPIDeviceConfig.CS_ACTIVE_HIGH);

        return DeviceManager.open(SPI_DEVICE_ID);
    }

    private GPIOPin outPin(int pinNum) throws IOException {

        GPIOPinConfig.Builder b = new GPIOPinConfig.Builder();
        b.setPinNumber(pinNum);
        b.setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY);
        b.setInitValue(true);

        return DeviceManager.open(b.build());
    }

    @Override
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {

        closeIgnoringExceptions(mcp4901Device);
        closeIgnoringExceptions(csPin);
    }
}
