package ie.klatt.jmeter.protocol.ssh.config.gui;

import ie.klatt.jmeter.protocol.ssh.sampler.SSHSampler;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledRadio;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class SSHConfigGui extends AbstractSamplerGui {
    private JLabeledTextField host = new JLabeledTextField("Host");
    private JLabeledTextField port = new JLabeledTextField("Port");
    private JLabeledTextField username = new JLabeledTextField("Username");
    private JLabeledTextField password = new JLabeledTextField("Password");
    private JLabeledTextField timeout = new JLabeledTextField("Timeout");
    private JLabeledRadio strictHostKeyChecking = new JLabeledRadio("Strict host key checking", new String[]{"yes", "no"});
    private JCheckBox allocatePty = new JCheckBox("Allocate PTY", false);
    private JCheckBox ignoreStatus = new JCheckBox("Ignore Exit Status", true);
    private JLabeledTextArea identity = new JLabeledTextArea("Private Key File Path/Content");
    private JLabeledTextField identityPassword = new JLabeledTextField("Private Key Password");
    private RSyntaxTextArea command = new RSyntaxTextArea();


    public SSHConfigGui() {
        init();
    }

    public void clearGui() {
        super.clearGui();
        host.setText("");
        port.setText("22");
        username.setText("");
        password.setText("");
        timeout.setText("5000");
        strictHostKeyChecking.setText("no");
        allocatePty.setSelected(false);
        ignoreStatus.setSelected(true);
        identity.setText("");
        identityPassword.setText("");
        command.setText("");
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        Box mainPanel = Box.createVerticalBox();

        JPanel sshSamplerPanel = new JPanel(new BorderLayout());
        sshSamplerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SSH Sampler"));

        JPanel conPanel = new VerticalPanel();
        conPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Connection"));
        conPanel.add(host);
        conPanel.add(port);
        JPanel conBottomPanel = new HorizontalPanel();
        JPanel wrapper = new VerticalPanel();
        wrapper.add(Box.createVerticalStrut(2));
        wrapper.add(timeout);
        conBottomPanel.add(wrapper);
        conBottomPanel.add(strictHostKeyChecking);
        conBottomPanel.add(allocatePty);
        conBottomPanel.add(ignoreStatus);
        conPanel.add(conBottomPanel);

        JPanel reqPanel = new JPanel(new GridLayout(1, 2, 0, 5));
        reqPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Command"));
        command.setSyntaxEditingStyle("text/unix");
        reqPanel.add(new RTextScrollPane(command));

        JPanel idPanel = new VerticalPanel();
        idPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Identity"));
        idPanel.add(username);
        idPanel.add(password);
        idPanel.add(identity);
        idPanel.add(identityPassword);

        sshSamplerPanel.add(conPanel, BorderLayout.NORTH);
        sshSamplerPanel.add(reqPanel, BorderLayout.CENTER);
        sshSamplerPanel.add(idPanel, BorderLayout.SOUTH);

        mainPanel.add(sshSamplerPanel, BorderLayout.NORTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    public TestElement createTestElement() {
        SSHSampler sampler = new SSHSampler();
        this.configureTestElement(sampler);
        transfer(sampler);
        return sampler;
    }

    public void modifyTestElement(TestElement element) {
        SSHSampler sampler = (SSHSampler) element;
        this.configureTestElement(sampler);
        transfer(sampler);
    }

    public void configure(TestElement element) {
        super.configure(element);
        SSHSampler sampler = (SSHSampler) element;
        host.setText(sampler.getHostname());
        port.setText(sampler.getPort());
        username.setText(sampler.getUsername());
        password.setText(sampler.getPassword());
        timeout.setText(sampler.getTimeout());
        strictHostKeyChecking.setText(sampler.getStrictHostKeyChecking());
        allocatePty.setSelected(Boolean.parseBoolean(sampler.getAllocatePty()));
        ignoreStatus.setSelected(Boolean.parseBoolean(sampler.getIgnoreStatus()));
        identity.setText(sampler.getIdentity());
        identityPassword.setText(sampler.getIdentityPassword());
        command.setText(sampler.getCommand());
    }


    public void transfer(SSHSampler sampler) {
        sampler.setHostname(host.getText());
        sampler.setPort(port.getText());
        sampler.setUsername(username.getText());
        sampler.setPassword(password.getText());
        sampler.setTimeout(timeout.getText());
        sampler.setStrictHostKeyChecking(strictHostKeyChecking.getText());
        sampler.setAllocatePty(String.valueOf(allocatePty.isSelected()));
        sampler.setIgnoreStatus(String.valueOf(ignoreStatus.isSelected()));
        sampler.setIdentity(identity.getText());
        sampler.setIdentityPassword(identityPassword.getText());
        sampler.setCommand(command.getText());
    }

    public String getLabelResource() {
        return null;
    }

    public String getStaticLabel() {
        return "Klatt :: SSH Sampler";
    }
}
