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
 * A shift-register implementation which allows clients to specify the order the
 * shifted-in bits should be latched-out.
 *
 * For example, assuming [A7 A6 A5 A4 A3 A2 A1 A0] is the data to be shifted-in
 * LSB first and [Q7 Q6 Q5 Q4 Q3 Q2 Q1 Q0] the latch-out pins then (by default)
 * the latch-out pins will get the following values:
 *
 * Q7 -> A0 | Q6 -> A1 | Q5 -> A2 | Q4 -> A3 | Q3 -> A4 | Q2 -> A5 | Q1 -> A6 |
 * Q0 -> A7
 *
 * This is the behaviour of the ShiftRegister superclass which may or may not be
 * desirable. One solution if it is not, is to send the bits in the reverse
 * order or alternatively configure (if possible) the pins of the attached
 * device differently. This could result in higher coupling between clients and
 * the shift register resulting in more expensive changes when requirements
 * change.
 *
 * A more flexible approach would be to expose a means of specifying the order
 * through the shift register. This flexibility comes at a (memory) cost of some
 * additional buffering of the shifted-in bits.
 *
 */
public abstract class OrderConfigurableShiftRegister extends ShiftRegister {

    private boolean naturalOrder;

    /**
     * Configure with "natural" order.
     *
     * E.g.:
     *
     * Given input is
     *
     * A7 A6 A5 A4 A3 A2 A1 A0
     *
     * And latch-out pins are:
     *
     * Q7 Q6 Q5 Q4 Q3 Q2 Q1 Q0
     *
     * Then A0 will be set to Q0, A1 to Q1 and so on.
     *
     */
    public void makeNaturalOrder() {
        naturalOrder = true;
    }

    /**
     * Configure with "inverse" order.
     *
     * E.g.:
     *
     * Given input is
     *
     * A7 A6 A5 A4 A3 A2 A1 A0
     *
     * And latch-out pins are:
     *
     * Q7 Q6 Q5 Q4 Q3 Q2 Q1 Q0
     *
     * Then A0 will set Q7, A2 will set Q6 and so on.
     *
     * (This is the default mode if none specified)
     *
     */
    public void makeInverseOrder() {
        naturalOrder = false;
    }

    public boolean isNaturalOrder() {
        return naturalOrder;
    }

    public boolean isInverseOrder() {
        return !naturalOrder;
    }
}
