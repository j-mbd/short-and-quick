package jmbd.i2c.blinkm.script;

import jmbd.i2c.blinkm.command.BlinkMCommandExecution;
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
 * INVARIANTS:
 *
 * lineNumber between [0 TO 49]
 *
 * @author savvas
 */
public class BlinkMCustomScript {

    protected static final short CUSTOM_SCRIPT_ID = 0;

    private static final short MAX_LINE_NUMBER = 49;

    private static final char WRITE_SCRIPT_LINE_MNIC = 'W';
    private static final short WRITE_COMMAND_MAX_LENGTH = 8;

    private static final char READ_SCRIPT_LINE_MNIC = 'R';
    private static final char PLAY_SCRIPT_MNIC = 'p';
    private static final short READ_LINE_RETURN_SIZE = 5;

    private static final char SET_SCRIPT_LENGTH_MNIC = 'L';

    private static final long DEVICE_WRITE_DELAY_MILLIS = 30;

    private short id = CUSTOM_SCRIPT_ID;
    private short lineNumber;
    private int repeats;
    private short length;

    protected BlinkMCommandExecution commandExecution;
    private final TimeDelay timeDelay;

    public BlinkMCustomScript(BlinkMCommandExecution blinkMCommand) {

        this.commandExecution = blinkMCommand;
        this.timeDelay = new TimeDelay();
    }

    /**
     * REQUIRES:
     *
     * id == 0
     *
     * @param id
     */
    public void setId(short id) {

        assert id == 0 : "Script id: [" + id + "] not supported. Only [" + CUSTOM_SCRIPT_ID + "] value is supported";

        this.id = id;
    }

    /**
     * REQUIRES:
     *
     * lineNumber between [0 TO 49]
     *
     * Subclasses can weaken this precondition to be:
     *
     * [REQUIRES: lineNumber between [0 TO *] ] (where * >= 49)
     *
     * @param lineNumber
     */
    public void setLineNumber(short lineNumber) {

        assert lineNumber == 0 : "Script line: [" + lineNumber + "] not supported. Only values [0 TO 49] are supported";

        this.lineNumber = lineNumber;
    }

    /**
     * REQUIRES:
     *
     * max lines not reached (i.e. maxLinesReached() == false)
     *
     */
    public void increaseLineNumber() {

        assert maxLinesReached() : "Maximum script line number [" + MAX_LINE_NUMBER + "] has been reached";

        lineNumber++;
    }

    /**
     * REQUIRES:
     *
     * min lines not reached (i.e. minLinesReached() == false)
     *
     */
    public void decreaseLineNumber() {

        assert minLinesReached() : "Minimum script line number [0] has been reached";

        lineNumber--;
    }

    // Can we keep adding more script lines for this id?
    public boolean maxLinesReached() {

        return (lineNumber == MAX_LINE_NUMBER);
    }

    // Have we hit the minimum line number?
    public boolean minLinesReached() {

        return (lineNumber == 0);
    }

    /**
     * REQUIRES:
     *
     * repeats >= 0
     *
     * @param repeats
     */
    public void setRepeats(int repeats) {

        assert repeats >= 0 : "Repeats is negative";

        this.repeats = repeats;
    }

    /**
     * REQUIRES:
     *
     * length between [1 TO 255]
     *
     * @param length
     */
    public void setLength(short length) {

        assert (length >= 1) && (length <= 255) : "Script length [" + length + "] is not within valid range [1 TO 255]";

        this.length = length;
    }

    public short getId() {

        return (id);
    }

    public short getLineNumber() {

        return (lineNumber);
    }

    public int getRepeats() {

        return (repeats);
    }

    public short getLength() {
        return (length);
    }

    public void write(BlinkMScriptLine scriptLine) {

        byte[] cmd = new byte[WRITE_COMMAND_MAX_LENGTH];

        short index = 0;
        // first set all non-command args
        cmd[index++] = WRITE_SCRIPT_LINE_MNIC;
        cmd[index++] = (byte) getId();
        cmd[index++] = (byte) getLineNumber();
        cmd[index++] = (byte) scriptLine.getTicks();
        // now set the command
        for (byte b : scriptLine.getCommand()) {
            cmd[index++] = b;
        }

        commandExecution.runWithNoReturnValue(cmd);

        timeDelay.pauseMillis(DEVICE_WRITE_DELAY_MILLIS);
    }

    public BlinkMScriptLine read() {

        byte[] cmd = {READ_SCRIPT_LINE_MNIC, (byte) getId(), (byte) getLineNumber()};
        byte[] retVal = new byte[READ_LINE_RETURN_SIZE];

        commandExecution.runAndPopulateReturnValue(cmd, retVal);

        return BlinkMScriptLine.fromRawValue(retVal);
    }

    public void play() {

        byte[] cmd = {PLAY_SCRIPT_MNIC, (byte) getId(), (byte) getRepeats(), (byte) getLineNumber()};

        commandExecution.runWithNoReturnValue(cmd);
    }

    /**
     * Configure device with currently set script-lengths and repeats.
     */
    public void applyScriptLengthAndRepeats() {

        byte[] cmd = {SET_SCRIPT_LENGTH_MNIC, (byte) getId(), (byte) getLength(), (byte) getRepeats()};

        commandExecution.runWithNoReturnValue(cmd);

        timeDelay.pauseMillis(DEVICE_WRITE_DELAY_MILLIS);
    }
}
