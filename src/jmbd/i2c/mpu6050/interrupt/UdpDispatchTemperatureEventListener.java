package jmbd.i2c.mpu6050.interrupt;

import jmbd.i2c.mpu6050.register.configuration.RegisterValue;
import jmbd.i2c.mpu6050.device.AccelGyroTempSensor;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

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
 * Sends event to some server somewhere in the network (protocol is UDP).
 *
 * @author savvas
 */
public class UdpDispatchTemperatureEventListener extends TemperatureEventListener {

    protected DatagramConnection connection;

    public UdpDispatchTemperatureEventListener(RegisterValue registerValue, AccelGyroTempSensor sensor) throws IOException {

        super(registerValue, sensor);
        connection = (DatagramConnection) Connector.open("datagram://savvas:8080");
    }

    @Override
    protected void sendOutTemperatureEvent(float temp) {

        try {
            String message = "Latest temperature is: " + temp;
            byte[] payload = message.getBytes();

            Datagram dg = connection.newDatagram(payload, payload.length);
            System.out.println("Sending...");
            connection.send(dg);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() throws Exception {

        connection.close();
    }
}
