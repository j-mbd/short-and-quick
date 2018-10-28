package jmbd.commons;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.BufferAccess;

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
 * Attempts to generate optimised ByteBuffers based on device I/O buffers.
 *
 * @author savvas
 */
public class ByteBufferAccessOptimisation {

    private BufferAccess<ByteBuffer> device;

    /**
     * REQUIRES:
     *
     * device != null
     *
     * ENSURES:
     *
     * this.device == device
     *
     * @param device
     */
    public ByteBufferAccessOptimisation(BufferAccess<ByteBuffer> device) {

        setDevice(device);
    }

    /**
     * REQUIRES:
     *
     * device != null
     *
     * ENSURES:
     *
     * this.device == device
     *
     * @param device
     */
    public void setDevice(BufferAccess<ByteBuffer> device) {

        assert device != null : "Given device is null";

        this.device = device;

        assert this.device == device : "Device was not set";
    }

    /**
     * Tries to create a new optimised buffer based on the configured
     * (ByteBuffer-based) device.
     *
     * Returns passed-in (original) buffer if unsuccessful.
     *
     * Implicit assumption:
     *
     * (size_of_the_intended_data_to_transfer) == original.capacity()
     *
     * REQUIRES:
     *
     * original != null
     *
     * @param original
     * @return
     */
    public ByteBuffer optimised(ByteBuffer original) {

        try {
            assert original != null : "original buffer is null";

            ByteBuffer optimised = device.prepareBuffer(original, original.capacity());

            if (optimised == null) {
                Logger.getLogger(ByteBufferAccessOptimisation.class.getName()).log(Level.WARNING, "Could not create optimised ByteBuffer, reverting back to original");
            }

            return optimised != null ? optimised : original;
        } catch (IOException ex) {

            Logger.getLogger(ByteBufferAccessOptimisation.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
