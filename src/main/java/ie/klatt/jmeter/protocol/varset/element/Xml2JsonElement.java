package ie.klatt.jmeter.protocol.varset.element;

import net.sf.saxon.FeatureKeys;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.om.Validation;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class Xml2JsonElement extends AbstractTestElement {
    public static final String REFERENCE = "Xml2Json.Reference";
    public static final String CONTENT = "Xml2Json.Content";
    public static final String TYPE = "Xml2Json.Type";

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

    public String getType() {
        return getPropertyAsString(TYPE);
    }

    public void setType(String Type) {
        setProperty(TYPE, Type);
    }

    public Xml2JsonElement() {
        super();
        setName("Klatt :: Variable Set (XML as JSON)");
    }

    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                    getType().equalsIgnoreCase("JsonML") ? "JsonML.xslt" :
                            "xml-to-json.xsl");
            byte[] isb = new byte[is.available()];
            is.read(isb);
            is.close();
            StreamSource oXmlSource = new StreamSource(new StringReader(
                    getContent()));
            String xslt = new String(isb, "UTF-8").replaceFirst("(skip-root.*select)=\".*\"", "$1=\"true()\"");
            StreamSource oXslSource = new StreamSource(new StringReader(
                    !getType().toLowerCase().contains("fish") ? xslt :
                            xslt.
                                    replaceFirst("(use-" + getType().toLowerCase() + ".*select)=\".*\"", "$1=\"true()\"")));
            TransformerFactoryImpl oTransformerFactory = new TransformerFactoryImpl();
            oTransformerFactory.setAttribute(FeatureKeys.SCHEMA_VALIDATION, new Integer(Validation.SKIP));
            Transformer oTransFormer = oTransformerFactory.newTransformer(oXslSource);
            StringWriter oResultStringWriter = new StringWriter();
            StreamResult oResultStream = new StreamResult(oResultStringWriter);
            oTransFormer.transform(oXmlSource, oResultStream);
            JMeterContext jmctx = JMeterContextService.getContext();
            JMeterVariables vars = jmctx.getVariables();
            vars.put(getReference(), oResultStringWriter.toString());
            result.setDataEncoding("UTF-8");
            result.setDataType(SampleResult.TEXT);
            result.setResponseData(oResultStringWriter.toString().getBytes());
            result.setSuccessful(true);
        } catch (Exception e) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            e.printStackTrace(ps);
            result.setResponseMessage(os.toString());
            result.setSuccessful(false);
        } finally {
            result.setSamplerData(getType());
            result.sampleEnd();
        }
        return result;
    }

    public void process() {
        sample(null);
    }
}
