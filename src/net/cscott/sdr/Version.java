// -*-java-*-
package net.cscott.sdr;

/**
 * The <code>Version</code> object contains fields naming the current version
 * of the SDR application.
 * @version $Id: Version.java,v 1.1 2006-10-26 16:50:30 cananian Exp $
 */
public abstract class Version {
    /** The name of the package. */
    public static final String PACKAGE_NAME = "SDR";
    /** The version of the package. */
    public static final String PACKAGE_VERSION = "0.1";
    /** The package name and version as one string. */
    public static final String PACKAGE_STRING=PACKAGE_NAME+" "+PACKAGE_VERSION;
    /** The address to which bug reports should be sent. */
    public static final String PACKAGE_BUGREPORT = "sdr@cscott.net";

    /** Prints the package version if invoked. */
    public static void main(String[] args) {
	System.out.println(PACKAGE_STRING);
	System.out.println("Bug reports to "+PACKAGE_BUGREPORT);
    }
}
