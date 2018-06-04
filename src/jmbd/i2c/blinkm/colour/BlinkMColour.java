package jmbd.i2c.blinkm.colour;

import jmbd.i2c.blinkm.command.BlinkMCommandExecution;

/**
 * INVARIANTS:
 *
 * 1) Quantity values for A,B,C between [0 TO 255]
 *
 * 2) Quantity value randomness for A,B,C between [0 TO 255]
 *
 * (Used in an "implementation-inheritance" mode. Abstraction-anchoring must be
 * done in subclasses)
 *
 * @author savvas
 */
abstract class BlinkMColour {

    private short quantityA;
    private short quantityB;
    private short quantityC;

    private short quantityARandomness;
    private short quantityBRandomness;
    private short quantityCRandomness;

    protected BlinkMCommandExecution commandExecution;

    protected BlinkMColour(BlinkMCommandExecution commandExecution) {

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
