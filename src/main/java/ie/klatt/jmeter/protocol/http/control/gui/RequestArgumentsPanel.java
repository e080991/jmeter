package ie.klatt.jmeter.protocol.http.control.gui;

import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.util.TextAreaTableCellEditor;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

import javax.swing.*;

public class RequestArgumentsPanel extends ArgumentsPanel {
    public RequestArgumentsPanel() {
        super();
    }

    public RequestArgumentsPanel(String name) {
        super(name);
        getTable().getTableHeader().setDefaultRenderer(new HeaderRenderer());
        getTable().getColumnModel().getColumn(2).setCellEditor(new TextAreaTableCellEditor());
        getTable().getColumnModel().getColumn(2).setCellRenderer(new TextAreaCellRenderer());
        getTable().setRowHeight(getTable().getRowHeight() * 3);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("Header");
        comboBox.addItem("Cookie");
        comboBox.addItem("Entity");
        getTable().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboBox));
        GuiUtils.fixSize(getTable().getColumnModel().getColumn(0), getTable());
        GuiUtils.fixSize(getTable().getColumnModel().getColumn(1), getTable());
    }

    @Override
    protected void initializeTableModel() {
        tableModel = new ObjectTableModel(new String[]{
                "Set Type", "Name", "Value"},
                RequestArgument.class,
                new Functor[]{
                        new Functor("getType"),
                        new Functor("getName"),
                        new Functor("getValue")
                },
                new Functor[]{
                        new Functor("setType"),
                        new Functor("setName"),
                        new Functor("setValue")
                },
                new Class[]{String.class, String.class, String.class});
    }

    @Override
    protected RequestArgument makeNewArgument() {
        RequestArgument arg = new RequestArgument();
        arg.setType("Header");
        return arg;
    }
}
