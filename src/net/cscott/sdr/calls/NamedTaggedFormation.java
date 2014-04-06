package net.cscott.sdr.calls;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.jutil.MultiMap;
import net.cscott.sdr.util.SdrToString;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.runner.RunWith;

/**
 * Associates a name with a canonical
 * formation.  This makes string representations more compact!
 * @doc.test Fetch tagged dancers from a TaggedFormation:
 *  js> f = FormationList.STATIC_SQUARE ; f.getClass()
 *  class net.cscott.sdr.calls.NamedTaggedFormation
 *  js> f.getName()
 *  STATIC SQUARE
 */
@RunWith(value=JDoctestRunner.class)
public class NamedTaggedFormation extends TaggedFormation {
    private final String name;
    /** Permutation of standard dancers needed to make normal couples in
     * this formation. */
    private final StandardDancer[] normalCouples;
    public NamedTaggedFormation(String name, StandardDancer[] normalCouples,
                                TaggedDancerInfo... tdi) {
	super(tdi);
	this.name = name;
	this.normalCouples = clone(normalCouples);
    }
    public NamedTaggedFormation(String name, Formation f,
				MultiMap<Dancer,Tag> tags,
				StandardDancer... normalCouples) {
	super(f, tags);
	this.name = name;
	this.normalCouples = clone(normalCouples);
    }
    public NamedTaggedFormation(String name, TaggedFormation tf,
                                StandardDancer... normalCouples) {
        super(tf, new AbstractMap<Dancer,Dancer>() {
            // HACK! simple identity map.
            @Override
            public Dancer get(Object d) { return (Dancer) d; }
            @Override
            public Set<Map.Entry<Dancer, Dancer>> entrySet() {
                return Collections.emptySet();
            }});
        this.name = name;
        this.normalCouples = clone(normalCouples);
    }
    public String getName() { return this.name; }
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, SdrToString.STYLE)
	    .append("name", getName());
        if (this.normalCouples != null)
            tsb = tsb.append("normalCouples", this.normalCouples);
        return tsb
	    .appendSuper(super.toString())
	    .toString();
    }
    private StandardDancer[] clone(StandardDancer[] normalCouples) {
        if (normalCouples == null || normalCouples.length == 0)
            return null; // save some memory
        return normalCouples.clone();
    }
    StandardDancer[] normalCouples() { return clone(this.normalCouples); }
    /**
     * {@link NamedTaggedFormation} overrides {@link Formation#mapStd} in
     * order to ensure normal couples for specific named formations.
     * @doc.test
      *  js> FormationList.STATIC_SQUARE.mapStd([]).toStringDiagram('|');
      *  |     3Gv  3Bv
      *  |
      *  |4B>            2G<
      *  |
      *  |4G>            2B<
      *  |
      *  |     1B^  1G^
      *  js> FormationList.TRADE_BY.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^
      *  |
      *  |2Gv  2Bv
      *  |
      *  |4B^  4G^
      *  |
      *  |3Gv  3Bv
      *  js> FormationList.EIGHT_CHAIN_THRU.mapStd([]).toStringDiagram('|');
      *  |1Gv  1Bv
      *  |
      *  |2B^  2G^
      *  |
      *  |4Gv  4Bv
      *  |
      *  |3B^  3G^
      */
    @Override
    public TaggedFormation mapStd(StandardDancer... dancers) {
        if (dancers.length == 0 && this.normalCouples != null) {
            dancers = this.normalCouples;
        }
        return super.mapStd(dancers);
    }
}
