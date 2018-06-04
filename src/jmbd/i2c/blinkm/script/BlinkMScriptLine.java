package jmbd.i2c.blinkm.script;

import jmbd.commons.ByteConversion;
import java.util.Arrays;

/**
 * INVARIANTS:
 *
 * command.length == EXACT_COMMAND_LENGTH
 *
 * @author savvas
 */
public class BlinkMScriptLine {

    private static final int EXACT_COMMAND_LENGTH = 4;

    private short ticks;
    private byte[] command;

    /**
     * REQUIRES:
     *
     * ticks between [1 TO 255]
     *
     * (supported range for MaxM version is [0 TO 255] so MaxM subclasses must
     * weaken this precondition)
     *
     * @param ticks
     */
    public void setTicks(short ticks) {

        assert ticks >= 1 && ticks <= 255 : "ticks value [" + ticks + "] is not within valid range of [1 TO 255]";

        this.ticks = ticks;
    }

    /**
     * REQUIRES:
     *
     * 1) command not null
     *
     * 2) command.length between [1 TO 4]
     *
     * @param command
     */
    public void setCommand(byte[] command) {

        assert command != null : "command is null";
        assert (command.length >= 1) && (command.length <= 4) : "command length is not 4";

        this.command = command;
        zeroExtendCommandIfNecessary();
    }

    public short getTicks() {

        return (ticks);
    }

    public byte[] getCommand() {
        return (command);
    }

    /**
     * According to datasheet:
     *
     * "Any command with less than 3 arguments should fill out the remaining
     * arguments slots with zeros."
     *
     */
    protected void zeroExtendCommandIfNecessary() {

        if (command.length != EXACT_COMMAND_LENGTH) {
            command = Arrays.copyOf(command, EXACT_COMMAND_LENGTH);
        }
    }

    public static BlinkMScriptLine fromRawValue(byte[] raw) {

        ByteConversion bc = new ByteConversion();
        BlinkMScriptLine scriptLine = new BlinkMScriptLine();

        bc.setByte(raw[0]);
        short tcks = bc.asShort();

        if (tcks >= 1 && tcks <= 255) {
            scriptLine.setTicks(tcks);
        } else {
            // device gave us a wrong value...oops
            throw new IllegalArgumentException("Ticks value [" + tcks + "] in device is invalid");
        }

        byte[] cmd = new byte[EXACT_COMMAND_LENGTH];
        System.arraycopy(raw, 1, cmd, 0, cmd.length);
        scriptLine.setCommand(cmd);

        return scriptLine;
    }
}
