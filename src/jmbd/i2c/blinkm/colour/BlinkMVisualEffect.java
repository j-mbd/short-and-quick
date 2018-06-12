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
 *
 * INVARIANTS:
 *
 * 1) Quantity values for A,B,C between [0 TO 255]
 *
 * 2) Quantity value randomness for A,B,C between [0 TO 255]
 *
 * (Used in an "implementation-inheritance" mode. Abstraction-anchoring must be
 * done in subclasses, hence package-scoped)
 *
 * @author savvas
 */
abstract class BlinkMVisualEffect {

    private short quantityA;
    private short quantityB;
    private short quantityC;

    private short quantityARandomness;
    private short quantityBRandomness;
    private short quantityCRandomness;

    protected BlinkMCommandExecution commandExecution;

    protected BlinkMVisualEffect(BlinkMCommandExecution commandExecution) {

        this.commandExecution = commandExecution;
    }

    /**
     * REQUIRES:
     *
     * quantityA between [0 TO 255]
     *
     * @param quantityA
     */
    protected void setQuantityA(short quantityA) {

        assertValueInRange(quantityA);

        this.quantityA = quantityA;
    }

    protected short getQuantityA() {

        return (quantityA);
    }

    /**
     * REQUIRES:
     *
     * quantityB between [0 TO 255]
     *
     * @param quantityB
     */
    protected void setQuantityB(short quantityB) {

        assertValueInRange(quantityB);

        this.quantityB = quantityB;
    }

    protected short getQuantityB() {

        return (quantityB);
    }

    /**
     * REQUIRES:
     *
     * quantityC between [0 TO 255]
     *
     * @param quantityC
     */
    protected void setQuantityC(short quantityC) {

        assertValueInRange(quantityC);

        this.quantityC = quantityC;
    }

    protected short getQuantityC() {

        return (quantityC);
    }

    /**
     * REQUIRES:
     *
     * quantityARandomness between [0 TO 255]
     *
     * @param quantityARandomness
     */
    protected void setQuantityARandomness(short quantityARandomness) {

        assertValueInRange(quantityARandomness);

        this.quantityARandomness = quantityARandomness;
    }

    protected short getQuantityARandomness() {

        return (quantityARandomness);
    }

    /**
     * REQUIRES:
     *
     * quantityBRandomness between [0 TO 255]
     *
     * @param quantityBRandomness
     */
    protected void setQuantityBRandomness(short quantityBRandomness) {

        assertValueInRange(quantityBRandomness);

        this.quantityBRandomness = quantityBRandomness;
    }

    protected short getQuantityBRandomness() {

        return (quantityBRandomness);
    }

    /**
     * REQUIRES:
     *
     * quantityCRandomness between [0 TO 255]
     *
     * @param quantityCRandomness
     */
    protected void setQuantityCRandomness(short quantityCRandomness) {

        assertValueInRange(quantityCRandomness);

        this.quantityCRandomness = quantityCRandomness;
    }

    protected short getQuantityCRandomness() {

        return (quantityCRandomness);
    }

    private void assertValueInRange(short val) {

        assert val >= 0 && val <= 255 : "Value [" + val + "] not in expected range [0 TO 255]";
    }

    protected void clear() {

        quantityA = quantityARandomness = 0;
        quantityB = quantityBRandomness = 0;
        quantityC = quantityCRandomness = 0;
    }

    protected abstract void apply();

    protected abstract void fadeApply();

    protected abstract void fadeToRandomApply();
}
