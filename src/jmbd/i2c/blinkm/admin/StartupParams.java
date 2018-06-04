package jmbd.i2c.blinkm.admin;

/**
 *
 * @author savvas
 */
public class StartupParams {

    private short mode;
    private short scriptId;
    private short repeats;
    private short fadeSpeed;
    private byte scriptPlaybackSpeed;

    public short getScriptId() {

        return scriptId;
    }

    public void setScriptId(short scriptId) {

        this.scriptId = scriptId;
    }

    public short getRepeats() {

        return repeats;
    }

    // How many times shall the startup script play?
    public void setRepeats(short repeats) {

        this.repeats = repeats;
    }

    public short getFadeSpeed() {

        return fadeSpeed;
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
     * @param fadeSpeed
     */
    public void setFadeSpeed(short fadeSpeed) {

        this.fadeSpeed = fadeSpeed;
    }

    public short getScriptPlaybackSpeed() {

        return scriptPlaybackSpeed;
    }

    /**
     *
     * @param scriptPlaybackSpeed Can range between [-128 TO 127] and is treated
     * as an additive adjustment to all durations of a script that is being
     * played. A value of 0 resets the playback speed to the default.
     */
    public void setScriptPlaybackSpeed(byte scriptPlaybackSpeed) {

        this.scriptPlaybackSpeed = scriptPlaybackSpeed;
    }

    /**
     * Plays no script at start-up.
     *
     * From datasheet (also, see HsbBlinkMColour.fadeToRandom()):
     *
     * "When turning off playing a script by setting the first argument 'm' to
     * 0, the other arguments are saved but not loaded on startup and instead
     * set to zero. This is most noticeable with the fade speed value. Thus if a
     * "{'B',0,...}" is issued to disable startup script playing, be sure to
     * issue a "{'f', 20}" command after BlinkM startup or color fading will not
     * work."
     */
    public void withDoNothingMode() {

        mode = 0;
    }

    /**
     * Plays configured script at start-up.
     */
    public void withPlayScriptMode() {

        mode = 1;
    }

    public short getMode() {

        return mode;
    }
}
