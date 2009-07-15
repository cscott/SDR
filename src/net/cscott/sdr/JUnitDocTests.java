package net.cscott.sdr;

import org.junit.BeforeClass;
import org.junit.Test;


public class JUnitDocTests extends net.cscott.jdoctest.JDocJUnitTest {
    @BeforeClass
    public static void setup() {
        /* Make sure JDocJUnitTest looks in the right place for our tests */
        testDir = "api/tests";
    }
    // no op: should automatically run our doc tests
    @Test
    @Override
    public void runAllDoctests() {
        super.runAllDoctests();
    }
}
