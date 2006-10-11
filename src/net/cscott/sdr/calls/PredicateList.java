package net.cscott.sdr.calls;

import net.cscott.sdr.calls.ast.Condition;

/** This class contains all the predicates known to the system. */
public abstract class PredicateList {
    
    // zero-arg operators
    public final static Predicate TRUE = new _Predicate("true") {
        @Override
        public boolean evaluate(DanceState ds, Formation f, Condition c) {
            assert c.getNumberOfChildren()==0;
            return true;
        }        
    };
    public final static Predicate FALSE = new _Predicate("false") {
        @Override
        public boolean evaluate(DanceState ds, Formation f, Condition c) {
            assert c.getNumberOfChildren()==0;
            return false;
        }
    };
    // one-arg operators
    public final static Predicate NOT = new _Predicate("not") {
        @Override
        public boolean evaluate(DanceState ds, Formation f, Condition c) {
            assert c.getNumberOfChildren()==1;
            Condition arg = c.getArg(0);
            return !arg.getPredicate().evaluate(ds,f,arg);
        }
    };
    // n-ary operators.
    public final static Predicate AND = new _Predicate("and") {
        @Override
        public boolean evaluate(DanceState ds, Formation f, Condition c) {
            assert c.getNumberOfChildren()>0;
            boolean result = true;
            for (int i=0; i<c.getNumberOfChildren(); i++) {
                Condition cc = c.getArg(i);
                result = cc.getPredicate().evaluate(ds, f, cc);
                if (!result) break; // short-circuit operator.
            }
            return result;
        }
    };
    public final static Predicate OR = new _Predicate("or") {
        @Override
        public boolean evaluate(DanceState ds, Formation f, Condition c) {
            assert c.getNumberOfChildren()>0;
            boolean result = false;
            for (int i=0; i<c.getNumberOfChildren(); i++) {
                Condition cc = c.getArg(i);
                result = cc.getPredicate().evaluate(ds, f, cc);
                if (result) break; // short-circuit operator.
            }
            return result;
        }
    };
    // okay, square-dance-specific operators.
    public final static Predicate PROGRAM_AT_LEAST = new _Predicate("program at least") {
        @Override
        public boolean evaluate(DanceState ds, Formation f, Condition c) {
            assert c.getNumberOfChildren()==1;
            Program p = Program.valueOf(c.getStringArg(0).toUpperCase());
            return ds.program.includes(p);
        }
    };

    // helper class ////////////////////////////////////
    private static abstract class _Predicate extends Predicate {
        private final String name;
        _Predicate(String name) { this.name = name; }
        public String getName() { return name; }
    }
}
