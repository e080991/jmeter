package ie.klatt.jmeter.protocol.ssh.sampler;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.*;

public class SSHSampler extends AbstractSampler {
    private static final Logger _logger = LoggingManager.getLoggerForClass();

    private static final String SSH_TIMEOUT = "SSHSampler.timeout";
    private static final String SSH_USERNAME = "SSHSampler.username";
    private static final String SSH_HOST = "SSHSampler.host";
    private static final String SSH_PORT = "SSHSampler.port";
    private static final String SSH_PASSWORD = "SSHSampler.password";
    private static final String SSH_STRICT_HOST_KEY_CHECKING = "SSHSampler.strictHostKeyChecking";
    private static final String SSH_ALLOCATE_PTY = "SSHSampler.allocatePty";
    private static final String SSH_IGNORE_STATUS = "SSHSampler.ignoreStatus";
    private static final String SSH_IDENTITY = "SSHSampler.identities";
    private static final String SSH_IDENTITY_PASSWORD = "SSHSampler.identitiesPassword";
    private static final String SSH_COMMAND = "SSHSampler.command";


    private static final JSch jsch = new JSch();
    private Session session = null;

    public SSHSampler() {
        super();
        setName("Klatt :: SSH Sampler");
    }

    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSamplerData(
                "Server: ssh " +
                        (getAllocatePty().equals("true") ? "-t " : "") +
                        (getPort().equals("22") ? "" : "-p " + getPort() + " ") +
                        (new File(getIdentity()).isFile() ? "-i " + getIdentity() + " " : "") +
                        getUsername() + "@" + getHostname() + "\n" +
                        "Command:\n" + getCommand());
        result.setDataType(SampleResult.TEXT);
        result.setContentType("text/plain");

        String response;

        try {
            if (session == null) {
                connect();
            }
        } catch (Exception e) {
            if (session == null) {
                _logger.error("Failed to connect to server with credentials " + getUsername() + "@" + getHostname() + ":" + (getPort().length() != 0 ? getPort() : "22") + " pw=" + getPassword() + " identity=" + getIdentity());
            }
            return handleException(result, e);
        }

