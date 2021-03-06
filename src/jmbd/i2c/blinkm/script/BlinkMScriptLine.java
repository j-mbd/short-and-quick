package jmbd.i2c.blinkm.script;

import jmbd.commons.ByteConversion;
import java.util.Arrays;

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
 * command.length == maxCommandLength
 *
 * @author savvas
 */
public class BlinkMScriptLine {

    private static final short DEFAULT_MAX_COMMAND_LENGTH = 4;

    private short ticks;
    private byte[] command;
    private short maxCommandLength = DEFAULT_MAX_COMMAND_LENGTH;

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

    public short getTicks() {

        return (ticks);
    }

    /**
     * REQUIRES:
     *
     * 1) command not null
     *
     * 2) command.length between [1 TO maxCommandLength]
     *
     * @param command
     */
    public void setCommand(byte[] command) {

        assert command != null : "command is null";
        assert (command.length >= 1) && (command.length <= 4) : "command length is not 4";

        this.command = command;
        zeroExtendCommandIfNecessary();
    }

    public byte[] getCommand() {

        return (command);
    }

    /**
     * REQUIRES:
     *
     * maxCommandLength > 0
     *
     * @param maxCommandLength
     */
    public void setMaxCommandLength(short maxCommandLength) {

        assert maxCommandLength > 0 : "exactCommandLength is negative";

        this.maxCommandLength = maxCommandLength;
    }

    public short getMaxCommandLength() {

        return (maxCommandLength);
    }

    /**
     * According to datasheet:
     *
     * "Any command with less than "maxCommandLength" arguments (four in this
     * implementation) should fill out the remaining arguments slots with
     * zeros."
     *
     */
    protected void zeroExtendCommandIfNecessary() {

        if (command.length != getMaxCommandLength()) {
            command = Arrays.copyOf(command, getMaxCommandLength());
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
            // device retuened a wrong value...oops
            throw new IllegalArgumentException("Ticks value [" + tcks + "] in device is invalid");
        }

        byte[] cmd = new byte[scriptLine.getMaxCommandLength()];

        System.arraycopy(raw, 1, cmd, 0, cmd.length);

        scriptLine.setCommand(cmd);

        return scriptLine;
    }
}
