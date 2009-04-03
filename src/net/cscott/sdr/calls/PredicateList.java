package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.ast.Condition;
import net.cscott.sdr.calls.ast.ParCall;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Tools;
import static net.cscott.sdr.util.Tools.mms;

/** This class contains all the predicates known to the system. */
public abstract class PredicateList {
    
    // zero-arg operators
    public final static Predicate TRUE = new _Predicate("true") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.isEmpty();
            return true;
        }        
    };
    public final static Predicate FALSE = new _Predicate("false") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.isEmpty();
            return false;
        }
    };
    // one-arg operators
    public final static Predicate NOT = new _Predicate("not") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.size()==1;
            Condition arg = c.getArg(0);
            return !arg.getPredicate().evaluate(ds,f,arg);
        }
    };
    // binary numerical operators
    public final static Predicate EQUAL = new _Predicate("equal") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.size()==2;
            Fraction f1 = Fraction.valueOf(c.getArg(0).predicate);
            Fraction f2 = Fraction.valueOf(c.getArg(1).predicate);
            return f1.equals(f2);
        }
    };
    public final static Predicate GREATER = new _Predicate("greater") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.size()==2;
            Fraction f1 = Fraction.valueOf(c.getArg(0).predicate);
            Fraction f2 = Fraction.valueOf(c.getArg(1).predicate);
            return f1.compareTo(f2) > 0;
        }
    };
    // n-ary operators.
    public final static Predicate AND = new _Predicate("and") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.size()>0;
            boolean result = true;
            for (Condition cc : c.args) {
                result = cc.getPredicate().evaluate(ds, f, cc);
                if (!result) break; // short-circuit operator.
            }
            return result;
        }
    };
    public final static Predicate OR = new _Predicate("or") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.size()>0;
            boolean result = false;
            for (Condition cc : c.args) {
                result = cc.getPredicate().evaluate(ds, f, cc);
                if (result) break; // short-circuit operator.
            }
            return result;
        }
    };
    // okay, square-dance-specific operators.
    public final static Predicate PROGRAM_AT_LEAST = new _Predicate("program at least") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            assert c.args.size()==1;
            Program p = Program.valueOf(c.getStringArg(0).toUpperCase());
            return ds.getProgram().includes(p);
        }
    };
    public final static Predicate SELECTED_ARE = new _Predicate("selected are") {
        @Override
        public boolean evaluate(DanceProgram ds, Formation f, Condition c) {
            List<String> args = new ArrayList<String>(c.args.size());
            for (int i=0; i<c.args.size(); i++)
                args.add(c.getStringArg(i));
            Set<Tag> tags = ParCall.parseTags(args);
            // each selected dancer must have all of these tags
            TaggedFormation tf = TaggedFormation.coerce(f);
            for (Dancer d: f.selectedDancers())
                for (Tag t : tags)
                    if (!tf.isTagged(d, t))
                        return false;
            return true;
        }
    };

    // helper class ////////////////////////////////////
    private static abstract class _Predicate extends Predicate {
        private final String name;
        _Predicate(String name) { this.name = name; }
        public String getName() { return name; }
    }
}
