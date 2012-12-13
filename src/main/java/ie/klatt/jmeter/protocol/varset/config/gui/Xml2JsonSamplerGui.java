package ie.klatt.jmeter.protocol.varset.config.gui;

import ie.klatt.jmeter.protocol.varset.sampler.Xml2JsonSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class Xml2JsonSamplerGui extends AbstractSamplerGui {
    protected JLabeledTextField reference = new JLabeledTextField("Reference Name:");
    protected RSyntaxTextArea content = new RSyntaxTextArea();
    protected JLabeledChoice type = new JLabeledChoice("Output Type:",
            new String[]{"BadgerFish",
                    "JsonML",
                    "Natural",
                    "RabbitFish",
                    "RayFish"},
            false);

    public Xml2JsonSamplerGui() {
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
        vbox.add(type);
        panel.add(vbox);
        panel.add(new JLabel("Content to Transform and Populate:"));
        add(panel, BorderLayout.NORTH);
        add(new RTextScrollPane(content), BorderLayout.CENTER);
        content.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        type.setText("Natural");
    }

    public TestElement createTestElement() {
        Xml2JsonSampler sampler = new Xml2JsonSampler();
        this.configureTestElement(sampler);
        transfer(sampler);
        return sampler;
    }

    public void modifyTestElement(TestElement element) {
        Xml2JsonSampler sampler = (Xml2JsonSampler) element;
        this.configureTestElement(sampler);
        transfer(sampler);
    }

    public void transfer(Xml2JsonSampler sampler) {
        sampler.setType(type.getText());
        sampler.setContent(content.getText());
        sampler.setReference(reference.getText());
    }

    public void configure(TestElement element) {
        super.configure(element);
        Xml2JsonSampler sampler = (Xml2JsonSampler) element;
        type.setText(sampler.getType());
        content.setText(sampler.getContent());
        reference.setText(sampler.getReference());
    }

    public String getStaticLabel() {
        return "Klatt :: Variable Set (XML as JSON)";
    }

    public String getLabelResource() {
        return null;
    }
}