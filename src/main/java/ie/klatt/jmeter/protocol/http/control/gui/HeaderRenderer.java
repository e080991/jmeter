package ie.klatt.jmeter.protocol.http.control.gui;

import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;

public class HeaderRenderer extends HeaderAsPropertyRenderer {
    protected String getText(Object value, int row, int column) {
        return (String) value;
    }
}
