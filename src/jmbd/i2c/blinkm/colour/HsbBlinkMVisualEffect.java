package jmbd.i2c.blinkm.colour;

import jmbd.i2c.blinkm.command.BlinkMCommandExecution;

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
 * INVARIANTS:
 *
 * 1) H,S,B values between [0 TO 255]
 *
 * 2) H,S,B randomness values between [0 TO 255]
 *
 * @author savvas
 */
public class HsbBlinkMVisualEffect extends BlinkMVisualEffect {

    private static final char FADE_TO_HSB_COLOUR_MNIC = 'h';
    private static final char FADE_TO_RANDOM_HSB_COLOUR_MNIC = 'H';

    private boolean hueSet;

    public HsbBlinkMVisualEffect(BlinkMCommandExecution commandExecution) {

        super(commandExecution);
    }

    /**
     * REQUIRES:
     *
     * h between [0 TO 255]
     *
     * @param h
     */
    public void setTargetH(short h) {

        setQuantityA(h);
    }

    public short getTargetH() {

        return getQuantityA();
    }

    /**
     * REQUIRES:
     *
     * s between [0 TO 255]
     *
     * @param s
     */
    public void setTargetS(short s) {

        setQuantityB(s);
    }

    public short getTargetS() {

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
     * hRandomness between [0 TO 255]
     *
     * @param hRandomness
     */
    public void setTargetHRandomness(short hRandomness) {

        setQuantityARandomness(hRandomness);
    }

    public short getTargetHRandomness() {

        return getQuantityARandomness();
    }

    /**
     * REQUIRES:
     *
     * sRandomness between [0 TO 255]
     *
     * @param sRandomness
     */
    public void setTargetSRandomness(short sRandomness) {

        setQuantityBRandomness(sRandomness);
    }

    public short getTargetSRandomness() {

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

    /**
     * Gradually updates the device with the currently set h,s,b values at a
     * rate determined by the currently configured "fadeApply speed".
     *
     * A hue (h) is the raw colour. Saturation (s) is the vividness of the
     * colour. A saturation of 0 means a very light/white colour and a
     * saturation of 255 means a very vivid colour. A brightness (b) of 0
     * results in total dark whereas one of 255 results in maximum brightness.
     *
     * ENSURES:
     *
     * "hue-set" flag is on
     *
     */
    public void fadeApply() {

        byte[] cmd = {FADE_TO_HSB_COLOUR_MNIC, (byte) getTargetH(), (byte) getTargetS(), (byte) getTargetB()};
        commandExecution.runWithNoReturnValue(cmd);

        hueSet = true;

        assert (hueSet == true) : "Hue-set flag is not on";
    }

    /**
     * Gradually updates the device with a random colour determined by the
     * currently configured "randomness" H,S,B values.
     *
     * (A value of "0" results in no change at all)
     *
     * REQUIRES:
     *
     * Device configured with a hue (i.e. hueSet() == true) by means of calling
     * the fadeApply() method.
     *
     */
    public void fadeToRandomApply() {

        assert hueSet() : "Device is not configured with a hue";

        byte[] cmd = {FADE_TO_RANDOM_HSB_COLOUR_MNIC, (byte) getTargetHRandomness(), (byte) getTargetSRandomness(), (byte) getTargetBRandomness()};
        commandExecution.runWithNoReturnValue(cmd);
    }

    // Is device configured with a hue?
    public boolean hueSet() {

        return (hueSet);
    }
}
