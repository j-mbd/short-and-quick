package jmbd.spi.mcp49x1;

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
 * @author savvas
 */
public class Mcp4901SpiCommand extends Mcp49x1SpiCommand {

    public Mcp4901SpiCommand(Mcp49x1SpiSlave mcp49x1SpiSlave) {

        super(mcp49x1SpiSlave);
    }

    @Override
    protected int mergedCommand() {

        // first four bits of the command are ignored in 4901, so value should be shifted left
        data <<= 4;

        // now merge that with configuration portion (beginning of value must fall right next to configuration)
        return getConfigurationValue() | data;
    }

    @Override
    public int getMinDataValue() {

        return 0;
    }

    @Override
    public int getMaxDataValue() {
        // data portion of register in 4901 is 1-byte so max value must be 255
        return 255;
    }
}
