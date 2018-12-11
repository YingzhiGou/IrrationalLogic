package im.irrational.logic.propositional;

import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyTest {

    @org.junit.jupiter.api.Test
    void next() {
        int current = Vocabulary.next();
        assertEquals(current + 1, Vocabulary.next());
    }

    @org.junit.jupiter.api.Test
    void resetTemp() {
        int current = Vocabulary.next();
        Vocabulary.resetTemp();
        assertEquals(current + 1, Vocabulary.nextTemp());
        assertEquals(current + 2, Vocabulary.nextTemp());
        assertEquals(current + 3, Vocabulary.nextTemp());

        Vocabulary.resetTemp();
        assertEquals(current + 1, Vocabulary.nextTemp());
        assertEquals(current + 2, Vocabulary.nextTemp());
        assertEquals(current + 3, Vocabulary.nextTemp());

        Vocabulary.resetTemp();
    }

    @org.junit.jupiter.api.Test
    void nextTemp() {
        int current = Vocabulary.next();
        Vocabulary.resetTemp();
        assertEquals(current + 1, Vocabulary.nextTemp());
        assertEquals(current + 2, Vocabulary.nextTemp());
        assertEquals(current + 3, Vocabulary.nextTemp());

        Vocabulary.resetTemp();
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        Vocabulary voc = new Vocabulary();
        String str = "p";
        testToStringHelper(voc, str);
        str = "~p";
        testToStringHelper(voc, str);
        str = "~seoaf";
        testToStringHelper(voc, str);
        str = " ~seoaf ";
        testToStringHelper(voc, str.trim());
    }

    private void testToStringHelper(@NotNull Vocabulary voc, String str) {
        try {
            Literal l = voc.parse(str);
            assertEquals(l.toString(), str);
        } catch (LanguageFormatException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void parse() {
        Vocabulary voc = new Vocabulary();
        String str = "p";
        try {
            Literal l = voc.parse(str);
            assertEquals(l.getName(), "p");
            assertEquals(voc.toLiteral(l.getValue()), l);
        } catch (LanguageFormatException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        str = " ~ d";
        try {
            Literal l = voc.parse(str);
            assertEquals(l.getName(), "d");
            assertEquals(voc.toLiteral(l.getValue()), l);
        } catch (LanguageFormatException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void toLiteral() {
        Vocabulary voc = new Vocabulary();
        assertNull(voc.toLiteral(623243));
        assertNull(voc.toLiteral(-234098));
    }
}