package im.irrational.logic.propositional;

import java.util.HashMap;

public class Vocabulary implements ILanguage<Literal> {
    private static HashMap<String, Integer> dictWord2Int = new HashMap<>();
    private static HashMap<Integer, String> dictInt2Word = new HashMap<>();
    static private int counter = 1;
    static private int tempCounter;
    private final String NEGATION_SYMBOL;

    public Vocabulary() {
        this.NEGATION_SYMBOL = Literal.NEGATION_SYMBOL;
    }

    static public int next() {
        return ++counter;
    }

    static public void resetTemp() {
        tempCounter = counter;
    }

    static public int nextTemp() {
        if (tempCounter <= counter) {
            resetTemp();
        }
        return ++tempCounter;
    }

    public String toString(Literal word) {
        return word.toString();
    }

    public Literal parse(String str) throws LanguageFormatException {
        String word = str.replaceAll(String.format("[\\s,%s]", NEGATION_SYMBOL), "");
        int value = 0;
        if (dictWord2Int.containsKey(word)) {
            value = dictWord2Int.get(word);
        } else {
            // new value
            value = next();
            while (dictInt2Word.containsKey(value)) {
                value = next();
            }
            dictWord2Int.put(word, value);
            dictInt2Word.put(value, word);
        }
        if (str.contains(NEGATION_SYMBOL)) {
            value = -value;
        }
        return new Literal(word, value);
    }

    public Literal toLiteral(int i) {
        int value = i > 0 ? i : -i;
        if (dictInt2Word.containsKey(value)) {
            return new Literal(dictInt2Word.get(value), i);
        } else return null;
    }
}
