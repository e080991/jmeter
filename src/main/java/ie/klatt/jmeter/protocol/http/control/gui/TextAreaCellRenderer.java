package ie.klatt.jmeter.protocol.http.control.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TextAreaCellRenderer implements TableCellRenderer {
    private JTextArea rend = new JTextArea("");

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        rend = new JTextArea(value.toString());
        rend.setRows(3);
        return rend;
    }

    public int getPreferredHeight() {
        return rend.getPreferredSize().height + 5;
    }
}
