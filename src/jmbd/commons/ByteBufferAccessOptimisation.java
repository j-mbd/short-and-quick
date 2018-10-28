package jmbd.commons;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.BufferAccess;

/**
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
