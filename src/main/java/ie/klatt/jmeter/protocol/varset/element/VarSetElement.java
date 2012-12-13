package ie.klatt.jmeter.protocol.varset.element;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class VarSetElement extends AbstractTestElement {
    public static final String REFERENCE = "VarSet.Reference";
    public static final String CONTENT = "VarSet.Content";
    public static final String MODE = "VarSet.Mode";

    public String getReference() {
        return getPropertyAsString(REFERENCE);
    }

    public void setReference(String Reference) {
        setProperty(REFERENCE, Reference);
    }

    public String getContent() {
        return getPropertyAsString(CONTENT);
    }

    public void setContent(String Content) {
        setProperty(CONTENT, Content);
    }

    public String getMode() {
        return getPropertyAsString(MODE);
    }

    public void setMode(String Mode) {
        setProperty(MODE, Mode);
    }

    public VarSetElement() {
        super();
        setName("Klatt :: Variable Set");
    }

    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();
        try {
            JMeterContext jmctx = JMeterContextService.getContext();
            JMeterVariables vars = jmctx.getVariables();
            result.setDataEncoding("UTF-8");
            result.setDataType(SampleResult.TEXT);
            vars.put(getReference(), getContent());
            result.setResponseData((vars.get(getReference())).getBytes());
            if (getContent().equals(vars.get(getReference()))) {
                result.setSuccessful(true);
            } else {
                result.setSuccessful(false);
            }
        } catch (Exception e) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            e.printStackTrace(ps);
            result.setResponseMessage(os.toString());
            result.setSuccessful(false);
        } finally {
            result.setSamplerData(getReference() + "=" + getContent());
            result.sampleEnd();
        }
        return result;
    }

    public void process() {
        sample(null);
    }
}
