package net.cscott.sdr.calls.grm;

/**
 * Rule and Action is a simple mutable pair class to associate
 * a grammar rule with its appropriate (ANTLR) action.
 * @author C. Scott Ananian
 * @version $Id: RuleAndAction.java,v 1.1 2006-10-21 21:31:25 cananian Exp $
 */
public class RuleAndAction {
    public Rule rule; // allow mutation.
    public final String action;
    public RuleAndAction(Rule rule, String action) {
        this.rule=rule; this.action=action;
    }
    public String toString() { return rule.toString() + " { "+action+" }"; }
}
