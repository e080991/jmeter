package ie.klatt.jmeter.protocol.http.control.gui;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.testelement.property.StringProperty;

import java.io.Serializable;

public class RequestArgument extends Argument implements Serializable {
    public void setType(String ue) {
        setProperty(new StringProperty("Type", ue));
    }

    public String getType() {
        return getPropertyAsString("Type");
    }
}
