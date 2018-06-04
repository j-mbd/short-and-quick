package jmbd.i2c.blinkm.command;

import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.i2cbus.I2CDevice;

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
public class BlinkMCommandExecution {

    private static final short DEFAULT_MAX_COMMAND_LENGTH = 4;
    private static final short DEFAULT_EXACT_RETURN_VAL_LENGTH = 3;

    private final I2CDevice blinkMHandle;

    private ByteBuffer payloadBuffer;
    private ByteBuffer returnValueBuffer;

    /**
     * REQUIRES:
     *
     * blinkMHandle is open
     *
     * @param blinkMHandle
     */
    public BlinkMCommandExecution(I2CDevice blinkMHandle) {

        assert blinkMHandle.isOpen() : "I2C device is not open";

        this.blinkMHandle = blinkMHandle;

        payloadBuffer = ByteBuffer.allocateDirect(DEFAULT_MAX_COMMAND_LENGTH);
        returnValueBuffer = ByteBuffer.allocateDirect(DEFAULT_EXACT_RETURN_VAL_LENGTH);
    }

    /**
     * REQUIRES:
     *
     * command not null
     *
     * @param command
     */
    public void runWithNoReturnValue(byte[] command) {

        assert command != null : "Given command is null";

        try {
            ensurePayloadBufferCapacity(command.length);

            payloadBuffer.put(command).flip();
            blinkMHandle.write(payloadBuffer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            payloadBuffer.clear();
        }
    }

    /**
     * REQUIRES:
     *
     * 1) command not null
     *
     * 2) returnValue not null
     *
     * @param command
     * @param returnValue
     */
    public void runAndPopulateReturnValue(byte[] command, byte[] returnValue) {

        assert command != null : "Given command is null";
        assert returnValue != null : "Given return value array is null";

        try {
            // first run command
            runWithNoReturnValue(command);

            ensureReturnBufferCapacity(returnValue.length);

            // then read the response
            blinkMHandle.read(returnValueBuffer);
            exportReturnBufferIntoArray(returnValue);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            returnValueBuffer.clear();
        }
    }

    private void exportReturnBufferIntoArray(byte[] returnValue) {

        returnValueBuffer.flip();
        returnValueBuffer.get(returnValue);
    }

    /**
     * REQUIRES:
     *
     * desired capacity > 0
     *
     * @param c
     */
    private void ensurePayloadBufferCapacity(int c) {

        assert c > 0 : "Target buffer capacity is zero or negative";

        if (payloadBuffer.capacity() < c) {
            System.out.println("Current payload buffer capacity [" + payloadBuffer.capacity() + "] not enough to accommodate desired capacity of [" + c + "]. Allocating new buffer..");
            // Let's bite the bullet and "allocateDirect" again...shouldn't happen often though as most commands are <= DEFAULT_MAX_COMMAND_LENGTH
            payloadBuffer = ByteBuffer.allocateDirect(c);
        }
    }

    /**
     * REQUIRES:
     *
     * desired capacity > 0
     *
     * @param c
     */
    private void ensureReturnBufferCapacity(int c) {

        assert c > 0 : "Target buffer capacity is zero or negative";

        if (returnValueBuffer.capacity() < c) {
            System.out.println("Current return value buffer capacity [" + returnValueBuffer.capacity() + "] not enough to accommodate desired capacity of [" + c + "]. Allocating new buffer..");
            // Let's bite the bullet and "allocateDirect" again...shouldn't happen often though as return value array.length usually == DEFAULT_EXACT_RETURN_VAL_LENGTH
            returnValueBuffer = ByteBuffer.allocateDirect(c);
        }
    }
}
