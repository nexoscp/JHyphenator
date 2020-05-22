package de.mfietz.jhyphenator.collection;

import javax.annotation.Nonnull;

public class TrieNode {
    private final IntTrieNodeArrayMap codePoint = new IntTrieNodeArrayMap();
    public int[] points;

    public @Nonnull IntTrieNodeArrayMap getCodePoint() {
        return codePoint;
    }
}
