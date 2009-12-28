package net.cscott.sdr.calls.grm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cscott.jutil.MultiMap;
import static net.cscott.sdr.util.Tools.mml; //listmultimap constructor

/** Abstract super-class for classes which emit some representation of a
 *  post-processed natural language call grammar. */
class AbstractEmit extends ToStringVisitor {
    // collect all rules with the same LHS
    static MultiMap<String,RuleAndAction> collectLHS(List<RuleAndAction> l) {
        MultiMap<String,RuleAndAction> mm = mml();
        for (RuleAndAction ra : l)
            mm.add(ra.rule.lhs, ra);
        return mm;
    }
    // sort a collection of strings
    static List<String> sorted(Collection<String> s) {
        List<String> l = new ArrayList<String>(s);
        Collections.sort(l);
        return l;
    }
    // substitute rules & classname into ths skeleton
    String subst(String skeleton, String rules, String className) {
        // snarf in the skeleton file.
        StringBuffer sb = new StringBuffer();
        String NL = System.getProperty("line.separator");
        try {
            InputStream is = getClass().getResourceAsStream(skeleton);
            assert is!=null : "can't find "+skeleton+" resource";
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while(true) {
                String line = br.readLine();
                if (line==null) break;
                sb.append(line);
                sb.append(NL);
            }
        } catch (IOException io) { /* hrm. */ assert false : io; }
        // do replace of @RULES@ and @CLASSNAME@
        Matcher m = pat.matcher(sb);
        sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).equals("RULES")?rules:className);
        }
        m.appendTail(sb);
        // done!
        return sb.toString();
    }
    private static final Pattern pat = Pattern.compile("@(RULES|CLASSNAME)@");
}
