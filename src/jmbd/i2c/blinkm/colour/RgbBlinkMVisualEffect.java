package jmbd.i2c.blinkm.colour;

import jmbd.i2c.blinkm.command.BlinkMCommandExecution;
import jmbd.commons.ByteConversion;

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
public class RgbBlinkMVisualEffect extends BlinkMVisualEffect {

    private static final char GO_TO_RGB_COLOUR_NOW_MNIC = 'n';
    private static final char FADE_TO_RGB_COLOUR_MNIC = 'c';
    private static final char FADE_TO_RANDOM_RGB_COLOUR_MNIC = 'C';
    private static final char GET_CURRENT_RGB_COLOUR_MNIC = 'g';

    private final ByteConversion byteConversion;

    public RgbBlinkMVisualEffect(BlinkMCommandExecution commandExecution) {

        super(commandExecution);
        byteConversion = new ByteConversion();
    }

    /**
     * REQUIRES:
     *
     * r between [0 TO 255]
     *
     * @param r
     */
    public void setTargetR(short r) {

        setQuantityA(r);
    }

    public short getTargetR() {

        return getQuantityA();
    }

    /**
     * REQUIRES:
     *
     * g between [0 TO 255]
     *
     * @param g
     */
    public void setTargetG(short g) {

        setQuantityB(g);
    }

    public short getTargetG() {

        return getQuantityB();
    }

    /**
     * REQUIRES:
     *
     * b between [0 TO 255]
     *
     * @param b
     */
    public void setTargetB(short b) {

        setQuantityC(b);
    }

    public short getTargetB() {

        return getQuantityC();
    }

    /**
     * REQUIRES:
     *
     * rRandomness between [0 TO 255]
     *
     * @param rRandomness
     */
    public void setTargetRRandomness(short rRandomness) {

        setQuantityARandomness(rRandomness);
    }

    public short getTargetRRandomness() {

        return getQuantityARandomness();
    }

    /**
     * REQUIRES:
     *
     * gRandomness between [0 TO 255]
     *
     * @param gRandomness
     */
    public void setTargetGRandomness(short gRandomness) {

        setQuantityBRandomness(gRandomness);
    }

    public short getTargetGRandomness() {

        return getQuantityBRandomness();
    }

    /**
     * REQUIRES:
     *
     * bRandomness between [0 TO 255]
     *
     * @param bRandomness
     */
    public void setTargetBRandomness(short bRandomness) {

        setQuantityCRandomness(bRandomness);
    }

    public short getTargetBRandomness() {

        return getQuantityCRandomness();
    }

    public RgbBlinkMVisualEffect getCurrentDeviceColour() {

        RgbBlinkMVisualEffect c = new RgbBlinkMVisualEffect(commandExecution);

        byte[] cmd = {GET_CURRENT_RGB_COLOUR_MNIC};
        byte[] retVal = new byte[3]; // Not really a magic-number...just 3 values for R-G-B

        commandExecution.runAndPopulateReturnValue(cmd, retVal);

        byteConversion.setByte(retVal[0]);
        c.setTargetR(byteConversion.asShort());

        byteConversion.setByte(retVal[1]);
        c.setTargetG(byteConversion.asShort());

        byteConversion.setByte(retVal[2]);
        c.setTargetB(byteConversion.asShort());

        return c;
    }

    public void setTargetRgbWithCurrentDeviceColour() {

        RgbBlinkMVisualEffect ve = getCurrentDeviceColour();

        setTargetR(ve.getTargetR());
        setTargetG(ve.getTargetG());
        setTargetB(ve.getTargetB());
    }

    /**
     * Updates the device with the currently set R,G,B values applying no delay.
     *
     */
    public void apply() {

        byte[] cmd = {GO_TO_RGB_COLOUR_NOW_MNIC, (byte) getTargetR(), (byte) getTargetG(), (byte) getTargetB()};

        commandExecution.runWithNoReturnValue(cmd);
    }

    public byte[] applyRawCommand() {

        byte[] cmd = {GO_TO_RGB_COLOUR_NOW_MNIC, (byte) getTargetR(), (byte) getTargetG(), (byte) getTargetB()};

        return cmd;
    }

    /**
     * Gradually updates the device with the currently set r,g,b values at a
     * rate determined by the currently configured "fadeApply speed".
     *
     * A value of "0" results in switching off a channel whereas a value of 255
     * results in maximum brightness.
     *
     */
    public void fadeApply() {

        byte[] cmd = {FADE_TO_RGB_COLOUR_MNIC, (byte) getTargetR(), (byte) getTargetG(), (byte) getTargetB()};

        commandExecution.runWithNoReturnValue(cmd);
    }

    public byte[] fadeRawCommand() {

        byte[] cmd = {FADE_TO_RGB_COLOUR_MNIC, (byte) getTargetR(), (byte) getTargetG(), (byte) getTargetB()};

        return cmd;
    }

    /**
     * Gradually updates the device with a random colour determined by the
     * currently configured "randomness" r,g,b values.
     *
     * A value of "0" results in no change at all.
     *
     */
    public void fadeToRandomApply() {

        byte[] cmd = {FADE_TO_RANDOM_RGB_COLOUR_MNIC, (byte) getTargetRRandomness(), (byte) getTargetGRandomness(), (byte) getTargetBRandomness()};

        commandExecution.runWithNoReturnValue(cmd);
    }

    public byte[] fadeToRandomRawCommand() {

        byte[] cmd = {FADE_TO_RANDOM_RGB_COLOUR_MNIC, (byte) getTargetRRandomness(), (byte) getTargetGRandomness(), (byte) getTargetBRandomness()};

        return cmd;
    }

    /**
     * Sets all values to zero.
     */
    public void makeBlack() {

        super.clear();
    }

    /**
     * Sets all values to 255.
     */
    public void makeWhite() {

        setTargetR((short) 255);
        setTargetG((short) 255);
        setTargetB((short) 255);
    }

    /**
     * Switches-off all colour.
     *
     */
    public void goBlack() {

        makeBlack();
        apply();
    }

    /**
     * Switches-on white colour.
     *
     */
    public void goWhite() {

        makeWhite();
        apply();
    }

    @Override
    public String toString() {

        return "[R:" + getTargetR() + "], [G:" + getTargetG() + "], [B:" + getTargetB() + "]";
    }
}
