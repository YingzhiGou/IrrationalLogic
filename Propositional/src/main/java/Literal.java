package im.irrational.logic.propositional;

import org.jetbrains.annotations.NotNull;

public class Literal implements ILogicFormula {
    public static final String NEGATION_SYMBLE = "~";
    private int value;
    private String name;

    public Literal(@NotNull Literal aThis) {
        this.value = aThis.value;
        this.name = aThis.name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return (value > 0 ? name : NEGATION_SYMBLE.concat(name));
    }

    public ILogicFormula negation() {
        Literal neg = this.clone();
        neg.value = -neg.value;
        return neg;
    }

    @Override
    public Literal clone() {
        return new Literal(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal)) return false;
        Literal literal = (Literal) o;
        return value == literal.value &&
                name.equals(literal.name);
    }

    @Override
    public int hashCode() {
        return 31 * value + (name == null ? 0 : name.hashCode());
    }
}
