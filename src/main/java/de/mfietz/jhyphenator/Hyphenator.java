package de.mfietz.jhyphenator;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Character.isDigit;
import static java.lang.Integer.parseInt;

/**
 * Hyphenator.java is an highly optimized adaptation of parts from Mathew
 * Kurian's TextJustify-Android Library:
 * https://github.com/bluejamesbond/TextJustify-Android/
 */
public class Hyphenator implements Serializable {
    private static final ConcurrentHashMap<HyphenationPattern, Hyphenator> cached = new ConcurrentHashMap<>();

    private final TrieNode trie;
    private final int leftMin;
    private final int rightMin;

    private Hyphenator(@Nonnull HyphenationPattern pattern) {
        this.trie = createTrie(pattern.patterns);
        this.leftMin = pattern.leftMin;
        this.rightMin = pattern.rightMin;
    }

    /**
     * Returns a hyphenator instance for a given hypenation pattern
     *
     * @param pattern hyphenation language pattern
     * @return newly created or cached hyphenator instance
     */
    public static @Nonnull Hyphenator getInstance(final @Nonnull HyphenationPattern pattern) {
        return cached.computeIfAbsent(pattern, Hyphenator::new);
    }

    private static TrieNode createTrie(final @Nonnull Map<Integer, String> patternObject) {
        final var tree = new TrieNode();

        for (final var entry : patternObject.entrySet()) {
            final var key = entry.getKey();
            final var value = entry.getValue();
            final var patterns = new String[value.length() / key];
            for (int i = 0; i + key <= value.length(); i = i + key) {
                patterns[i / key] = value.substring(i, i + key);
            }
            for (final String pattern : patterns) {
                var t = tree;

                for (int c = 0; c < pattern.length(); c++) {
                    final var chr = pattern.charAt(c);
                    if (isDigit(chr)) {
                        continue;
                    }
                    final var codePoint = pattern.codePointAt(c);
                    if (t.codePoint.get(codePoint) == null) {
                        t.codePoint.put(codePoint, new TrieNode());
                    }
                    t = t.codePoint.get(codePoint);
                }

                final var list = new IntArrayList();
                var digitStart = -1;
                for (int p = 0; p < pattern.length(); p++) {
                    if (isDigit(pattern.charAt(p))) {
                        if (digitStart < 0) {
                            digitStart = p;
                        }
                        if (p == pattern.length() - 1) {
                            // last number in the pattern
                            final var number = pattern.substring(digitStart);
                            list.add(parseInt(number));
                        }
                    } else if (digitStart >= 0) {
                        final var number = pattern.substring(digitStart, p);
                        list.add(parseInt(number));
                        digitStart = -1;
                    } else {
                        list.add(0);
                    }
                }
                t.points = list.toArray();
            }
        }
        return tree;
    }

    /**
     * Returns a list of syllables that indicates at which points the word can
     * be broken with a hyphen
     *
     * @param word_ Word to hyphenate
     * @return list of syllables
     */
    public @Nonnull List<String> hyphenate(final @Nonnull String word_) {
        final var word = "_" + word_ + "_";

        final var lowercase = word.toLowerCase();

        final var wordLength = lowercase.length();
        final var points = new int[wordLength];
        final var characterPoints = new int[wordLength];
        for (int i = 0; i < wordLength; i++) {
            points[i] = 0;
            characterPoints[i] = lowercase.codePointAt(i);
        }

        final var trie = this.trie;
        for (int i = 0; i < wordLength; i++) {
            var node = trie;
            for (int j = i; j < wordLength; j++) {
                node = node.codePoint.get(characterPoints[j]);
                if (node != null) {
                    final var nodePoints = node.points;
                    if (nodePoints != null) {
                        for (int k = 0, nodePointsLength = nodePoints.length;
                             k < nodePointsLength; k++) {
                            points[i + k] = Math.max(points[i + k], nodePoints[k]);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        final var result = new ArrayList<String>();
        var start = 1;
        for (int i = 1; i < wordLength - 1; i++) {
            if (i > this.leftMin && i < (wordLength - this.rightMin) && points[i] % 2 > 0) {
                result.add(word.substring(start, i));
                start = i;
            }
        }
        if (start < word.length() - 1) {
            result.add(word.substring(start, word.length() - 1));
        }
        return result;
    }

}
