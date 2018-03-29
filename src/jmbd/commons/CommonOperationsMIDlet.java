package jmbd.commons;

import javax.microedition.midlet.MIDlet;

public abstract class CommonOperationsMIDlet extends MIDlet {

    protected void closeIgnoringExceptions(AutoCloseable ac) {

        if (ac != null) {
            try {
                ac.close();
            } catch (Exception ex) {
                // Ignore
            }
        }
    }
}
