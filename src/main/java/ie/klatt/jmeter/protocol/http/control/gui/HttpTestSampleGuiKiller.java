package ie.klatt.jmeter.protocol.http.control.gui;

import ie.klatt.jmeter.protocol.http.sampler.HttpClient;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;

import javax.swing.*;
import java.awt.*;

public class HttpTestSampleGuiKiller extends AbstractSamplerGui {
    private JLabeledTextField host = new JLabeledTextField("Host");
    private JLabeledTextField port = new JLabeledTextField("Port");
    private JLabeledTextField clients = new JLabeledTextField("Clients");
    private JLabeledTextField runtime = new JLabeledTextField("Runtime");
    private JLabeledChoice method = new JLabeledChoice("Method", new String[]{"GET"});
    private JLabeledTextField path = new JLabeledTextField("Path");
    private JLabeledTextField timeout = new JLabeledTextField("Timeout");
    private RequestArgumentsPanel request = new RequestArgumentsPanel("Request Data");
    private ResponseArgumentsPanel response = new ResponseArgumentsPanel("Response Assertions");

    public HttpTestSampleGuiKiller() {
        init();
    }

    public void clearGui() {
        super.clearGui();
        host.setText("");
        port.setText("");
        clients.setText("");
        runtime.setText("");
        method.setText("");
        path.setText("");
        timeout.setText("");
        request.clearGui();
        response.clearGui();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        Container panel = makeTitlePanel();
        Box box = Box.createHorizontalBox();
        box.add(host);
        box.add(Box.createHorizontalGlue());
        box.add(port);
        box.add(Box.createHorizontalGlue());
        box.add(clients);
        box.add(Box.createHorizontalGlue());
        box.add(runtime);
        panel.add(box);
        box = Box.createHorizontalBox();
        box.add(method);
        box.add(Box.createHorizontalGlue());
        box.add(path);
        box.add(Box.createHorizontalGlue());
        box.add(timeout);
        panel.add(box);
        add(panel, BorderLayout.NORTH);
        box = Box.createVerticalBox();
        box.add(request);
        box.add(Box.createVerticalGlue());
        box.add(response);
        add(box, BorderLayout.CENTER);
    }

    public TestElement createTestElement() {
        HttpClient sampler = new HttpClient();
        this.configureTestElement(sampler);
        transfer(sampler);
        return sampler;
    }

    public void modifyTestElement(TestElement element) {
        HttpClient sampler = (HttpClient) element;
        this.configureTestElement(sampler);
        transfer(sampler);
    }

    public void transfer(HttpClient sampler) {
        sampler.setHost(host.getText());
        sampler.setPort(port.getText());
        sampler.setClients(clients.getText());
        sampler.setRuntime(runtime.getText());
        sampler.setMethod(method.getText());
        sampler.setPath(path.getText());
        sampler.setTimeout(timeout.getText());
        sampler.setProperty(new TestElementProperty("request", request.createTestElement()));
        sampler.setProperty(new TestElementProperty("response", response.createTestElement()));
    }

    public void configure(TestElement element) {
        super.configure(element);
        HttpClient sampler = (HttpClient) element;
        host.setText(sampler.getHost());
        port.setText(sampler.getPort());
        clients.setText(sampler.getClients());
        runtime.setText(sampler.getRuntime());
        method.setText(sampler.getMethod());
        path.setText(sampler.getPath());
        timeout.setText(sampler.getTimeout());
        request.configure((Arguments) sampler.getProperty("request").getObjectValue());
        response.configure((Arguments) sampler.getProperty("response").getObjectValue());
    }

    public String getStaticLabel() {
        return "Klatt :: HTTP Killer Sampler";
    }

    public String getLabelResource() {
        return null;
    }
}
