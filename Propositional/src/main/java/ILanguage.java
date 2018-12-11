package im.irrational.logic.propositional;

public interface ILanguage<T> {
    /**
     * convert a language element <T> to String
     *
     * @param languageElement an element in the language
     * @return the string representation of the given element
     */
    String toString(T languageElement);

    /**
     * parse the given string to the language element <T>
     *
     * @param str string to parse
     * @return the language element <T>
     */
    T parse(String str) throws LanguageFormatException;
}
