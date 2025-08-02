package com.company.compiler.lexer;
import com.company.exceptions.UnknownSymbolException;
import com.company.model.Pair;

import java.util.*;

public class Lexer implements LexerInterface {
    private Collection<? extends AutomatonInterface> automata;

    public Lexer(Collection<? extends TokenInterface> tokens) {
        this.automata = tokens
                .stream()
                .map(Automaton::new)
                .toList();
    }

    public List<MatchedToken> analyze(String prog) throws UnknownSymbolException {
        List<MatchedToken> tokenList = new ArrayList<>();
        var currentLine = 1;
        var program = prog + '\n';
        
        for (int i = 0 ; i < program.length() ; i++) {
            boolean anAutomatonMatched = false;
            AutomatonInterface maxAutomaton = null;

            for (AutomatonInterface automaton : automata) {
                Pair<AutomatonState, Integer> result = automaton.feed(prog.charAt(i));

                var matchStatus = result.first;

                if (matchStatus == AutomatonState.MATCHED || matchStatus == AutomatonState.PARTIAL_MATCH) {
                    anAutomatonMatched = true;
                }

                if (matchStatus == AutomatonState.MATCHED || matchStatus == AutomatonState.MATCHED_BEFORE) {
                    maxAutomaton = maxAutomaton == null ? automaton : maxAutomaton;

                    maxAutomaton = automaton.getLengthOfRecognizedWord() > maxAutomaton.getLengthOfRecognizedWord()
                            ? automaton : maxAutomaton;
                }
            }

            if (!anAutomatonMatched) {
                if (maxAutomaton == null) {
                    throw new UnknownSymbolException(this.getAnyAutomata().getWord(), currentLine);
                }
                if (!maxAutomaton.getToken().isBlank()) {
                    tokenList.add(new MatchedToken(maxAutomaton.getToken(), maxAutomaton.getMatchedWord(), currentLine));
                } else if(maxAutomaton.getToken().isNewLine()) {
                    currentLine += 1;
                }
                i -= maxAutomaton.getMatchedSignsBefore();
                automata.forEach(AutomatonInterface::reset);
            }
        }
        return tokenList;
    }
    
    private AutomatonInterface getAnyAutomata() {
        return automata.iterator().next();
    }
}
