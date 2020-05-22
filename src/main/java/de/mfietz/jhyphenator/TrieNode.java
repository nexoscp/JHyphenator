package de.mfietz.jhyphenator;

import javax.annotation.Nonnull;

public class TrieNode {
    private final IntTrieNodeArrayMap codePoint = new IntTrieNodeArrayMap();
    int[] points;

    public @Nonnull IntTrieNodeArrayMap getCodePoint() {
        return codePoint;
    }
}
