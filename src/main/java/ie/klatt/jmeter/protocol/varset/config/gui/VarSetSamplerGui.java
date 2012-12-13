package ie.klatt.jmeter.protocol.varset.config.gui;

import ie.klatt.jmeter.protocol.varset.sampler.VarSetSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.lang.reflect.Field;

public class VarSetSamplerGui extends AbstractSamplerGui {
    protected JLabeledTextField reference = new JLabeledTextField("Reference Name:");
    protected RSyntaxTextArea content = new RSyntaxTextArea();
    protected JLabeledChoice mode = new JLabeledChoice("Highlight Mode:", false);

    public VarSetSamplerGui() {
        init();
    }

    public void clearGui() {
        super.clearGui();
        reference.setText("");
        content.setText("");
    }

    protected void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        Container panel = makeTitlePanel();
        Box vbox = Box.createHorizontalBox();
        Box hbox = Box.createVerticalBox();
        hbox.add(Box.createVerticalStrut(8));
        hbox.add(reference);
        hbox.add(Box.createVerticalStrut(8));
        vbox.add(hbox);
        vbox.add(Box.createHorizontalGlue());
        vbox.add(mode);
        panel.add(vbox);
        panel.add(new JLabel("Content to Populate:"));
        add(panel, BorderLayout.NORTH);
        add(new RTextScrollPane(content), BorderLayout.CENTER);
        try {
            mode.setValues(null);
        } catch (Exception ignored) {
        }
        for (Field field : SyntaxConstants.class.getDeclaredFields()) {
            try {
                mode.addValue(field.get(null).toString());
            } catch (IllegalAccessException ignored) {
            }
        }
        mode.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                content.setSyntaxEditingStyle(mode.getText());
            }
        });
        mode.setText(SyntaxConstants.SYNTAX_STYLE_NONE);
    }

    public TestElement createTestElement() {
        VarSetSampler sampler = new VarSetSampler();
        this.configureTestElement(sampler);
        transfer(sampler);
        return sampler;
    }

    public void modifyTestElement(TestElement element) {
        VarSetSampler sampler = (VarSetSampler) element;
        this.configureTestElement(sampler);
        transfer(sampler);
    }

    public void transfer(VarSetSampler sampler) {
        sampler.setReference(reference.getText());
        sampler.setContent(content.getText());
        sampler.setMode(mode.getText());
    }

    public void configure(TestElement element) {
        super.configure(element);
        VarSetSampler sampler = (VarSetSampler) element;
        reference.setText(sampler.getReference());
        content.setText(sampler.getContent());
        mode.setText(sampler.getMode());
    }

    public String getStaticLabel() {
        return "Klatt :: Variable Set";
    }

    public String getLabelResource() {
        return null;
    }
}
