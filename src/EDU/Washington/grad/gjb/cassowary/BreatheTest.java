package EDU.Washington.grad.gjb.cassowary;

import org.junit.Assert;
import org.junit.Test;

/** Simple feasibility test for SDR application. */
public class BreatheTest {
    @Test
    public void testDiamondBreathing() throws ExCLError {
        ClSimplexSolver solver = new ClSimplexSolver();
        ClVariable a = new ClVariable(0);
        ClVariable b = new ClVariable(0);
        ClVariable c = new ClVariable(0);
        ClVariable d = new ClVariable(0);
        ClVariable e = new ClVariable(0);
        ClVariable f = new ClVariable(0);
        ClVariable g = new ClVariable(0);
        // objective: minimize a-g
        solver.addConstraint(new ClLinearEquation(a, 0, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(b, 0, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(c, 0, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(d, 0, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(e, 0, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(f, 0, ClStrength.weak));
        solver.addConstraint(new ClLinearEquation(g, 0, ClStrength.weak));
        // basic monotonicity constraints (required)
        solver.addConstraint(new ClLinearEquation(a, 0));
        solver.addConstraint(new ClLinearInequality(b, CL.Op.GEQ, a));
        solver.addConstraint(new ClLinearInequality(c, CL.Op.GEQ, b));
        solver.addConstraint(new ClLinearInequality(d, CL.Op.GEQ, c));
        solver.addConstraint(new ClLinearInequality(e, CL.Op.GEQ, d));
        solver.addConstraint(new ClLinearInequality(f, CL.Op.GEQ, e));
        solver.addConstraint(new ClLinearInequality(g, CL.Op.GEQ, f));
        // make sure each element has enough space (required)
        solver.addConstraint(new ClLinearInequality(CL.Plus(a, 2), CL.Op.LEQ, d));
        solver.addConstraint(new ClLinearInequality(CL.Plus(d, 2), CL.Op.LEQ, g));
        solver.addConstraint(new ClLinearInequality(CL.Plus(b, 2), CL.Op.LEQ, e));
        solver.addConstraint(new ClLinearInequality(CL.Plus(c, 2), CL.Op.LEQ, f));
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
        System.out.println("a="+a.value());
        System.out.println("b="+b.value());
        System.out.println("c="+c.value());
        System.out.println("d="+d.value());
        System.out.println("e="+e.value());
        System.out.println("f="+f.value());
        System.out.println("g="+g.value());
        // automate the check
        double EPSILON = 1e-5;
        Assert.assertEquals("a", 0., a.value(), EPSILON);
        Assert.assertEquals("b", 2/3., b.value(), EPSILON);
        Assert.assertEquals("c", 4/3., c.value(), EPSILON);
        Assert.assertEquals("d", 2., d.value(), EPSILON);
        Assert.assertEquals("e", 8/3., e.value(), EPSILON);
        Assert.assertEquals("f", 10/3., f.value(), EPSILON);
        Assert.assertEquals("g", 4., g.value(), EPSILON);
    }
}
