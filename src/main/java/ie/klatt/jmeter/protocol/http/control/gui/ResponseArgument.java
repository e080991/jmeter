package ie.klatt.jmeter.protocol.http.control.gui;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.testelement.property.BooleanProperty;

import java.io.Serializable;

public class ResponseArgument extends Argument implements Serializable {
    public void setNegation(boolean ue) {
        setProperty(new BooleanProperty("Negation", ue));
    }

    public boolean getNegation() {
        return getPropertyAsBoolean("Negation");
    }
}
