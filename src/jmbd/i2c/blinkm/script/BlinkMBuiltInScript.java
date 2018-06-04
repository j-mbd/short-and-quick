package jmbd.i2c.blinkm.script;

import jmbd.i2c.blinkm.command.BlinkMCommandExecution;

/**
 *
 * @author savvas
 */
public class BlinkMBuiltInScript extends BlinkMCustomScript {

    private static final int MAX_SCRIPT_ID = 18;

    public BlinkMBuiltInScript(BlinkMCommandExecution commandExecution) {

        super(commandExecution);
    }

    /**
     * REQUIRES:
     *
     * (id >= 0) && (id <= 18)
     *
     * (weakening superclass precondition as ids > 0 are valid for built-in
     * scripts)
     *
     * @param id
     */
    @Override
    public void setId(short id) {

        assert (id >= 0) && (id <= MAX_SCRIPT_ID) : "id [" + id + "] is not within expected range [0 TO " + MAX_SCRIPT_ID + "]";

        super.setId(id);
    }

    @Override
    public void write(BlinkMScriptLine scriptLine) {

        // If id is for custom scripts, do the write
        if (getId() == CUSTOM_SCRIPT_ID) {
            super.write(scriptLine);
        }
        // Else, a no-op (invalid for built-in scripts).
    }
}
