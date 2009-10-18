// $Id: ExCLConstraintNotFound.java,v 1.1 2008/08/12 22:32:42 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ExCLConstraintNotFound
//

package EDU.Washington.grad.gjb.cassowary;

public class ExCLConstraintNotFound extends ExCLError {
    private static final long serialVersionUID = 1L;

    public String description() {
        return "(ExCLConstraintNotFound) Tried to remove a constraint never added to the tableu";
    }
}