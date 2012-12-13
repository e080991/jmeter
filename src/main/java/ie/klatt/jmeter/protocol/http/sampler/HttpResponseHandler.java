package ie.klatt.jmeter.protocol.http.sampler;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
    private boolean readingChunks;
    private ClientBootstrap bootstrap;
    private HttpClientPipelineFactory factory;
    private HttpRequest request;
    private HttpResponse response;
    private HashMap<String, String> myCookies = new HashMap<String, String>();

    public HttpResponseHandler(ClientBootstrap bootstrap, HttpClientPipelineFactory httpClientPipelineFactory) {
        this.bootstrap = bootstrap;
        this.factory = httpClientPipelineFactory;
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, factory.getPath());
        request.setHeader(HttpHeaders.Names.HOST, factory.getHost());

        CookieEncoder httpCookieEncoder = new CookieEncoder(false);
        myCookies = factory.getCookies();
        for (Map.Entry<String, String> entry : myCookies.entrySet()) {
            httpCookieEncoder.addCookie(entry.getKey(), entry.getValue());
        }
        request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

        for (Map.Entry<String, String> entry : factory.getHeaders().entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }

        ctx.getChannel().write(request);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        factory.addChannel(ctx.getChannel());
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        factory.removeChannel(ctx.getChannel());
        if (factory.isAvailable()) {
            bootstrap.connect();
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!readingChunks) {
            response = (HttpResponse) e.getMessage();

            HashMap<String, Boolean> assertionStatus = factory.getAsserion("Status");

            if (assertionStatus != null) {
                String data = response.getStatus().toString();
                for (String key : assertionStatus.keySet()) {
                    if (data.contains(key) == assertionStatus.get(key)) {
                        throw new SomethingDidGoWrong("Incorrect status, got '" + data + "' expected '" + key + (assertionStatus.get(key) ? "' not" : "'"));
                    }
                }
            }

            HashMap<String, Boolean> assertionHeaders = factory.getAsserion("Headers");
            HashMap<String, Boolean> assertionCookies = factory.getAsserion("Cookies");

            Set<Cookie> cookies = new HashSet<Cookie>();

            for (String name : response.getHeaderNames()) {
                for (String value : response.getHeaders(name)) {
                    if (name.equalsIgnoreCase("Set-Cookie")) {
                        CookieDecoder httpCookieDecoder = new CookieDecoder(true);
                        System.out.println("name " + name);
                        System.out.println("value " + value);
                        cookies = httpCookieDecoder.decode(value);
                        for (Cookie cookie : cookies) {
                            System.err.println("");
                            System.err.println("Replacing cookie " + cookie.getName()
                                    + " from " + myCookies.get(cookie.getName())
                                    + " to " + cookie.getValue());
                            System.err.println("");
                            factory.replaceCookie(cookie.getName(), cookie.getValue(), myCookies.get(cookie.getName()));
                        }
                    }
                }
            }

            if (assertionCookies != null) {
                String data = cookies.toString();
                for (String key : assertionCookies.keySet()) {
                    if (data.contains(key) == assertionCookies.get(key)) {
                        throw new SomethingDidGoWrong("Incorrect cookies, got '" + data + "' expected '" + key + (assertionCookies.get(key) ? "' not" : "'"));
                    }
                }
            }

            if (assertionHeaders != null) {
                String data = response.getHeaders().toString();
                for (String key : assertionHeaders.keySet()) {
                    if (data.contains(key) == assertionHeaders.get(key)) {
                        throw new SomethingDidGoWrong("Incorrect headers, got '" + data + " expected '" + key + (assertionHeaders.get(key) ? "' not" : "'"));
                    }
                }
            }

            if (response.isChunked()) {
                readingChunks = true;
            } else {
                ChannelBuffer content = response.getContent();
                if (content.readable()) {
                    HashMap<String, Boolean> assertionEntity = factory.getAsserion("Entity");
                    if (assertionEntity != null) {
                        String data = content.toString(CharsetUtil.UTF_8);
                        for (String key : assertionEntity.keySet()) {
                            if (data.contains(key) == assertionEntity.get(key)) {
                                throw new SomethingDidGoWrong("Incorrect body, got '" + data + "' expected '" + key + (assertionEntity.get(key) ? "' not" : "'"));
                            }
                        }
                    }
                }
                factory.newColumn('.');
                ctx.getChannel().close();
                factory.incrementOK();
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;
            } else {
                ChannelBuffer content = chunk.getContent();
                if (content.readable()) {
                    HashMap<String, Boolean> assertionEntity = factory.getAsserion("Entity");
                    if (assertionEntity != null) {
                        String data = content.toString(CharsetUtil.UTF_8);
                        for (String key : assertionEntity.keySet()) {
                            if (data.contains(key) == assertionEntity.get(key)) {
                                throw new SomethingDidGoWrong("Incorrect body, got '" + data + "' expected '" + key + (assertionEntity.get(key) ? "' not" : "'"));
                            }
                        }
                    }
                }
                factory.newColumn(',');
                ctx.getChannel().close();
                factory.incrementOK();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        if (e.getCause() instanceof ReadTimeoutException) {
            factory.newColumn('T');
        } else if (e.getCause() instanceof ConnectException) {
            factory.newColumn('t');
        } else if (e.getCause() instanceof ClosedChannelException) {
            factory.newColumn('X'); // Server did close socket after accepting it
        } else if (e.getCause() instanceof IOException) {
            factory.newColumn('x'); // Server did close socket a while after accepting it
        } else if (e.getCause() instanceof SomethingDidGoWrong) {
            factory.newColumn('?');
            String out = "Something did go wrong:\n" + e.getCause().getMessage() + "\nRequest:\n" + request + "\nResponse:\n" + response;
            System.err.println("");
            System.err.println(out);
            System.err.println("");
            factory.setLastError(out);
        } else {
            factory.newColumn('!');
            System.err.println("");
            e.getCause().printStackTrace();
            System.err.println("");
            factory.setLastException(e.getCause());
        }
        ctx.getChannel().close();
    }
}
