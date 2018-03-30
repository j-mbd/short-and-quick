package jmbd.gpio.shiftregister;

import java.util.ArrayDeque;
import java.util.Deque;

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
 * A deque-based OrderConfigurableShiftRegister implementation.
 *
 */
public class DequeOrderConfigurableShiftRegister extends OrderConfigurableShiftRegister {

    private Deque<Boolean> buffer;

    @Override
    public void maxLoadBits(int maxShiftInBits) {

        super.maxLoadBits(maxShiftInBits);
        buffer = new ArrayDeque(maxLoadBits());
    }

    @Override
    public void load(boolean bit) {

        buffer.offerLast(bit);
        // From superclass post-condition..
        bitsRemaining--;
    }

    @Override
    public void unload() {

        // A reminder that the shift register is already "fullyLoaded" at this stage as seen from a client, but no bits have actually been loaded.
        // It is safe to load without checking preconditions though, as the number of bits has already been pre-condition checked.
        assert fullyLoaded() : "The shift register isn't full";

        if (isNaturalOrder()) {
            while (!buffer.isEmpty()) {
                super.load(buffer.removeLast());
            }
        } else {
            while (!buffer.isEmpty()) {
                super.load(buffer.removeFirst());
            }
        }
        super.unload();
    }
}
