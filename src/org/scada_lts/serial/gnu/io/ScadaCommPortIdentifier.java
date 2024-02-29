package org.scada_lts.serial.gnu.io;


import java.util.Enumeration;

public class ScadaCommPortIdentifier {

    public static Enumeration getPortIdentifiers() {
        return gnu.io.CommPortIdentifier.getPortIdentifiers();
    }
}
