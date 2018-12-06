package jmbd.spi.mcp49x1;

import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.spibus.SPIDevice;

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
 * Access to an MCP-4901 device (via SPI).
 *
 * @author savvas
 */
public class Mcp49x1SpiSlave {

    protected SPIDevice device;
    protected GPIOPin csPin;

    /**
     * REQUIRES:
     *
     * 1) device not null
     *
     * 2) device is open
     *
     * 3) csPin not null
     *
     * 4) csPin configured for output only
     *
     * 5) csPin open
     *
     * 6) (wordSize % 8) == 0
     *
     * @param device
     * @param csPin
     */
    public Mcp49x1SpiSlave(SPIDevice device, GPIOPin csPin) {

        // pre-conditions applied on each method
        setDevice(device);
        setCsPin(csPin);
    }

    /**
     * REQUIRES:
     *
     * 1) Device not null
     *
     * 2) Device is open
     *
     * @param device
     */
    public void setDevice(SPIDevice device) {

        assert device != null : "SPI device is null";
        assert device.isOpen() : "SPI device is closed";

        this.device = device;
    }

    /**
     * REQUIRES:
     *
     * 1) csPin not null
     *
     * 2) csPin configured for output only
     *
     * 3) csPin open
     *
     * @param csPin
     */
    public void setCsPin(GPIOPin csPin) {

        try {
            assert csPin != null : "GPIO CS device is null";
            assert csPin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY : "GPIO CS device not configured for output only";
            assert csPin.isOpen() : "GPIO CS device is closed";

            this.csPin = csPin;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * REQUIRES:
     *
     * 1) payload != null
     *
     * 2) payload.remaining() == 2 (i.e. word length is 16-bits)
     *
     * @param payload
     */
    public void accept(ByteBuffer payload) {

        assert payload != null : "payload is null";
        assert payload.remaining() == 2 : "Payload doesn't contain exactly two bytes to store";

        try {
            prepareForWrite();
            device.write(payload);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            // let's "high" CS pin in case of exception too since that would leave the device in a consistent state (i.e. non-write mode)
            updateVout();
        }
    }

    protected void prepareForWrite() {

        try {
            // The CS pin must be held low for the entire duration of a write.
            csPin.setValue(false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void updateVout() {

        try {
            // latch-out Vout..CS pin high
            csPin.setValue(true);
            additionalFlush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * NOP in this version, subclasses can override to take into account usage
     * of LDAC pin.
     */
    protected void additionalFlush() {
        // NOP
    }
}
