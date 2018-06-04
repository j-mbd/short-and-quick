package jmbd.i2c.blinkm;

import jmbd.i2c.blinkm.admin.BlinkMDeviceAdministration;
import jmbd.i2c.blinkm.admin.FirmwareVersion;
import jmbd.i2c.blinkm.admin.StartupParams;
import jmbd.i2c.blinkm.colour.HsbBlinkMColour;
import jmbd.i2c.blinkm.colour.RgbBlinkMColour;
import jmbd.i2c.blinkm.command.BlinkMCommandExecution;
import jmbd.i2c.blinkm.morsecode.BlinkMMorseCodeTransmission;
import jmbd.i2c.blinkm.script.BlinkMBuiltInScript;
import jmbd.i2c.blinkm.script.BlinkMCustomScript;
import jmbd.i2c.blinkm.script.BlinkMScriptLine;
import jmbd.commons.CommonOperationsMIDlet;
import jmbd.commons.TimeDelay;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

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
 * Some sandbox space to experiment with features of BlinkM classes.
 *
 * NOTE: acceptMorseCodeRequest() requires Push-Registry to have been set-up in
 * jad and the app to have been "ams-install" via the VM proxy. The
 * Push-Registry doesn't seem to work if the app is installed via NetBeans (i.e.
 * telneting into the port works but the startApp() method isn't invoked when
 * data is sent through). It only seems to work if "ams-install"-ed manually..
 *
 * The following are the Push Registry related entries required in jad:
 *
 * MIDlet-Permission-3: javax.microedition.io.PushRegistryPermission "socket:"
 * "static"
 *
 * MIDlet-Permission-4: javax.microedition.io.SocketProtocolPermission
 * "socket://:5000"
 *
 * MIDlet-Push-1: socket://:5000,com.cos.jmbd.i2c.blinkm.BlinkMDriver,*
 *
 * @author savvas
 */
public class BlinkMDriver extends CommonOperationsMIDlet {

    private static final int DEVICE_ADDRESS = 0x09;
    private static final int SENSOR_ADDRESS_SIZE = 7;
    private static final int IC2_BUS_NUMBER = 1;

    private I2CDevice blinkMHandle;

    private RgbBlinkMColour rgbColour;
    private HsbBlinkMColour hsbColour;
    private BlinkMBuiltInScript buildInScript;
    private BlinkMCustomScript customScript;
    private BlinkMDeviceAdministration blinkMDeviceAdministration;
    private BlinkMMorseCodeTransmission codeTransmission;

    private TimeDelay timeDelay;

