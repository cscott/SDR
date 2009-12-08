package net.cscott.sdr.webapp.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.grm.CompletionEngine;

import com.google.gwt.user.client.ui.SuggestOracle;

public class CallOracle extends SuggestOracle {
    private Program program = Program.PLUS;
    public void setProgram(Program p) {
        this.program = p;
    }

    @Override
    public void requestSuggestions(Request request, Callback callback) {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        String input = request.getQuery();
        Iterator<String> it = CompletionEngine.complete(program, input);
        for (int i=0; i<request.getLimit() && it.hasNext(); i++) {
            String option = it.next();
            String replace = option.replaceFirst("<.*", "");
            suggestions.add(new S(option, replace));
        }
        Response response = new Response();
        response.setSuggestions(suggestions);
        callback.onSuggestionsReady(request, response);
    }
    static class S implements Suggestion {
        final String display, replacement;
        S(String display, String replacement) {
            this.display=display;
            this.replacement=replacement;
        }
        public String getDisplayString() { return display; }
        public String getReplacementString() { return replacement; }
    }
}
