package net.cscott.sdr.calls.grm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;

/** Emit (trimmed) pronunciation dictionary for spoken natural-language
 *  call grammar.
 */
public class EmitDictionary extends AbstractEmit {
    public final static EmitDictionary INSTANCE = new EmitDictionary();
    Set<String> words = new HashSet<String>();
    {   // extra words, from the stock grammar skeleton
        // (see resources/net/cscott/sdr/calls/grm/jsapi.skel)
        words.addAll(Arrays.asList(
                "heads","sides","head","side","center","end","very","centers",
                "boys","men","girls","ladies","all","every","one","body","ends",
                "one","two","three","four","five","six","seven","eight","nine",
                "a","half","third","quarter","thirds","quarters","once","and",
                "twice","times",
		// from the "menu" command grammar
		"square","up","quit","exit"
                ));
    }
    public void collect(Program program, List<RuleAndAction> l) {
        for (RuleAndAction ra : l)
            ra.rule.rhs.accept(tokenVisitor);
    }
    final GrmVisitor<Void> tokenVisitor = new GrmVisitor<Void>() {
        @Override
        public Void visit(Alt alt) {
            for (Grm g : alt.alternates)
                g.accept(this);
            return null;
        }
        @Override
        public Void visit(Concat concat) {
            for (Grm g : concat.sequence)
                g.accept(this);
            return null;
        }
        @Override
        public Void visit(Mult mult) {
            return mult.operand.accept(this);
        }
        @Override
        public Void visit(Nonterminal nonterm) {
            return null;
        }
        @Override
        public Void visit(Terminal term) {
            words.add(term.literal.toLowerCase());
            return null;
        }
    };
    public void readPronunciationDictionary(String resourceName,
                                            List<String> entries) {
        try {
            InputStream is = getClass().getResourceAsStream(resourceName);
            assert is!=null : "can't find "+resourceName+" resource";
            BufferedReader br =
                new BufferedReader(new InputStreamReader(is, "UTF-8"));
            for (String line=br.readLine(); line!=null; line=br.readLine()) {
                Matcher m = dictEntryPat.matcher(line);
                if (m.find()) {
                    String entry = m.group(1).toLowerCase();
                    if (words.contains(entry))
                        entries.add(line);
                }
            }
        } catch (IOException e) {
            // shouldn't happen, but just ignore this resource if it does.
            assert false : e;
        }
    }
    private static final Pattern dictEntryPat =
        Pattern.compile("^(\\S+?)([(]\\d+[)])?\\s");
    public String emit() {
        // read both CMU pronunciation dictionary and some extra entries we've
        // compiled for square-dance-specific terms.
        // (make sure cmudict and extradict are on the classpath)
        List<String> entries = new ArrayList<String>(words.size());
        for (String resourceName : Arrays.asList("/cmudict.0.6d", "/extradict"))
            readPronunciationDictionary(resourceName, entries);
        // ok, now emit all the entries
        StringBuilder sb = new StringBuilder();
        String NL = System.getProperty("line.separator");
        for (String line : sorted(entries)) {
            sb.append(line);
            sb.append(NL);
        }
        return sb.toString();
    }
}
