package net.cscott.sdr.calls.grm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.util.Tools;

/** Emit Java representation of post-processed natural language grammar
 *  for calls. */
public class EmitJava extends AbstractEmit {
    public static String emit(String parserName, List<RuleAndAction> l) {
        EmitJava ej = new EmitJava();
        // collect all the rules with the same LHS
        MultiMap<String,RuleAndAction> mm = collectLHS(l);
        // join all the alternatives
        Map<String,Grm> m = new LinkedHashMap<String,Grm>();
        for (String lhs : sorted(mm.keySet())) {
            List<Grm> alts = new ArrayList<Grm>();
            for (RuleAndAction ra : mm.getValues(lhs))
                alts.add(ra.rule.rhs);
            m.put(lhs, new Grm.Alt(alts));
        }
        // add rules for <anyone>, etc.
        m.put("parenthesized_anything", new Grm.Concat
                (Tools.<Grm>l(new Grm.Terminal("("),
                              new Grm.Nonterminal("anything", -1),
                              new Grm.Terminal(")"))));
        m.put("people", Grm.parse("<genders> | <all>"));
        m.put("genders", Grm.parse("<boys> | <girls>"));
        m.put("boys", Grm.parse("boys | men"));
        m.put("girls", Grm.parse("girls | ladies"));
        m.put("all", Grm.parse("all | every (one|body) | everyone"));
        m.put("wave_select", Grm.parse("centers | ends"));
        m.put("anyone", Grm.parse("<people> | <wave_select>"));
        // emit all the grammars
        String NL = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,Grm> me : m.entrySet()) {
            sb.append("        m.put(");
            sb.append(Grm.str_escape(me.getKey()));
            sb.append(",");
            me.getValue().repr(sb);
            sb.append(");");
            sb.append(NL);
        }
        // substitute the rules & the classname into the skeleton
        String result = ej.subst("java.skel", sb.toString(), parserName);
        // done.
        return result;
    }
}
