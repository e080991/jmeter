package ie.klatt.jmeter.protocol.http.sampler;

import ie.klatt.jmeter.protocol.http.control.gui.RequestArgument;
import ie.klatt.jmeter.protocol.http.control.gui.ResponseArgument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpClient extends AbstractSampler {
    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();
        ClientBootstrap bootstrap = null;
        HttpClientPipelineFactory factory = null;
        try {
            result.setDataEncoding("UTF-8");
            result.setDataType(SampleResult.TEXT);

            bootstrap = new ClientBootstrap(
                    new NioClientSocketChannelFactory(
                            Executors.newCachedThreadPool(),
                            Executors.newCachedThreadPool()));
            bootstrap.setOption("connectTimeoutMillis", Integer.parseInt(getTimeout()) * 1000);
            bootstrap.setOption("remoteAddress", new InetSocketAddress(getHost(), Integer.parseInt(getPort())));
            factory = new HttpClientPipelineFactory(bootstrap);
            bootstrap.setPipelineFactory(factory);
            factory.setAvailable(true);
            factory.setHost(getHost());
            factory.setPath(getPath());
            factory.setTimeout(Integer.parseInt(getTimeout()));

            Arguments request = (Arguments) getProperty("request").getObjectValue();
            for (int i = 0; i < request.getArgumentCount(); i++) {
                RequestArgument argument = (RequestArgument) request.getArgument(i);
                String name = argument.getName();
                String values[] = argument.getValue().split("[\\r?\\n]+");
                if (argument.getType().equals("Cookie")) {
                    for (String value : values) {
                        factory.addCookie(name, value);
                    }
                } else if (argument.getType().equals("Header")) {
                    for (String value : values) {
                        factory.addHeader(name, value);
                    }
                } else if (argument.getType().equals("Entity")) {
                }
            }

            Arguments response = (Arguments) getProperty("response").getObjectValue();
            for (int i = 0; i < response.getArgumentCount(); i++) {
                ResponseArgument argument = (ResponseArgument) response.getArgument(i);
                String name = argument.getName();
                String value = argument.getValue();
                factory.addAssertion(name, value, argument.getNegation());
            }

            for (int i = 0; i < Integer.parseInt(getClients()); i++) {
                bootstrap.connect();
            }

            Thread.sleep((Integer.parseInt(getRuntime()) - Integer.parseInt(getTimeout())) * 1000);
            factory.setAvailable(false);
            Thread.sleep(Integer.parseInt(getTimeout()) * 1000);
            if (factory.getLastException() != null) {
                throw factory.getLastException();
            } else if (factory.getLastError() != null) {
                result.setResponseData(factory.getLastError(), "UTF-8");
                result.setSuccessful(false);
            } else {
                result.setResponseData(String.valueOf(factory.getOK()), "UTF-8");
                result.setSuccessful(true);
            }
        } catch (Throwable e) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            e.printStackTrace(ps);
            result.setResponseMessage(os.toString());
            result.setSuccessful(false);
        } finally {
            if (factory != null) {
                factory.setAvailable(false);
                for (int i = 0; i < 3; i++) {
                    factory.closeChannels();
                    try {
                        Thread.sleep(Integer.parseInt(getTimeout()) * 1000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            if (bootstrap != null) {
                bootstrap.releaseExternalResources();
            }
            result.sampleEnd();
        }
        return result;
    }

    private static final String HOST = "Host";

    public String getHost() {
        return getPropertyAsString(HOST);
    }

    public void setHost(String host) {
        setProperty(HOST, host);
    }

    private static final String PORT = "Port";

    public String getPort() {
        return getPropertyAsString(PORT);
    }

    public void setPort(String port) {
        setProperty(PORT, port);
    }

    private static final String CLIENTS = "Clients";

    public String getClients() {
        return getPropertyAsString(CLIENTS);
    }

    public void setClients(String clients) {
        setProperty(CLIENTS, clients);
    }

    private static final String TIMEOUT = "Timeout";

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT);
    }

    public void setTimeout(String timeout) {
        setProperty(TIMEOUT, timeout);
    }

    private static final String METHOD = "Method";

    public String getMethod() {
        return getPropertyAsString(METHOD);
    }

    public void setMethod(String method) {
        setProperty(METHOD, method);
    }

    private static final String PATH = "Path";

    public String getPath() {
        return getPropertyAsString(PATH);
    }

    public void setPath(String path) {
        setProperty(PATH, path);
    }

    private static final String RUNTIME = "Runtime";

    public String getRuntime() {
        return getPropertyAsString(RUNTIME);
    }

    public void setRuntime(String runtime) {
        setProperty(RUNTIME, runtime);
    }
}
