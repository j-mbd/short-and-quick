package jmbd.i2c.mpu6050.interrupt;
//import java.net.DatagramSocket;

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
 * To be ran on some SE Runtime....
 *
 * @author savvas
 */
public class UdpServer {

    private static final int PORT = 8080;
    private static final int MAX_PACKET_SIZE = 65507;
    private static final String STOP_MESSAGE = "stop";

    private boolean running;

    public void startListening(int port, int pckSize) {

        running = true;

//        try (DatagramSocket soc = new DatagramSocket(port)) {
//
//            byte[] buffer = new byte[pckSize];
//            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//
//            System.out.println("Starting server");
//
//            while (running) {
//
//                soc.receive(packet);
//
//                String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
//                message = message.replaceAll(System.lineSeparator(), "");
//                System.out.println(packet.getAddress() + " at port " + packet.getPort() + " says " + "\"" + message + "\"");
//                // prepare for next packet..
//                packet.setLength(pckSize);
//
//                if (message.equalsIgnoreCase(STOP_MESSAGE)) {
//                    running = false;
//                }
//            }
//            System.out.println("Stopping server");
//        } catch (IOException ioex) {
//            throw new RuntimeException(ioex);
//        }
    }

    public static void main(String[] args) {

        UdpServer server = new UdpServer();
        server.startListening(PORT, MAX_PACKET_SIZE);
    }
}
