package net.cscott.sdr.calls;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.jutil.UnmodifiableIterator;

import org.junit.runner.RunWith;

/** A {@link Permutation} represents a reordering of dancers. */
@RunWith(value=JDoctestRunner.class)
public class Permutation implements Comparable<Permutation> {
    /** Internal representation: if dancer #0 becomes dancer #3 in the
     *  new formation, then <code>p[0] == 3</code>. */
    private byte[] p;
    /** External representation is a string like "01234567". */
    public String toString() {
        char c[] = new char[this.p.length];
        for (int i=0; i<this.p.length; i++)
            c[i] = (char)(this.p[i] + ((this.p[i] < 10) ? '0' : ('A'-10)));
        return new String(c);
    }
    private Permutation(byte[] p) {
        this.p = p;
        assert isValid();
    }

    /** Return a permutation corresponding to the given string.
     * @doc.test
     *  js> Permutation.valueOf("01234567").equals(Permutation.IDENTITY8)
     *  true
     * @doc.test Test digits above 9
     *  js> Permutation.valueOf("0123456789ABC").toString()
     *  0123456789ABC
     */
    public static Permutation valueOf(String p) {
        p = p.toUpperCase();
        byte[] b = new byte[p.length()];
        for (int i=0; i<p.length(); i++) {
            char c = p.charAt(i);
            if (c>='0' && c<='9')
                b[i] = (byte) (c-'0');
            else
                b[i] = (byte) (10 + c - 'A');
        }
        return valueOf(b);
    }
    /**
     * Hash-cons to get a Permutation object.
     * @doc.test Object equality can be used to compare Permutations:
     *  js> Permutation.valueOf("0123") === Permutation.valueOf("0123")
     *  true
     *  js> Permutation.valueOf("0123") === Permutation.valueOf("3210")
     *  false
     *  js> Permutation.IDENTITY8.inverse() === Permutation.IDENTITY8
     *  true
     */
    public static Permutation valueOf(byte... b) {
        Permutation p = new Permutation(b);
        // hash-cons!
        WeakReference<Permutation> r = hashConsMap.get(p);
        if (r!=null) {
            Permutation pp = r.get();
            if (pp!=null)
                return pp;
        }
        hashConsMap.put(p, new WeakReference<Permutation>(p));
        return p;
    }
    private static WeakHashMap<Permutation,WeakReference<Permutation>> hashConsMap =
        new WeakHashMap<Permutation,WeakReference<Permutation>>();

    /** Invert a permutation.
     * @doc.test The identity transform is its own inverse.
     *  js> Permutation.IDENTITY8.inverse().equals(Permutation.IDENTITY8)
     *  true
     * @doc.test Composing a permutation and its inverse results in
     *  identity.
     *  js> p = Permutation.valueOf("13025746")
     *  13025746
     *  js> p.multiply(p.inverse())
     *  01234567
     *  js> p.inverse().multiply(p)
     *  01234567
     */
    public Permutation inverse() {
        byte[] b = new byte[this.p.length];
        for (int i=0; i<this.p.length; i++) {
            b[this.p[i]] = (byte) i;
        }
        return valueOf(b);
    }

    /** Compose a permutation.  We use Knuth's order of operations
     * (TAOCP 7.2.1.2) where alpha * beta means apply alpha first,
     * then apply beta to the result.
     * @doc.test Clarify the order of operations:
     *  js> a = Permutation.valueOf('250143')
     *  250143
     *  js> // b is the 'reflection' permutation
     *  js> b = Permutation.valueOf('543210')
     *  543210
     *  js> // a*b is "apply b after a"
     *  js> a.multiply(b)
     *  305412
     *  js> // b*a is 'a' reflected (apply b *to* a)
     *  js> b.multiply(a)
     *  341052
     * @doc.test Applying a permutation to IDENTITY results in itself.
     *  js> p = Permutation.valueOf("13025746");
     *  13025746
     *  js> Permutation.IDENTITY8.multiply(p)
     *  13025746
     * @doc.test Applying IDENTITY leaves a permutation unchanged.
     *  js> p = Permutation.valueOf("13025746");
     *  13025746
     *  js> p.multiply(Permutation.IDENTITY8)
     *  13025746
     */
    public Permutation multiply(Permutation other) {
        byte[] b = new byte[this.p.length];
        for (int i=0; i<this.p.length; i++) {
            b[i] = other.p[this.p[i]];
        }
        return valueOf(b);
    }
    public Permutation divide(Permutation p) {
        return this.multiply(p.inverse());
    }
    public boolean equals(Object o) {
        // even though we use hash consing, we need to implement equals
        // the "slow way", so that the hash cons map itself works
        // properly.
        if (!(o instanceof Permutation)) return false;
        return Arrays.equals(this.p, ((Permutation)o).p);
    }
    public int hashCode() {
        if (this.hash == 0)
            this.hash = Arrays.hashCode(this.p);
        return this.hash;
    }
    private transient int hash=0;

