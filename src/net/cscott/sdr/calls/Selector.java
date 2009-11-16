package net.cscott.sdr.calls;

import java.util.Set;

/** Mechanism to select certain dancers from a Formation. */
// it would seem like it would be nice to make ths an ExprFunc<Set<Dancer>>,
// but then our class-based dispatch method goes awry because of type erasure.
// we would then have to reify the Set<Dancer> type -- better to make a
// Selector object.
public abstract class Selector {
    /** Return the dancers selected by this {@link Selector} from the given
     *  formation. */
    public abstract Set<Dancer> select(TaggedFormation tf);
}
