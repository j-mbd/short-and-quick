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
abstract class BlinkMColourUpdate {

    private short targetQuantityA;
    private short targetQuantityB;
    private short targetQuantityC;

    private short targetQuantityARandomness;
    private short targetQuantityBRandomness;
    private short targetQuantityCRandomness;

    protected BlinkMCommandExecution commandExecution;

    protected BlinkMColourUpdate(BlinkMCommandExecution commandExecution) {

        this.commandExecution = commandExecution;
    }

    /**
     * REQUIRES:

 targetQuantityA between [0 TO 255]
     *
     * @param quantityA
     */
    protected void setTargetQuantityA(short quantityA) {

        assertValueInRange(quantityA);

        this.targetQuantityA = quantityA;
    }

    protected short getTargetQuantityA() {

        return (targetQuantityA);
    }

    /**
     * REQUIRES:

 targetQuantityB between [0 TO 255]
     *
     * @param targetQuantityB
     */
    protected void setTargetQuantityB(short targetQuantityB) {

        assertValueInRange(targetQuantityB);

        this.targetQuantityB = targetQuantityB;
    }

    protected short getTargetQuantityB() {

        return (targetQuantityB);
    }

    /**
     * REQUIRES:

 targetQuantityC between [0 TO 255]
     *
     * @param targetQuantityC
     */
    protected void setTargetQuantityC(short targetQuantityC) {

        assertValueInRange(targetQuantityC);

        this.targetQuantityC = targetQuantityC;
    }

    protected short getTargetQuantityC() {

        return (targetQuantityC);
    }

    /**
     * REQUIRES:

 targetQuantityARandomness between [0 TO 255]
     *
     * @param targetQuantityARandomness
     */
    protected void setTargetQuantityARandomness(short targetQuantityARandomness) {

        assertValueInRange(targetQuantityARandomness);

        this.targetQuantityARandomness = targetQuantityARandomness;
    }

    protected short getTargetQuantityARandomness() {

        return (targetQuantityARandomness);
    }

    /**
     * REQUIRES:

 targetQuantityBRandomness between [0 TO 255]
     *
     * @param targetQuantityBRandomness
     */
    protected void setTargetQuantityBRandomness(short targetQuantityBRandomness) {

        assertValueInRange(targetQuantityBRandomness);

        this.targetQuantityBRandomness = targetQuantityBRandomness;
    }

    protected short getTargetQuantityBRandomness() {

        return (targetQuantityBRandomness);
    }

    /**
     * REQUIRES:

 targetQuantityCRandomness between [0 TO 255]
     *
     * @param targetQuantityCRandomness
     */
    protected void setTargetQuantityCRandomness(short targetQuantityCRandomness) {

        assertValueInRange(targetQuantityCRandomness);

        this.targetQuantityCRandomness = targetQuantityCRandomness;
    }

    protected short getTargetQuantityCRandomness() {

        return (targetQuantityCRandomness);
    }

    private void assertValueInRange(short val) {

        assert val >= 0 && val <= 255 : "Value [" + val + "] not in expected range [0 TO 255]";
    }

    protected void clear() {

        targetQuantityA = targetQuantityARandomness = 0;
        targetQuantityB = targetQuantityBRandomness = 0;
        targetQuantityC = targetQuantityCRandomness = 0;
    }
}