    /**
     * Compare two permutations lexicographically.
     * @doc.test Permutations of shorter sequences are smaller.
     *  js> importPackage(java.util);
     *  js> l=Arrays.asList(Permutation.valueOf("01"), Permutation.valueOf("0"), Permutation.valueOf("012"))
     *  [01, 0, 012]
     *  js> Collections.sort(l)
     *  js> l
     *  [0, 01, 012]
     * @doc.test Permutations of the same length are compared left to right:
     *  js> importPackage(java.util)
     *  js> l=Arrays.asList(Permutation.valueOf("012"), Permutation.valueOf("201"), Permutation.valueOf("120"))
     *  [012, 201, 120]
     *  js> Collections.sort(l)
     *  js> l
     *  [012, 120, 201]
     */
    public int compareTo(Permutation p) {
        if (this.p.length != p.p.length)
            return this.p.length - p.p.length;
        for (int i=0; i<this.p.length; i++)
            if (this.p[i] != p.p[i])
                return this.p[i] - p.p[i];
        return 0;
    }

    private boolean isValid() {
        boolean[] seen = new boolean[this.p.length];
        for (int i=0; i<this.p.length; i++) {
            if (seen[this.p[i]])
                return false;
            seen[this.p[i]] = true;
        }
        return true;
    }

    /** The identity permutation for 8 dancers.
     * @doc.test
     *  js> Permutation.IDENTITY8.toString()
     *  01234567
     *  js> Permutation.IDENTITY8.inverse().toString()
     *  01234567
     */
    public static Permutation IDENTITY8 = Permutation.valueOf("01234567");

    /* --- square dance-specific methods --- */
    public static Permutation fromFormation(FormationMatch fm) {
        return null; // XXX
    }
    /** Generate all symmetric permutations.
     * There are 96, if we rule out permutations where the heads and sides
     * can be swapped.  Dancer n's opposite is Dancer 4+n.
     * @doc.test Count the number of permutations
     *  js> p = Permutation.valueOf('01234567')
     *  01234567
     *  js> [pp for each (pp in Iterator(Permutation.generate(p)))].length
     *  96
     * @doc.test Ensure they are unique:
     *  js> o = {}
     *  [object Object]
     *  js> i=0
     *  0
     *  js> for each (pp in Iterator(Permutation.generate(Permutation.valueOf("01234567")))) {
     *    >   for each (rp in Iterator(pp.rotated())) {
     *    >     if (rp.toString() in o) throw new Error(i+" "+rp+" not unique!");
     *    >     o[rp.toString()] = rp;
     *    >   }
     *    >   i++;
     *    > }; i
     *  96
     * @doc.test Ensure that each permutation returned is canonical
     *  js> for each (pp in Iterator(Permutation.generate(Permutation.valueOf("01234567")))) {
     *    >   if (pp.canonical() !== pp)
     *    >     throw new Error("Not canonical! "+pp);
     *    > }; "OK";
     *  OK
     */
    public static Iterator<Permutation> generate(final Permutation first) {
        return new UnmodifiableIterator<Permutation>() {
            private Permutation next = first;
            private int i=0, j=0;
            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Permutation next() {
                if (!hasNext()) throw new NoSuchElementException();
                Permutation result = next;
                // advance
                int s = swapTable[j++];
                if (j>=swapTable.length) {
                    j=0;
                    i++;
                }
                // swap dancer 0 with dancer s
                byte[] b = next.p.clone();
                byte t = b[0];
                b[0] = b[s];
                b[s] = t;
                if (s!=7) {
                    s = (4+s) % 8;
                    t = b[4];
                    b[4] = b[s];
                    b[s] = t;
                }
                next = Permutation.valueOf(b);
                if (i==4)
                    next = null; // we're done!
                return result.canonical();
            }
        };
    }
    /** Adapted from Knuth, */
    private static byte[] swapTable = new byte[] {
            1,2,1,2,1,3,
            2,1,2,1,2,3,
            1,2,1,2,1,3,
            2,1,2,1,2,7
    };
    /** Generate the four rotated versions of the given permutation.
     * @doc.test
     *  js> [p for each (p in Iterator(Permutation.valueOf("01234567").rotated()))]
     *  01234567,23456701,45670123,67012345
     */
    public Iterator<Permutation> rotated() {
        final Permutation first = this;
        return new UnmodifiableIterator<Permutation>() {
            private Permutation next = first;
            @Override
            public boolean hasNext() {
                return next!=null;
            }
            @Override
            public Permutation next() {
                Permutation result = next;
                byte[] b = next.p.clone();
                for (int i=0; i<b.length; i++)
                    b[i] = (byte) ((b[i]+2) % 8);
                next = Permutation.valueOf(b);
                if (next.equals(first))
                    next = null;
                return result;
            }
        };
    }
    /**
     * The canonical form of a formation permutation is the smallest
     * lexicographically among the four rotational equivalents.
     * @doc.test
     *  js> Permutation.valueOf("23456701").canonical()
     *  01234567
     *  js> [p.canonical() for each (p in Iterator(Permutation.valueOf("23456701").rotated()))]
     *  01234567,01234567,01234567,01234567
     */
    public Permutation canonical() {
        // XXX: this could be more efficient, but I'm lazy.
        List<Permutation> r = new ArrayList<Permutation>(4);
        for (Iterator<Permutation> it = this.rotated(); it.hasNext(); )
            r.add(it.next());
        return Collections.min(r);
    }
}
