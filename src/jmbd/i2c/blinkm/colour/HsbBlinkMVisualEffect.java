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
    public void setH(short h) {

        setQuantityA(h);
    }

    public short getH() {

        return getQuantityA();
    }

    /**
     * REQUIRES:
     *
     * s between [0 TO 255]
     *
     * @param s
     */
    public void setS(short s) {

        setQuantityB(s);
    }

    public short getS() {

        return getQuantityB();
    }

    /**
     * REQUIRES:
     *
     * b between [0 TO 255]
     *
     * @param b
     */
    public void setB(short b) {

        setQuantityC(b);
    }

    public short getB() {

        return getQuantityC();
    }

    /**
     * REQUIRES:
     *
     * hRandomness between [0 TO 255]
     *
     * @param hRandomness
     */
    public void setHRandomness(short hRandomness) {

        setQuantityARandomness(hRandomness);
    }

    public short getHRandomness() {

        return getQuantityARandomness();
    }

    /**
     * REQUIRES:
     *
     * sRandomness between [0 TO 255]
     *
     * @param sRandomness
     */
    public void setSRandomness(short sRandomness) {

        setQuantityBRandomness(sRandomness);
    }

    public short getSRandomness() {

        return getQuantityBRandomness();
    }

    /**
     * REQUIRES:
     *
     * bRandomness between [0 TO 255]
     *
     * @param bRandomness
     */
    public void setBRandomness(short bRandomness) {

        setQuantityCRandomness(bRandomness);
    }

    public short getBRandomness() {

        return getQuantityCRandomness();
    }

    /**
     * A no-op.
     *
     * (not specified for HSB colours)
     */
    @Override
    protected void apply() {
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
    @Override
    public void fadeApply() {

        byte[] cmd = {FADE_TO_HSB_COLOUR_MNIC, (byte) getH(), (byte) getS(), (byte) getB()};
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
    @Override
    public void fadeToRandomApply() {

        assert hueSet() : "Device is not configured with a hue";

        byte[] cmd = {FADE_TO_RANDOM_HSB_COLOUR_MNIC, (byte) getHRandomness(), (byte) getSRandomness(), (byte) getBRandomness()};
        commandExecution.runWithNoReturnValue(cmd);
    }

    // Is device configured with a hue?
    public boolean hueSet() {

        return (hueSet);
    }
}
