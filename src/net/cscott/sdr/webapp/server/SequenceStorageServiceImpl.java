package net.cscott.sdr.webapp.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.cscott.sdr.webapp.client.NotLoggedInException;
import net.cscott.sdr.webapp.client.Sequence;
import net.cscott.sdr.webapp.client.SequenceInfo;
import net.cscott.sdr.webapp.client.SequenceStorageService;

import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class SequenceStorageServiceImpl extends RemoteServiceServlet
    implements SequenceStorageService {

    public List<SequenceInfo> list(Date earlierThan, int maxItems,
                                   List<String> requiredTags)
        throws NotLoggedInException {
        User user = checkLogin();
        PersistenceManager pm = PMF.pmf.getPersistenceManager();
        Query query = pm.newQuery(SequenceInfoJDO.class);
        String filter = "user == userParam";
        if (earlierThan != null)
            filter += " && lastModified < dateParam";
        else
            earlierThan = new Date(); // just to avoid passing a null param
        for (String tag : requiredTags) // note slacker-style tag escaping
            filter += " && tags == '"+tag.replace('\'', '"')+"'";
        query.setFilter(filter);
        query.setOrdering("lastModified desc");
        query.declareParameters("User userParam, Date dateParam");
        query.setRange(0, maxItems); // max 15 entries

        try {
            @SuppressWarnings("unchecked")
            List<SequenceInfoJDO> dbResults = (List<SequenceInfoJDO>)
                query.execute(user, earlierThan);
            List<SequenceInfo> results =
                new ArrayList<SequenceInfo>(dbResults.size());
            for (SequenceInfoJDO s : dbResults) {
                s.info.id = s.id; // transfer id to non-persistent property
                results.add(s.info);
            }
            return results;
        } finally {
            query.closeAll();
        }
    }

    public Sequence load(Long id) throws NotLoggedInException {
        User user = checkLogin();
        // TODO Auto-generated method stub
        return null;
    }

    public Long save(SequenceInfo info, Sequence sequence)
            throws NotLoggedInException {
        User user = checkLogin();
        PersistenceManager pm = PMF.pmf.getPersistenceManager();
        SequenceInfoJDO sjdo;
        try {
            if (info.id==null) {
                // create new persistent entity
                sjdo = new SequenceInfoJDO(user, info);
            } else {
                // update existing entity
                sjdo = pm.getObjectById(SequenceInfoJDO.class, info.id);
                sjdo.update(info);
            }
            pm.makePersistent(sjdo);
        } finally {
            pm.close();
        }
        // XXX: we're only saving the sequenceinfo!
        return sjdo.id;
    }

    private User checkLogin() throws NotLoggedInException {
        User user = PMF.userService.getCurrentUser();
        if (user==null)
            throw new NotLoggedInException
                (PMF.userService.createLoginURL("/closeme.html"));
        return user;
    }
}
