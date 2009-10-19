package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

import org.junit.Assert;
import org.junit.Test;

/** Simple feasibility test for SDR application. */
public class BreatheTest {
    @Test
    public void testDiamondBreathing() throws ExCLError {
        ClSimplexSolver solver = new ClSimplexSolver();
        ClVariable a = new ClVariable(Fraction.ZERO);
        ClVariable b = new ClVariable(Fraction.ZERO);
        ClVariable c = new ClVariable(Fraction.ZERO);
        ClVariable d = new ClVariable(Fraction.ZERO);
        ClVariable e = new ClVariable(Fraction.ZERO);
        ClVariable f = new ClVariable(Fraction.ZERO);
        ClVariable g = new ClVariable(Fraction.ZERO);
        // objective: minimize a-g
        solver.addConstraint(new ClLinearEquation(a, Fraction.ZERO, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(b, Fraction.ZERO, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(c, Fraction.ZERO, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(d, Fraction.ZERO, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(e, Fraction.ZERO, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(f, Fraction.ZERO, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(g, Fraction.ZERO, ClStrength.weak));
        // basic monotonicity constraints (required)
        solver.addConstraint(new ClLinearEquation(a, Fraction.ZERO));
        solver.addConstraint(new ClLinearInequality(b, CL.Op.GEQ, a));
        solver.addConstraint(new ClLinearInequality(c, CL.Op.GEQ, b));
        solver.addConstraint(new ClLinearInequality(d, CL.Op.GEQ, c));
        solver.addConstraint(new ClLinearInequality(e, CL.Op.GEQ, d));
        solver.addConstraint(new ClLinearInequality(f, CL.Op.GEQ, e));
        solver.addConstraint(new ClLinearInequality(g, CL.Op.GEQ, f));
        // make sure each element has enough space (required)
        solver.addConstraint(new ClLinearInequality(CL.Plus(a, Fraction.TWO), CL.Op.LEQ, d));
        solver.addConstraint(new ClLinearInequality(CL.Plus(d, Fraction.TWO), CL.Op.LEQ, g));
        solver.addConstraint(new ClLinearInequality(CL.Plus(b, Fraction.TWO), CL.Op.LEQ, e));
        solver.addConstraint(new ClLinearInequality(CL.Plus(c, Fraction.TWO), CL.Op.LEQ, f));
        // attempt to maintain symmetry (strong)
        solver.addConstraint(new ClLinearEquation(CL.Minus(b, a), CL.Minus(d, c),
                ClStrength.strong));
        solver.addConstraint(new ClLinearEquation(CL.Minus(e, d), CL.Minus(g, f),
                ClStrength.strong));
        solver.addConstraint(new ClLinearEquation(CL.Minus(c, b), CL.Minus(e, d),
                ClStrength.strong));
        solver.addConstraint(new ClLinearEquation(CL.Minus(d, c), CL.Minus(f, e),
                ClStrength.strong));
        // okay, look at solution
        System.out.println("a="+a.value().toProperString());
        System.out.println("b="+b.value().toProperString());
        System.out.println("c="+c.value().toProperString());
        System.out.println("d="+d.value().toProperString());
        System.out.println("e="+e.value().toProperString());
        System.out.println("f="+f.value().toProperString());
        System.out.println("g="+g.value().toProperString());
        // automate the check
        Assert.assertEquals("a", Fraction.ZERO, a.value());
        Assert.assertEquals("b", Fraction.valueOf(2, 3), b.value());
        Assert.assertEquals("c", Fraction.valueOf(4, 3), c.value());
        Assert.assertEquals("d", Fraction.TWO, d.value());
        Assert.assertEquals("e", Fraction.valueOf(8, 3), e.value());
        Assert.assertEquals("f", Fraction.valueOf(10, 3), f.value());
        Assert.assertEquals("g", Fraction.valueOf(4), g.value());
    }
}
