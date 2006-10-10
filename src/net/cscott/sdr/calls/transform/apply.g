// Apply a call to a formation to get a set of dancer paths.
header {
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.util.*;
import java.util.*;
}
// @@startwalker
/** CallApply takes a Def tree and a Formation and creates
 * DancerActions. */
class FindInWalker extends Walker;
// @@startrules
res
	: #(IN pieces) { System.out.println("IN!"); }
	;
// @@endrules
// @@endwalker
