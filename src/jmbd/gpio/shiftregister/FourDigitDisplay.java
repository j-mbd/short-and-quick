package jmbd.gpio.shiftregister;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
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
 * Drives a four-digit, seven-segment LED display with the use of two
 * shift-registers joined together in a cascading fashion.
 *
 * Tested by means of a "four-digit display" emulation. Output of first
 * shift-register was wired to a seven-digit display whilst the four LSBs of the
 * second shift-register were connected to four LEDs indicating which digit the
 * display is showing at any given time. (A poor man's four-digit display
 * perhaps..)
 *
 */
public class FourDigitDisplay extends CommonOperationsMIDlet {

    private static final Map<String, Integer> NUMBER_TO_REGISTER = numbersMapping();

    // Bit-pattern to be OR-ed with main shift-register value.
    private static final short[] DISPLAY_SELECTOR_MASKS = {0b1000, 0b0100, 0b0010, 0b0001};

    private static final int SHIFT_REGISTER_SIZE = 16;

    private static final int DATA_PIN_NUMBER = 18;
    private static final int LATCH_PIN_NUM = 23;
    private static final int CLOCK_PIN_NUM = 24;

    private GPIOPin dataPin;
    private GPIOPin latchPin;
    private GPIOPin clockPin;

    private ShiftRegister shiftRegister;

    private TimeDelay timeDelay;

    @Override
    public void startApp() {

        try {
            dataPin = outPin(DATA_PIN_NUMBER);
            latchPin = outPin(LATCH_PIN_NUM);
            clockPin = outPin(CLOCK_PIN_NUM);

            shiftRegister = shiftRegister();

            timeDelay = new TimeDelay();

            for (short i = 0; i < 1_000; i++) {
                showNumber(i);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // REQUIRES: (num >= 0) & (num <= 9999)
    private void showNumber(short num) {

        assert num >= 0 && num <= 9999 : "Number " + num + " is outside the expected range [0 - 9999]";

        System.out.println("Displaying number: " + num);
        String asString = padIfLengthNotRight(String.valueOf(num), 4);

        for (short i = 3; i >= 0; i--) {

            short result = DISPLAY_SELECTOR_MASKS[i];
            result <<= 8;

            String nc = asString.substring(i, i + 1);
            int shiftRegisterVal = NUMBER_TO_REGISTER.get(nc);

            // silently casts shiftRegisterVal to short
            result += shiftRegisterVal;

            unload(result);
        }
    }

    // pads with zeros to the left until desired size is reached
    private String padIfLengthNotRight(String num, int size) {

        while (num.length() < size) {
            num = "0" + num;
        }

        return num;
    }

    private void unload(short num) {

        shiftRegister.clearLatchOutput();

        while (!shiftRegister.fullyLoaded()) {
            boolean bit = (num & 1) == 1;
            shiftRegister.load(bit);
            num >>= 1;
        }
        shiftRegister.unload();
        timeDelay.pauseMillis(200);
    }

    private ShiftRegister shiftRegister() {

        // Can be controlled by app prop
        // OrderConfigurableShiftRegister sr = new ArrayOrderConfigurableShiftRegister();
        OrderConfigurableShiftRegister ocsr = new DequeOrderConfigurableShiftRegister();

        ocsr.setDataPin(dataPin);
        ocsr.setLatchPin(latchPin);
        ocsr.setClockPin(clockPin);
        ocsr.maxLoadBits(SHIFT_REGISTER_SIZE);
        ocsr.makeNaturalOrder();

        return ocsr;
    }

    private GPIOPin outPin(int pNum) throws IOException {

        GPIOPinConfig.Builder pBuilder = new GPIOPinConfig.Builder();
        pBuilder.setPinNumber(pNum);
        pBuilder.setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY);

        return DeviceManager.open(pBuilder.build());
    }

    @Override
    public void destroyApp(boolean unconditional) {

        closeIgnoringExceptions(dataPin);
        closeIgnoringExceptions(latchPin);
        closeIgnoringExceptions(clockPin);
    }

    /**
     * Mappings of numbers to bit-patterns the shift-register needs to be loaded
     * with in order to display a number on a seven segment display.
     *
     * @return
     */
    private static Map<String, Integer> numbersMapping() {

        Map<String, Integer> regMapping = new LinkedHashMap<>();

        regMapping.put("9", 0b01101111);
        regMapping.put("8", 0b01111111);
        regMapping.put("7", 0b00000111);
        regMapping.put("6", 0b01111101);
        regMapping.put("5", 0b01101101);
        regMapping.put("4", 0b01100110);
        regMapping.put("3", 0b01001111);
        regMapping.put("2", 0b01011011);
        regMapping.put("1", 0b00000110);
        regMapping.put("0", 0b00111111);
        regMapping.put(".", 0b10000000);

        return regMapping;
    }
}
