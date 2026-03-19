package com.company.compiler.lexer.infrastructure;

import com.company.compiler.common.exceptions.DevException;
import com.company.compiler.common.token.IgnoredToken;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.common.token.Token;
import com.company.compiler.lexer.exceptions.TokenNotRecognizedException;
import com.company.compiler.lexer.model.MatchedResult;
import com.company.compiler.lexer.model.TokenMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LongestTokenLexer implements Lexer {

    private boolean anyMatch(String word, List<TokenMatcher> tokenMatchers) {
        return tokenMatchers.stream()
                .map(tokenMatcher -> tokenMatcher.matches(word))
                .anyMatch(MatchedResult::stillMatching);
    }

    private List<MatchedResult> getHighest(List<MatchedResult> matches) {
        if (matches.isEmpty()) {
            return new ArrayList<>();
        }
        var max = matches.getFirst();
        var result = new ArrayList<MatchedResult>();

        for (var match : matches) {
            var compareResult = match.compareTo(max);
            if (compareResult < 0) {
                continue;
            }
            if (compareResult > 0) {
                max = match;
                result = new ArrayList<>();
            }
            result.add(match);
        }

        return result;
    }

    private MatchedResult getMaxTokenMatched(String word, List<TokenMatcher> tokenMatchers) {
        var matches = tokenMatchers.stream()
                .map(tokenMatcher -> tokenMatcher.matches(word))
                .filter(MatchedResult::hasMatched)
                .toList();

        if (matches.isEmpty()) {
            return null;
        }

        var longestMatches = getHighest(matches);

        if (longestMatches.size() > 1) {
            throw new DevException("Multiple tokens with same matched length matched");
        }

        return longestMatches.getFirst();
    }

    @Override
    public List<RecognisedToken> analyze(String program, List<Token> definedTokens) {
        var tokenMatchers = Stream.concat(
                definedTokens.stream(),
                Stream.of(new TerminalToken())
        ).map(TokenMatcher::new).toList();

        int curr = 0;
        StringBuilder read = new StringBuilder();
        List<RecognisedToken> recognisedTokens = new ArrayList<>();
        program = program + "$";
        int line = 1;

        while (curr != program.length()) {
            var readChar = program.charAt(curr);
            read.append(readChar);
            curr += 1;

            if (readChar == '\n' || readChar == '\r') {
                line += 1;
            }

            var word = read.toString();

            if (anyMatch(word, tokenMatchers)) {
                continue;
            }

            var result = getMaxTokenMatched(word, tokenMatchers);

            if (result == null) {
                throw new TokenNotRecognizedException(word, line);
            }

            if (!(result.getToken() instanceof IgnoredToken)) {
                recognisedTokens.add(RecognisedToken.match(result.getToken(), word.substring(0, result.getMatchedLength()), line));
            }

            curr -= (word.length() - result.getMatchedLength());
            read = new StringBuilder();
        }

        recognisedTokens.add(RecognisedToken.match(new TerminalToken(), "$", line));

        return new ArrayList<>(recognisedTokens);
    }
}
