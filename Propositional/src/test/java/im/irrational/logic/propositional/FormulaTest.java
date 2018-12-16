package im.irrational.logic.propositional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FormulaTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void size() {
        Formula formula = new Formula();
        assertEquals(formula.size(), 0);
        try {
            formula.add(new Literal("test", 1));
            assertEquals(formula.size(), 1);
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Disabled("not implemented")
    @Test
    void convertToCNF() {
    }

    @Test
    void contains() {
        Formula formula = new Formula();
        Literal literal = new Literal("test", 1);
        assertFalse(formula.contains(literal));
        try {
            formula.add(literal);
            assertTrue(formula.contains(literal));
            assertTrue(formula.contains(literal.clone()));
            assertFalse(formula.contains(literal.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
        Formula anotherFormula = new Formula();
        assertFalse(formula.contains(anotherFormula));
        anotherFormula.setType(eClauseType.CONJUNCTIVE);
        try {
            formula.add(anotherFormula);
            assertTrue(formula.contains(literal));
            assertFalse(formula.contains(anotherFormula));
            assertFalse(formula.contains(anotherFormula.clone()));
            assertFalse(formula.contains(anotherFormula.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
        try {
            anotherFormula.add(literal.negation());
            formula.add(anotherFormula);
            assertTrue(formula.contains(literal));
            assertFalse(formula.contains(new Formula(eClauseType.CONJUNCTIVE)));
            assertTrue(formula.contains(anotherFormula));
            assertTrue(formula.contains(anotherFormula.clone()));
            assertFalse(formula.contains(anotherFormula.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
        }
    }

    @Test
    void contaionsAll() {
        Formula formula = new Formula();
        Literal literal = new Literal("test", 1);
        try {
            formula.add(literal);
            formula.add(literal.negation());
            Literal[] literals = new Literal[2];
            assertFalse(formula.containsAll(Arrays.asList(literals)));
            literals[0] = literal;
            literals[1] = literal.negation();
            assertTrue(formula.containsAll(Arrays.asList(literals)));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Disabled("not implemented")
    @Test
    void getType() {
    }

    @Disabled("not implemented")
    @Test
    void setType() {
    }

    @Disabled("not implemented")
    @Test
    void add() {
    }

    @Disabled("not implemented")
    @Test
    void negation() {
    }

    @Disabled("not implemented")
    @Test
    void test_clone() {
    }

    @Disabled("not implemented")
    @Test
    void test_hashCode() {
    }

    @Disabled("not implemented")
    @Test
    void test_equals() {
    }

    @Disabled("not implemented")
    @Test
    void test_toString() {
    }

    @Disabled("not implemented")
    @Test
    void iterator() {
    }
}