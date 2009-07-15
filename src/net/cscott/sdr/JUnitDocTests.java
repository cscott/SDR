package net.cscott.sdr;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(value = Parameterized.class)
public class JUnitDocTests extends net.cscott.jdoctest.JDocJUnitTest {
    public JUnitDocTests(String testFile) { super(testFile); assertTrue(testFile!=null); }

    @Parameters
    public static Collection<Object[]> listTests() {
        /* give the correct directory for our tests */
        return net.cscott.jdoctest.JDocJUnitTest.listTests("api/tests");
    }
}
