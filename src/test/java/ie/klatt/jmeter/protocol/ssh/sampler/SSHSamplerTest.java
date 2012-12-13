package ie.klatt.jmeter.protocol.ssh.sampler;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SSHSamplerTest {
    @Ignore
    @Test
    public void badHost() {
        Sampler sampler = new SSHSampler();
        sampler.setProperty("SSHSampler.timeout", "0");
        sampler.setProperty("SSHSampler.host", "badhost");
        sampler.setProperty("SSHSampler.username", "root");
        sampler.setProperty("SSHSampler.password", "nevermind");
        SampleResult result = sampler.sample(null);
        assertEquals("java.net.UnknownHostException: badhost", result.getResponseMessage().split("\n", 2)[0]);
        assertFalse(result.isSuccessful());
    }

    @Ignore
    @Test
    public void authFail() {
        Sampler sampler = new SSHSampler();
        sampler.setProperty("SSHSampler.timeout", "0");
        sampler.setProperty("SSHSampler.host", "localhost");
        sampler.setProperty("SSHSampler.username", "baduser");
        sampler.setProperty("SSHSampler.password", "nevermind");
        SampleResult result = sampler.sample(null);
        assertEquals("Auth fail", result.getResponseMessage().split("\n", 2)[0]);
        assertFalse(result.isSuccessful());
    }
}
