package im.irrational.logic.propositional;

public interface ILogicFormula {
    /**
     * negation of the element
     *
     * @return the negation of the logic formula
     */
    ILogicFormula negation() throws FormulaError;

    ILogicFormula clone();
}
