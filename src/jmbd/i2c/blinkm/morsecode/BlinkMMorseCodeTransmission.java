package jmbd.i2c.blinkm.morsecode;

import jmbd.commons.TimeDelay;
import jmbd.i2c.blinkm.colour.RgbBlinkMVisualEffect;

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
public class BlinkMMorseCodeTransmission extends MorseCodeTransmission {

    private final RgbBlinkMVisualEffect rgbBlinkMVisualEffect;

    public BlinkMMorseCodeTransmission(RgbBlinkMVisualEffect rgbBlinkMVisualEffect, TimeDelay timeDelay) {

        super(timeDelay);
        this.rgbBlinkMVisualEffect = rgbBlinkMVisualEffect;
    }

    @Override
    protected void signalOn() {

        rgbBlinkMVisualEffect.goWhite();
    }

    @Override
    protected void signalOff() {

        rgbBlinkMVisualEffect.goBlack();
    }
}
