package ie.klatt.jmeter.protocol.varset.config.gui;

import ie.klatt.jmeter.protocol.varset.modifier.Xml2JsonModifier;
import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
import org.apache.jmeter.testelement.TestElement;

import java.awt.*;

public class Xml2JsonPreProcessorGui extends AbstractPreProcessorGui {
    private Xml2JsonSamplerGui gui;

    public Xml2JsonPreProcessorGui() {
        gui = new Xml2JsonSamplerGui();
        init();
    }

    public void clearGui() {
        super.clearGui();
        gui.clearGui();
    }

    protected void init() {
        setLayout(new BorderLayout());
        gui.init();
        add(gui);
    }

    public TestElement createTestElement() {
        Xml2JsonModifier sampler = new Xml2JsonModifier();
        this.configureTestElement(sampler);
        transfer(sampler);
        return sampler;
    }

    public void modifyTestElement(TestElement element) {
        Xml2JsonModifier sampler = (Xml2JsonModifier) element;
        this.configureTestElement(sampler);
        transfer(sampler);
    }

    public void transfer(Xml2JsonModifier sampler) {
        sampler.setType(gui.type.getText());
        sampler.setContent(gui.content.getText());
        sampler.setReference(gui.reference.getText());
    }

    public void configure(TestElement element) {
        super.configure(element);
        Xml2JsonModifier sampler = (Xml2JsonModifier) element;
        gui.type.setText(sampler.getType());
        gui.content.setText(sampler.getContent());
        gui.reference.setText(sampler.getReference());
    }

    public String getStaticLabel() {
        return "Klatt :: Variable Set (XML as JSON)";
    }

    public String getLabelResource() {
        return null;
    }

    public void setName(String name) {
        if (gui != null)
            gui.setName(name);
        else
            super.setName(name);
    }

    public String getName() {
        if (gui != null)
            return gui.getName();
        else
            return super.getName();
    }
}
