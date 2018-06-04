package jmbd.i2c.blinkm.morsecode;

import jmbd.commons.TimeDelay;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * 1) time_unit_duration > 0
 *
 * 2) (dit_duration) == (1 X time_unit_duration)
 *
 * 3) (dah_duration) == (3 X time_unit_duration)
 *
 * 4) (gap_duration_within_character) == (1 X time_unit_duration)
 *
 * 5) (gap_duration_between_letters) == (3 X time_unit_duration)
 *
 * 6) (gap_duration_between_words) == (7 X time_unit_duration)
 *
 *
 * @author savvas
 */
public abstract class MorseCodeTransmission {

    private static final Map<Character, List<SignalType>> CHAR_SEQUENCE_MAPPING = makeSequenceMapping();

    private long timeUnitDuration;
    private long ditDuration;
    private long dahDuration;
    private long interCharacterGapDuration;
    private long letterGapDuration;
    private long wordGapDuration;

    private TimeDelay timeDelay;

    /**
     * REQUIRES:
     *
     * _timeUnitDuration > 0
     *
     * ENSURES:
     *
     * 1) (timeUnitDuration) > 0
     *
     * 2) (ditDuration) == (1 X timeUnitDuration)
     *
     * 3) (dahDuration) == (3 X timeUnitDuration)
     *
     * 4) (interCharacterGapDuration) == (1 X timeUnitDuration)
     *
     * 5) (letterGapDuration) == (3 X timeUnitDuration)
     *
     * 6) (wordGapDuration) == (7 X timeUnitDuration)
     *
     * @param _timeUnitDuration
     */
    public void setTimeUnitDuration(long _timeUnitDuration) {

        assert _timeUnitDuration > 0 : "time-unit duration is negative";

        timeUnitDuration = _timeUnitDuration;

        ditDuration = 1 * timeUnitDuration;
        dahDuration = 3 * timeUnitDuration;
        interCharacterGapDuration = 1 * timeUnitDuration;
        letterGapDuration = 3 * timeUnitDuration;
        wordGapDuration = 7 * timeUnitDuration;

        assert ditDuration == 1 * timeUnitDuration : "ditDuration != (1 * timeUnitDuration)";
        assert dahDuration == 3 * timeUnitDuration : "dahDuration != (3 * timeUnitDuration)";
        assert interCharacterGapDuration == 1 * timeUnitDuration : "interCharacterGapDuration != (1 * timeUnitDuration)";
        assert letterGapDuration == 3 * timeUnitDuration : "letterGapDuration != (3 * timeUnitDuration)";
        assert wordGapDuration == 7 * timeUnitDuration : "wordGapDuration != (7 * timeUnitDuration)";
    }

    /**
     * REQUIRES:
     *
     * timeDelay not null
     *
     * (For a more generic solution TimeDelay can be extended to introduce a
     * "morse-code" specialised variant with a "timeUnit" variable and a
     * apply(duration) function which can be used polymorphically here..)
     *
     * @param timeDelay
     */
    public void setTimeDelay(TimeDelay timeDelay) {

        assert timeDelay != null : "timeDelay instance is null";

        this.timeDelay = timeDelay;
    }

    /**
     * REQUIRES:
     *
     * word not null
     *
     * @param word
     */
    public void send(String word) {

        char[] cs = word.toCharArray();

        for (char c : cs) {
            send(c);
        }

        timeDelay.pauseMillis(wordGapDuration);
    }

    public void send(char c) {

        // first upper-case
        c = Character.toUpperCase(c);

        if (CHAR_SEQUENCE_MAPPING.containsKey(c)) {

            List<SignalType> sequence = CHAR_SEQUENCE_MAPPING.get(c);

            for (SignalType st : sequence) {

                signalOn();

                switch (st) {
                    case DIT:
                        timeDelay.pauseMillis(ditDuration);
                        break;
                    case DAH:
                        timeDelay.pauseMillis(dahDuration);
                        break;
                    default:
                        throw new IllegalArgumentException("Signal-Type " + st + ", not supported");
                }

                signalOff();

                timeDelay.pauseMillis(interCharacterGapDuration);
            }
            timeDelay.pauseMillis(letterGapDuration);
        } else {
            System.out.println("Character '" + c + "' not supported");
            // or throw exception...
            //throw new IllegalArgumentException("Character '" + c + "' not supported");
        }
    }

