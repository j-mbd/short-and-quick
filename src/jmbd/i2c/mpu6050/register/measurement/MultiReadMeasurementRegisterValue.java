package jmbd.i2c.mpu6050.register.measurement;

import jmbd.i2c.mpu6050.register.configuration.RegisterValue;

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
 * Reads values of both registers in multiple sequential reads.
 *
 * WARNING: Consistency, with this implementation, can *only* be achieved if the
 * sensor blocks (and therefore does not update the registers) waiting for the
 * application to clear the interrupt flag (this behaviour can be configured via
 * the LATCH_INT_EN/INT_RD_CLEAR bits in INT_PIN_CFG register (0x37)).
 *
 * Also see, @@CombinedMessageMeasurementRegisterValue@@
 *
 * @author savvas
 */
public class MultiReadMeasurementRegisterValue extends MeasurementRegisterValue {

    protected RegisterValue registerValue;

    public MultiReadMeasurementRegisterValue(RegisterValue registerValue) {

        this.registerValue = registerValue;
    }

    @Override
    public void load() {

        registerValue.setRegisterAddress(getHighRegisterAddr());
        registerValue.load();

        highRegValue = registerValue.getValue();

        registerValue.setRegisterAddress(getLowRegisterAddr());
        registerValue.load();

        lowRegValue = registerValue.getValue();
    }
}
