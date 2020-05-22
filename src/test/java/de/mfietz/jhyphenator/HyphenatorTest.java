package de.mfietz.jhyphenator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;

import java.util.stream.Stream;

import static de.mfietz.jhyphenator.HyphenationPattern.*;
import static de.mfietz.jhyphenator.Hyphenator.getInstance;
import static java.lang.String.join;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HyphenatorTest {
    private static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(DE, "Kochschule", "Koch-schu-le"),
                Arguments.of(DE, "Seewetterdienst", "See-wet-ter-dienst"),
                Arguments.of(DE, "Hochverrat", "Hoch-ver-rat"),
                Arguments.of(DE, "Musterbeispiel", "Mus-ter-bei-spiel"),
                Arguments.of(DE, "Bundespräsident", "Bun-des-prä-si-dent"),
                Arguments.of(DE, "Schmetterling", "Schmet-ter-ling"),
                Arguments.of(DE, "Christian", "Chris-ti-an"),
                Arguments.of(EN_US, "crocodile", "croc-o-dile"),
                Arguments.of(EN_US, "activity", "ac-tiv-ity"),
                //Arguments.of(EN_US, "potato", "po-ta-to"), FIXME
                //Arguments.of(EN_US, "hyphenation", "hy-phen-a-tion"), FIXME
                Arguments.of(EN_US, "podcast", "pod-cast"),
                Arguments.of(EN_US, "message", "mes-sage")
                //Arguments.of(HU, "segítség", "se-gít-ség")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void test(
            final @Nonnull HyphenationPattern pattern,
            final @Nonnull String input,
            final @Nonnull String expected) {
        final var actual = join("-", getInstance(pattern).hyphenate(input));
        assertEquals(expected, actual);
    }
}
