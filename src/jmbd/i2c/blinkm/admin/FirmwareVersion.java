package jmbd.i2c.blinkm.admin;

import jmbd.commons.ByteConversion;

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
public class FirmwareVersion {

    private final short major;
    private final short minor;

    public FirmwareVersion(short major, short minor) {

        this.major = major;
        this.minor = minor;
    }

    public short getMajor() {

        return major;
    }

    public short getMinor() {

        return minor;
    }

    @Override
    public String toString() {

        return "FirmwareVersion{" + "major=" + major + ", minor=" + minor + '}';
    }

    /**
     * REQUIRES:
     *
     * 1) r not null
     *
     * 2) r.length == 2
     *
     * @param r
     * @return
     */
    public static FirmwareVersion fromRawValues(byte[] r) {

        short major, minor;
        ByteConversion bc = new ByteConversion();

        bc.setByte(r[0]);
        major = bc.asShort();

        bc.setByte(r[1]);
        minor = bc.asShort();

        return new FirmwareVersion(major, minor);
    }
}
