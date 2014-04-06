// PMSD startup script.
//
// This defines the common startup environment for PMSD.
// Inside this function, the 'state' variable contains the PMSD "state"
// object; this will be the local scope once PMSD starts its REPL loop.

importPackage(net.cscott.sdr.calls);
state.props = { }
// set call-pending so that circle left & etc always stop promptly.
state.props["call-pending"] = true;

// utility functions
function print() {
    writer.print(Array.join(arguments, " "));
}
function println() {
    writer.println(Array.join(arguments, " "));
}
/** Annotate a function with documentation. */
function _doc(doc, f) {
    f.doc = doc;
    return f;
}

// interactive support

function welcome() {
    let Version = net.cscott.sdr.Version;
    println("Welcome to "+Version.PACKAGE_STRING);
    println("Type /help or /license for more information; /exit to quit.");
}

function help() {
    println("Type square dance calls at the sdr> prompt, for example 'heads square thru'.");
    println("You can access a javascript interpreter by prefixing your statement with /,");
    println("for example '/1+2'.");
    println("Defined commands:");
    function describeFunc(name, f, extra) {
        if (f.hide) return;
        print(" /"+name);
        if (extra) print(extra);
        if (f.doc) print(" - "+f.doc);
        println();
    }
    // native commands
    let nativeCmds = {
            reset: ['Restore initial dancer formation and dance program'],
            ds: ['Get current DanceState object'],
            program: ['Get/set the current dance program'],
            runTest: ['Run the specified test case', '(testName)'],
            exit: ['Exit the program'],
    };
    for (let fld in nativeCmds) {
        describeFunc(fld, _doc(nativeCmds[fld][0], function(){}),
                     nativeCmds[fld][1]);
    }
    // javascript commands
    for (let fld in state) {
        let g = state.__lookupGetter__(fld);
        let s = state.__lookupSetter__(fld);
        if (g) {
            describeFunc(fld, g);
        } else if (s) {
            describeFunc(fld, s, "=...");
        } else if (typeof(state[fld]) == "function") {
            let argdoc = ('argdoc' in state[fld]) ? state[fld].argdoc : '...';
            describeFunc(fld, state[fld], "("+argdoc+")");
        }
    }
}
help.doc = "Show this message";

function _printResource(resourceName) {
    let pmsd = java.lang.Class.forName("net.cscott.sdr.PMSD");
    let r = new java.io.BufferedReader(
            new java.io.InputStreamReader(
                    pmsd.getResourceAsStream(resourceName),
                    "UTF-8"));
    for (let line=r.readLine(); line!=null; line=r.readLine())
        println(line);
}

function authors() { _printResource("AUTHORS"); }
authors.doc = "Show the list of authors of this software";

function license() { _printResource("COPYING"); }
license.doc = "Show license and warranty information";

// Import the program values
let _programs = net.cscott.sdr.calls.Program.values();
for (let i=0; i < _programs.length; i++) {
    this[_programs[i].name()] = _programs[i];
}

// Customize the state object
// note that we abuse getters to eliminate parentheses from no-argument commands

state.errorDetails = false;
state.formationDetails = false;

state.__defineGetter__("help", help);
state.__defineGetter__("authors", authors);
state.__defineGetter__("license", license);

/** Called after every call is evaluated: prints the resulting formation. */
state.__defineGetter__("printFormation",
                       _doc("Prints the current dancer formation", function() {
    let f = this.ds.currentFormation();
    if (this.formationDetails)
        return f.toStringDiagramWithDetails("| ");
    return f.toStringDiagram("| ");
}));

/** Called to interpret a BadCallException. */
state.formatBCE = function(bce) {
    if (this.errorDetails) {
        // XXX print out more information about error
    }
    return bce.getMessage();
};

/** Prints out one step of a resolve from here.  Currently uses Dave Wilson's
 *  ocean wave resolution method.
 *  @see net.cscott.sdr.toolbox.DWResolver
 */
state.__defineGetter__("resolveStep",
                       _doc("Prints the next step of a resolve from the current formation", function() {
    let f = this.ds.currentFormation();
    return net.cscott.sdr.toolbox.DWResolver.resolveStep(f);
}));

/** Helper to write parsing tests.
 *  @see net.cscott.sdr.calls.CallDB#parse(Program,String)
 */
state.parse = function(calltext) {
    let a = CallDB.INSTANCE.parse(this.ds.dance.getProgram(), calltext);
    // simplify expression for easy reading
    return a.call.simplify().toShortString();
};
state.parse.argdoc = "'<call>'";
state.parse.doc = "Show how a square dance call is parsed";

/** Reload call definitions from resource files. */
state.__defineGetter__("reload", _doc("Reload call definitions", function() {
    CallDB.INSTANCE.reload();
    return undefined;
}));

/** Runs each test in its own javascript context, to ensure
 *  independence between tests.
 */
state.runAllTests = function() {
    return net.cscott.sdr.PMSD.runAllTests();
};
state.runAllTests.argdoc = "";
state.runAllTests.doc = "Run all PMSD unit tests.";