    @Override
    public void startApp() {
        try {

            blinkMHandle = blinkMHandle();
            BlinkMCommandExecution commandExecution = new BlinkMCommandExecution(blinkMHandle);

            rgbColour = new RgbBlinkMColour(commandExecution);
            hsbColour = new HsbBlinkMColour(commandExecution);
            buildInScript = new BlinkMBuiltInScript(commandExecution);
            customScript = new BlinkMCustomScript(commandExecution);
            blinkMDeviceAdministration = new BlinkMDeviceAdministration(commandExecution);

            timeDelay = new TimeDelay();

            codeTransmission = new BlinkMMorseCodeTransmission(rgbColour);
            codeTransmission.setTimeUnitDuration(300);
            codeTransmission.setTimeDelay(timeDelay);

            blinkMDeviceAdministration.stopCurrentlyPlayingScript();

            //blinkMRgbLights();
            //blinkMHsbLights();
            //playScript(Short.valueOf("16"));
            //writeScriptAndPlay(commandExecution);
            //readScript(Short.valueOf("0"));
            //doAdminTasks();
            //testMorseCode();
            acceptMorseCodeRequest();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void acceptMorseCodeRequest() {

        String[] pendingConnections = PushRegistry.listConnections(true);

        if (pendingConnections != null && pendingConnections.length > 0) {

            for (String s : pendingConnections) {

                rgbColour.goDark();

                System.out.println("Attemping to open connection: " + s);

                try (ServerSocketConnection ssc = (ServerSocketConnection) Connector.open(s);
                        StreamConnection sc = ssc.acceptAndOpen();
                        InputStream is = sc.openInputStream()) {
                    for (int i = is.read(); i != -1; i = is.read()) {

                        char c = (char) i;
                        if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
                            codeTransmission.send(c);
                        }
                    }
                } catch (IOException ioex) {
                    throw new RuntimeException(ioex);
                }
            }
        } else {
            System.out.println("No pending connections.");
        }

        destroyApp(true);
    }

    private void testMorseCode() {

        codeTransmission.setTimeUnitDuration(300);
        codeTransmission.setTimeDelay(new TimeDelay());

        codeTransmission.send('A');
        timeDelay.pauseMillis(5_000);
        codeTransmission.send('J');
        timeDelay.pauseMillis(5_000);
        codeTransmission.send('S');
    }

    private void readScript(short id) {

        // black-out
        rgbColour.makeBlack();

        buildInScript.setId(id);
        buildInScript.setLineNumber(Short.valueOf("2"));

        BlinkMScriptLine scriptLine = buildInScript.read();

        BlinkMCommandExecution commandExecution = new BlinkMCommandExecution(blinkMHandle);

        for (int i = 0; i < 10; i++) {
            rgbColour.fadeApply();
            timeDelay.pauseMillis(5_000);
            commandExecution.runWithNoReturnValue(scriptLine.getCommand());
        }
    }

    private void blinkMRgbLights() {

        Random r = new Random();

        for (int i = 0; i < 5; i++) {

            short targetR = (short) r.nextInt(256);
            short targetG = (short) r.nextInt(256);
            short targetB = (short) r.nextInt(256);

            System.out.println("Target R,G,B:  " + targetR + ", " + targetG + ", " + targetB);

            // set random colour...
            rgbColour.setR(targetR);
            rgbColour.setG(targetG);
            rgbColour.setB(targetB);
            rgbColour.apply();

            timeDelay.pauseMillis(5_000);

            RgbBlinkMColour currentDeviceColour = rgbColour.getCurrentDeviceColour();
            System.out.println(currentDeviceColour);

            timeDelay.pauseMillis(5_000);
        }
    }

    private void blinkMHsbLights() {

        Random r = new Random();

        for (int i = 0; i < 500; i++) {

            short targetH = (short) r.nextInt(256);
            short targetS = (short) r.nextInt(256);
            short targetB = (short) r.nextInt(256);

            System.out.println("Target H,S,B:  " + targetH + ", " + targetS + ", " + targetB);

            // set random hsb colour...
            hsbColour.setH(targetH);
            hsbColour.setS(targetS);
            hsbColour.setB(targetB);
            hsbColour.fadeApply();

            timeDelay.pauseMillis(1_000);
        }
    }

    private void playScript(short scriptId) {

        // fadeApply to black(i.e. switch off..)
        rgbColour.makeBlack();
        rgbColour.fadeApply();

        timeDelay.pauseMillis(3_000);

        // play given script 20 times
        buildInScript.setId(scriptId);
        buildInScript.setRepeats(20);
        buildInScript.setLineNumber(Short.valueOf("0"));
        buildInScript.play();
    }

    private void writeScriptAndPlay(BlinkMCommandExecution commandExecution) {

        // Write
        customScript.setLineNumber(Short.parseShort("0"));
        customScript.setRepeats(10);

        BlinkMScriptLine scriptLine = new BlinkMScriptLine();
        RgbBlinkMColour colour = new RgbBlinkMColour(commandExecution);

        scriptLine.setTicks(Short.parseShort("150"));
        colour.setR(Short.parseShort("255"));
        scriptLine.setCommand(colour.changeNowRawCommand());
        customScript.write(scriptLine);

        if (!customScript.maxLinesReached()) {
            customScript.increaseLineNumber();
            colour.makeBlack();
        } else {
            System.out.println("Max lines has been reached..cannot add more lines");
        }

        scriptLine.setTicks(Short.parseShort("75"));
        colour.setG(Short.parseShort("255"));
        scriptLine.setCommand(colour.changeNowRawCommand());
        customScript.write(scriptLine);

        if (!customScript.maxLinesReached()) {
            customScript.increaseLineNumber();
            colour.makeBlack();
        } else {
            System.out.println("Max lines has been reached..cannot add more lines");
        }

        scriptLine.setTicks(Short.parseShort("150"));
        colour.setR(Short.parseShort("255"));
        scriptLine.setCommand(colour.changeNowRawCommand());
        customScript.write(scriptLine);

        customScript.setLength(Short.valueOf("3"));

        customScript.applyScriptLengthAndRepeats();

        // Play
        timeDelay.pauseMillis(5_000);
        playScript(Short.valueOf("0"));
    }

    private void doAdminTasks() {

        short deviceAddress = blinkMDeviceAdministration.getDeviceAddress();
        FirmwareVersion firmwareVersion = blinkMDeviceAdministration.getDeviceFirmwareVersion();

        System.out.println("Device address: " + deviceAddress);
        System.out.println(firmwareVersion);

        StartupParams params = new StartupParams();
        params.withPlayScriptMode();
        params.setScriptId((short) 16);
        params.setRepeats((short) 10);
        params.setFadeSpeed((short) 0x20);
        params.setScriptPlaybackSpeed((byte) 0);

        blinkMDeviceAdministration.setStartupParams(params);
    }

    private I2CDevice blinkMHandle() throws IOException {

        I2CDeviceConfig.Builder b = new I2CDeviceConfig.Builder();
        b.setAddress(DEVICE_ADDRESS, SENSOR_ADDRESS_SIZE).setControllerNumber(IC2_BUS_NUMBER);

        return DeviceManager.open(b.build());
    }

    @Override
    public void destroyApp(boolean unconditional) {
        closeIgnoringExceptions(blinkMHandle);
    }
}
