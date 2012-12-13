package ie.klatt.jmeter.protocol.http.sampler;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jboss.netty.channel.Channels.pipeline;

public class HttpClientPipelineFactory implements ChannelPipelineFactory {
    private ClientBootstrap bootstrap;
    private boolean available;
    private String host;
    private String method;
    private String path;
    private int timeout;
    private final HashMap<String, LinkedList<String>> headers = new HashMap<String, LinkedList<String>>();
    private HashMap<String, LinkedList<String>> cookies = new HashMap<String, LinkedList<String>>();
    private final HashMap<String, HashMap<String, Boolean>> assertions = new HashMap<String, HashMap<String, Boolean>>();
    private Random random = new Random();
    private Timer timer = new HashedWheelTimer();
    private AtomicInteger ok = new AtomicInteger();
    private AtomicInteger cols = new AtomicInteger();
    private Throwable exception;
    private String lastError;
    private final HashSet<Channel> channels = new HashSet<Channel>();

    public HttpClientPipelineFactory(ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public HashMap<String, String> getCookies() {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String key : cookies.keySet()) {
            result.put(key, cookies.get(key).get(random.nextInt(cookies.get(key).size())));
        }
        return result;
    }

    public void addCookie(String name, String value) {
        if (cookies.get(name) == null) {
            cookies.put(name, new LinkedList<String>());
        }
        System.err.println("Adding " + name + " " + value);
        cookies.get(name).add(value);
    }

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String key : headers.keySet()) {
            result.put(key, headers.get(key).get(random.nextInt(headers.get(key).size())));
        }
        return result;
    }

    public void addHeader(String name, String value) {
        if (headers.get(name) == null) {
            headers.put(name, new LinkedList<String>());
        }
        headers.get(name).add(value);
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("timeout", new ReadTimeoutHandler(timer, timeout));
        pipeline.addLast("codec", new HttpClientCodec());
        pipeline.addLast("inflater", new HttpContentDecompressor());
        pipeline.addLast("handler", new HttpResponseHandler(bootstrap, this));
        return pipeline;
    }

    public void addAssertion(String name, String value, boolean negation) {
        if (assertions.get(name) == null) {
            assertions.put(name, new HashMap<String, Boolean>());
        }
        assertions.get(name).put(value, negation);
    }

    public HashMap<String, Boolean> getAsserion(String name) {
        return assertions.get(name);
    }

    public void replaceCookie(String name, String value, String old) {
        HashMap<String, LinkedList<String>> newCookies = (HashMap<String, LinkedList<String>>) cookies.clone();
        for (String key : newCookies.keySet()) {
            newCookies.put(key, (LinkedList<String>) newCookies.get(key).clone());
        }
        if (newCookies.get(name) == null) {
            newCookies.put(name, new LinkedList<String>());
        }
        newCookies.get(name).remove(old);
        newCookies.get(name).add(value);
        cookies = newCookies;
    }

    public void newColumn(char x) {
        System.out.print(x);
        if (cols.incrementAndGet() == 80) {
            System.out.println("");
            cols.set(0);
        }
    }

    public void incrementOK() {
        ok.incrementAndGet();
    }

    public int getOK() {
        return ok.get();
    }

    public void setLastException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getLastException() {
        return exception;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getLastError() {
        return lastError;
    }

    public void addChannel(Channel channel) {
        synchronized (channels) {
            channels.add(channel);
        }
    }

    public void removeChannel(Channel channel) {
        synchronized (channels) {
            channels.remove(channel);
        }
    }

    public void closeChannels() {
        HashSet<Channel> currentChannels;
        synchronized (channels) {
            currentChannels = (HashSet<Channel>) channels.clone();
        }
        for (Channel channel : currentChannels) {
            channel.close();
        }
    }
}