    protected abstract void signalOn();

    protected abstract void signalOff();

    private static enum SignalType {

        DIT, DAH;
    }

    /**
     * Can be externalised somehow to facilitate extensions..or introduce method
     * to add/remove mappings..? or expose sequence mapping map...? (yikesss)
     *
     * @return
     */
    private static Map<Character, List<SignalType>> makeSequenceMapping() {

        Map<Character, List<SignalType>> m = new HashMap<>();

        // A
        m.put('A', Arrays.asList(SignalType.DIT, SignalType.DAH));
        // B
        m.put('B', Arrays.asList(SignalType.DAH, SignalType.DIT, SignalType.DIT, SignalType.DIT));
        // C
        m.put('C', Arrays.asList(SignalType.DAH, SignalType.DIT, SignalType.DAH, SignalType.DIT));
        // D
        m.put('D', Arrays.asList(SignalType.DAH, SignalType.DIT, SignalType.DIT));
        // E
        m.put('E', Arrays.asList(SignalType.DIT));
        // F
        m.put('F', Arrays.asList(SignalType.DIT, SignalType.DIT, SignalType.DAH, SignalType.DIT));
        // G
        m.put('G', Arrays.asList(SignalType.DAH, SignalType.DAH, SignalType.DIT));
        // H
        m.put('H', Arrays.asList(SignalType.DIT, SignalType.DIT, SignalType.DIT, SignalType.DIT));
        // I
        m.put('I', Arrays.asList(SignalType.DIT, SignalType.DIT));
        // J
        m.put('J', Arrays.asList(SignalType.DIT, SignalType.DAH, SignalType.DAH, SignalType.DAH));
        // K
        m.put('K', Arrays.asList(SignalType.DAH, SignalType.DIT, SignalType.DAH));
        // L
        m.put('L', Arrays.asList(SignalType.DIT, SignalType.DAH, SignalType.DIT, SignalType.DIT));
        // M
        m.put('M', Arrays.asList(SignalType.DAH, SignalType.DAH));
        // N
        m.put('N', Arrays.asList(SignalType.DAH, SignalType.DIT));
        // O
        m.put('O', Arrays.asList(SignalType.DAH, SignalType.DAH, SignalType.DAH));
        // P
        m.put('P', Arrays.asList(SignalType.DIT, SignalType.DAH, SignalType.DAH, SignalType.DIT));
        // Q
        m.put('Q', Arrays.asList(SignalType.DAH, SignalType.DAH, SignalType.DIT, SignalType.DAH));
        // R
        m.put('R', Arrays.asList(SignalType.DIT, SignalType.DAH, SignalType.DIT));
        // S
        m.put('S', Arrays.asList(SignalType.DIT, SignalType.DIT, SignalType.DIT));
        // T
        m.put('T', Arrays.asList(SignalType.DAH));
        // U
        m.put('U', Arrays.asList(SignalType.DIT, SignalType.DIT, SignalType.DAH));
        // V
        m.put('V', Arrays.asList(SignalType.DIT, SignalType.DIT, SignalType.DIT, SignalType.DAH));
        // W
        m.put('W', Arrays.asList(SignalType.DIT, SignalType.DAH, SignalType.DAH));
        // X
        m.put('X', Arrays.asList(SignalType.DAH, SignalType.DIT, SignalType.DIT, SignalType.DAH));
        // Y
        m.put('Y', Arrays.asList(SignalType.DAH, SignalType.DIT, SignalType.DAH, SignalType.DAH));
        // Z
        m.put('Z', Arrays.asList(SignalType.DAH, SignalType.DAH, SignalType.DIT, SignalType.DIT));

        return m;
    }
}
