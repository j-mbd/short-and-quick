package jmbd.gpio.shiftregister;

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
 * An array-based OrderConfigurableShiftRegister implementation.
 *
 * INVARIANTS:
 *
 * 1) Buffer position always within array bounds.
 *
 */
public class ArrayOrderConfigurableShiftRegister extends OrderConfigurableShiftRegister {

    private boolean[] buffer;

    private int bufferPos;

    @Override
    public void maxLoadBits(int maxShiftInBits) {

        super.maxLoadBits(maxShiftInBits);

        buffer = new boolean[maxLoadBits()];
    }

    @Override
    public void load(boolean bit) {

        buffer[bufferPos++] = bit;
        // From superclass post-condition..
        bitsRemaining--;
    }

    @Override
    public void unload() {

        // A reminder that the shift register is already "fullyLoaded" at this stage as seen from a client, but no bits have actually been loaded.
        // It is safe to load without checking preconditions though, as the number of bits has already been pre-condition checked.
        assert fullyLoaded() : "The shift register isn't full";

        try {
            if (isNaturalOrder()) {
                for (int i = buffer.length - 1; i >= 0; i--) {
                    super.load(buffer[i]);
                }
            } else {
                for (boolean bit : buffer) {
                    super.load(bit);
                }
            }
            super.unload();
        } finally {
            bufferPos = 0;
        }
    }
}
