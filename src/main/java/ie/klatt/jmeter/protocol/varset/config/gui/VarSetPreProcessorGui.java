package ie.klatt.jmeter.protocol.varset.config.gui;

import ie.klatt.jmeter.protocol.varset.modifier.VarSetModifier;
import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
import org.apache.jmeter.testelement.TestElement;

import java.awt.*;

public class VarSetPreProcessorGui extends AbstractPreProcessorGui {
    protected VarSetSamplerGui gui;

    public VarSetPreProcessorGui() {
        gui = new VarSetSamplerGui();
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
        VarSetModifier sampler = new VarSetModifier();
        this.configureTestElement(sampler);
        transfer(sampler);
        return sampler;
    }

    public void modifyTestElement(TestElement element) {
        VarSetModifier sampler = (VarSetModifier) element;
        this.configureTestElement(sampler);
        transfer(sampler);
    }

    public void transfer(VarSetModifier sampler) {
        sampler.setReference(gui.reference.getText());
        sampler.setContent(gui.content.getText());
        sampler.setMode(gui.mode.getText());
    }

    public void configure(TestElement element) {
        super.configure(element);
        VarSetModifier sampler = (VarSetModifier) element;
        gui.reference.setText(sampler.getReference());
        gui.content.setText(sampler.getContent());
        gui.mode.setText(sampler.getMode());
    }

    public String getStaticLabel() {
        return "Klatt :: Variable Set";
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
