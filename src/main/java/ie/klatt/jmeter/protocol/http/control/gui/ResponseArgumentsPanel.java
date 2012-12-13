package ie.klatt.jmeter.protocol.http.control.gui;

import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.util.TextAreaTableCellEditor;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

import javax.swing.*;

public class ResponseArgumentsPanel extends ArgumentsPanel {
    public ResponseArgumentsPanel() {
        super();
    }

    public ResponseArgumentsPanel(String name) {
        super(name);
        getTable().getTableHeader().setDefaultRenderer(new HeaderRenderer());
        getTable().getColumnModel().getColumn(2).setCellEditor(new TextAreaTableCellEditor());
        getTable().getColumnModel().getColumn(2).setCellRenderer(new TextAreaCellRenderer());
        getTable().setRowHeight(getTable().getRowHeight() * 3);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("Status");
        comboBox.addItem("Headers");
        comboBox.addItem("Cookies");
        comboBox.addItem("Entity");
        getTable().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBox));
        GuiUtils.fixSize(getTable().getColumnModel().getColumn(0), getTable());
        GuiUtils.fixSize(getTable().getColumnModel().getColumn(1), getTable());
    }

    @Override
    protected void initializeTableModel() {
        tableModel = new ObjectTableModel(new String[]{
                "Neg?", "Element Type", "Value of substring to look for"},
                ResponseArgument.class,
                new Functor[]{
                        new Functor("getNegation"),
                        new Functor("getName"),
                        new Functor("getValue")
                },
                new Functor[]{
                        new Functor("setNegation"),
                        new Functor("setName"),
                        new Functor("setValue")
                },
                new Class[]{Boolean.class, String.class, String.class});
    }

    @Override
    protected ResponseArgument makeNewArgument() {
        ResponseArgument arg = new ResponseArgument();
        arg.setNegation(false);
        return arg;
    }
}
