package net.cscott.sdr.webapp.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NotLoggedInException extends Exception implements Serializable {
    public String loginUrl;

    public NotLoggedInException(String loginUrl) {
        super("Not logged in!");
        this.loginUrl = loginUrl;
    }
    // no-arg constructor for GWT serializability
    NotLoggedInException() { this(null); }
}