        try {
            response = doCommand(session, getCommand(), result);
            result.setResponseData(response.getBytes());
            result.setResponseCodeOK();
            result.setResponseMessageOK();

            disconnect();
            return result;
        } catch (Exception e) {
            return handleException(result, e);
        }
    }

    private SampleResult handleException(SampleResult result, Exception e) {
        return handleException(result, e, false);
    }

    private SampleResult handleException(SampleResult result, Exception e, boolean succeeds) {
        result.setSuccessful(succeeds);
        result.setResponseCode(e.getClass().getName());

        StringWriter writer = new StringWriter();

        StackTraceElement[] els = e.getStackTrace();
        for (StackTraceElement el : els) {
            writer.write('\n');
            writer.write(el.toString());
        }

        result.setResponseMessage(e.getMessage() + "\n" + writer.toString());

        disconnect();
        return result;
    }


    private String doCommand(Session session, String command, SampleResult result) throws JSchException, IOException {
        StringBuilder sb = new StringBuilder();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        PipedInputStream remoteInputStream = new PipedInputStream();
        OutputStream outputStream = new PipedOutputStream(remoteInputStream);
        outputStream.close();
        PipedInputStream InputStream = new PipedInputStream();
        OutputStream remoteOutputStream = new PipedOutputStream(InputStream);
        channel.setInputStream(remoteInputStream);
        channel.setOutputStream(remoteOutputStream);
        channel.setErrStream(remoteOutputStream);
        channel.setPty(Boolean.parseBoolean(getAllocatePty()));
        BufferedReader br = new BufferedReader(new InputStreamReader(InputStream));
        result.sampleStart();
        channel.connect();

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            sb.append(line);
            sb.append("\n");
        }
        channel.disconnect();
        result.sampleEnd();
        result.setSuccessful(Boolean.parseBoolean(getIgnoreStatus()) || !channel.isClosed() || channel.getExitStatus() == 0);

        return sb.toString();
    }

    public void connect() throws JSchException {
        try {
            String identity = getIdentity().trim();
            String identityPassword = getIdentityPassword().trim();
            if (identity.length() != 0) {
                if (identityPassword.length() == 0) {
                    identityPassword = null;
                }
                try {
                    FileInputStream check = new FileInputStream(identity);
                    check.close();
                } catch (IOException exception) {
                    try {
                        File file = File.createTempFile("id_rsa", "jmeter");
                        file.deleteOnExit();
                        Runtime.getRuntime().exec("chmod 600 " + file.getCanonicalPath());
                        FileOutputStream stream = new FileOutputStream(file);
                        stream.write(identity.getBytes());
                        stream.close();
                        identity = file.getCanonicalPath();
                    } catch (IOException ignored) {
                    }
                }
                jsch.addIdentity(identity, identityPassword);
            }
            session = jsch.getSession(getUsername(), getHostname(), (getPort() == null || getPort().length() == 0) ? 22 : Integer.parseInt(getPort()));
            session.setPassword(getPassword());
            session.setConfig("StrictHostKeyChecking", getStrictHostKeyChecking());
            session.connect(Integer.parseInt(getTimeout()));
        } catch (JSchException e) {
            disconnect();
            throw e;
        }
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    public String getUsername() {
        return getPropertyAsString(SSH_USERNAME);
    }

    public void setUsername(String username) {
        setProperty(SSH_USERNAME, username);
    }

    public String getHostname() {
        return getPropertyAsString(SSH_HOST);
    }

    public void setHostname(String hostname) {
        setProperty(SSH_HOST, hostname);
    }

    public String getPort() {
        return getPropertyAsString(SSH_PORT);
    }

    public void setPort(String port) {
        setProperty(SSH_PORT, port);
    }

    public String getPassword() {
        return getPropertyAsString(SSH_PASSWORD);
    }

    public void setPassword(String password) {
        setProperty(SSH_PASSWORD, password);
    }

    public String getTimeout() {
        return getPropertyAsString(SSH_TIMEOUT);
    }

    public void setTimeout(String timeout) {
        setProperty(SSH_TIMEOUT, timeout);
    }

    public String getStrictHostKeyChecking() {
        return getPropertyAsString(SSH_STRICT_HOST_KEY_CHECKING);
    }

    public void setStrictHostKeyChecking(String strictHostKeyChecking) {
        setProperty(SSH_STRICT_HOST_KEY_CHECKING, strictHostKeyChecking);
    }

    public String getAllocatePty() {
        return getPropertyAsString(SSH_ALLOCATE_PTY);
    }

    public void setAllocatePty(String ignoreStatus) {
        setProperty(SSH_ALLOCATE_PTY, ignoreStatus);
    }

    public String getIgnoreStatus() {
        return getPropertyAsString(SSH_IGNORE_STATUS);
    }

    public void setIgnoreStatus(String ignoreStatus) {
        setProperty(SSH_IGNORE_STATUS, ignoreStatus);
    }

    public String getIdentity() {
        return getPropertyAsString(SSH_IDENTITY);
    }

    public void setIdentity(String identity) {
        setProperty(SSH_IDENTITY, identity);
    }

    public String getIdentityPassword() {
        return getPropertyAsString(SSH_IDENTITY_PASSWORD);
    }

    public void setIdentityPassword(String identityPassword) {
        setProperty(SSH_IDENTITY_PASSWORD, identityPassword);
    }

    public String getCommand() {
        return getPropertyAsString(SSH_COMMAND);
    }

    public void setCommand(String command) {
        setProperty(SSH_COMMAND, command);
    }

    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable ignored) {
        } finally {
            disconnect();
        }
    }
}
