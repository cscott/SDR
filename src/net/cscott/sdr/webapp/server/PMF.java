package net.cscott.sdr.webapp.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Share PersistenceManagerFactory and UserService instances. */
public final class PMF {
    public static final PersistenceManagerFactory pmf =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");
    public static final UserService userService =
        UserServiceFactory.getUserService();

    private PMF() {}
}