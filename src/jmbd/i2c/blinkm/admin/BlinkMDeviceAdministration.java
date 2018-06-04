package jmbd.i2c.blinkm.admin;

import jmbd.i2c.blinkm.command.BlinkMCommandExecution;
import jmbd.commons.ByteConversion;
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
 * @author savvas
 */
public class BlinkMDeviceAdministration {

    private static final int STARTUP_PARAMS_WRITE_DELAY_MILLIS = 25;
    private static final int DEVICE_ADDRESS_RETURN_ARRAY_LENGTH = 1;
    private static final int DEVICE_FIRM_VERSION_RETURN_ARRAY_LENGTH = 2;

    private static final byte[] STOP_SCRIPT_COMMAND = {'o'};
    private static final byte[] GET_ADDRESS_COMMAND = {'a'};
    private static final byte[] GET_FIRMWARE_VERSION_COMMAND = {'Z'};

    private static final char PLAYBACK_SPEED_MNIC = 't';
    private static final char FADE_SPEED_MNIC = 'f';
    private static final char STARTUP_PARAMS_MNIC = 'B';

    private final BlinkMCommandExecution commandExecution;

    public BlinkMDeviceAdministration(BlinkMCommandExecution commandExecution) {

        this.commandExecution = commandExecution;
    }

    /**
     * Has no effect if no script is currently playing.
     */
    public void stopCurrentlyPlayingScript() {

        commandExecution.runWithNoReturnValue(STOP_SCRIPT_COMMAND);
    }

    /**
     * Sets the rate at which colour fading happens.
     *
     * REQUIRES:
     *
     * speed BETWEEN [1 TO 255]
     *
     * Slowest fading when speed is 1. Colours change instantly when speed is
     * 255.
     *
     * A value of 0 is invalid and is reserved for a future "Smart Fade"
     * feature. SmartFade subclasses must weaken the precondition to be:
     *
     * speed BETWEEN [0 TO 255]
     *
     * @param speed
     */
    public void setFadeSpeed(short speed) {

        byte[] cmd = {FADE_SPEED_MNIC, (byte) speed};

        commandExecution.runWithNoReturnValue(cmd);
    }

    /**
     *
     * @param speed Can range between [-128 TO 127] and is treated as an
     * additive adjustment to all durations of a script that is being played. A
     * value of 0 resets the playback speed to the default.
     */
    public void setScriptPlaybackSpeed(byte speed) {

        byte[] cmd = {PLAYBACK_SPEED_MNIC, speed};

        commandExecution.runWithNoReturnValue(cmd);
    }

    /**
     * Currently not supported.
     *
     * @param newAddress
     */
    public void setDeviceAddress(short newAddress) {

        throw new UnsupportedOperationException("Address change of a BlinkM device not currently supported");
    }

    public short getDeviceAddress() {

        byte[] retVal = new byte[DEVICE_ADDRESS_RETURN_ARRAY_LENGTH];

        commandExecution.runAndPopulateReturnValue(GET_ADDRESS_COMMAND, retVal);

        ByteConversion byteConversion = new ByteConversion(retVal[0]);

        return byteConversion.asShort();
    }

    /**
     *
     * @return null if device didn't return both minor and major versions
     */
    public FirmwareVersion getDeviceFirmwareVersion() {

        byte[] retVal = new byte[DEVICE_FIRM_VERSION_RETURN_ARRAY_LENGTH];

        commandExecution.runAndPopulateReturnValue(GET_FIRMWARE_VERSION_COMMAND, retVal);

        if (retVal.length == 2) {
            return FirmwareVersion.fromRawValues(retVal);
        }

        System.out.println("Device didn't not return both minor and major versions..?");

        return null;
    }

    public void setStartupParams(StartupParams params) {

        byte[] cmd = {STARTUP_PARAMS_MNIC, (byte) params.getMode(), (byte) params.getScriptId(), (byte) params.getRepeats(), (byte) params.getFadeSpeed(), (byte) params.getScriptPlaybackSpeed()};

        commandExecution.runWithNoReturnValue(cmd);

        TimeDelay td = new TimeDelay();
        td.pauseMillis(STARTUP_PARAMS_WRITE_DELAY_MILLIS);
    }
}
