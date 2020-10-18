package im.irrational.logic.propositional;

import java.util.Objects;

public class Literal implements ILogicFormula {
    public static final String NEGATION_SYMBOL = "~";
    private final boolean value;
    private final String displayName;

    public static Literal TRUE = new Literal("TRUE", true);
    public static Literal FALSE = new Literal("FALSE", false);

    /***
     *
     * @param aThis
     * @deprecated deep copy constructor is disabled since Literal is now immutable
     */
    @Deprecated(since = "a0.2", forRemoval = true)
    public Literal(final Literal aThis) {
        Objects.requireNonNull(aThis, "cannot deep copy null");
        this.value = aThis.value;
        this.displayName = aThis.displayName;
    }

    public Literal(final String displayName, final boolean value) {
        this.displayName = displayName;
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return (value ? displayName : NEGATION_SYMBOL.concat(displayName));
    }

    public Literal negation() {
        return new Literal(this.displayName, !value);
    }

    @Override
    public Literal clone() {
//        Literal newLiteral;
//        try {
//            newLiteral = (Literal) super.clone();
//        } catch (CloneNotSupportedException e) {
//            newLiteral = new Literal(this);
//        }
//        return newLiteral;
        return this; // no deep copy or clone, this object is immutable
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal)) return false;
        Literal literal = (Literal) o;
        return value == literal.value &&
                displayName.equals(literal.displayName);
    }

    @Override
    public int hashCode() {
        return 31 * (value ? 1 : 0) + (displayName == null ? 0 : displayName.hashCode());
    }
}
