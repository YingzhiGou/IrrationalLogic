package im.irrational.logic.propositional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FormulaTest {
    Formula formula;
    Literal literal;
    @BeforeEach
    void setUp() {
        formula = new Formula();
        literal = new Literal("test", 1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void size() {
        assertEquals(formula.size(), 0);
        try {
            formula.add(new Literal("test", 1));
            assertEquals(1, formula.size());
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

    @Test
    void getType() {
        assertEquals(eClauseType.DISJUNCTIVE, formula.getType());
        try {
            assertEquals(eClauseType.CONJUNCTIVE, formula.negation().getType());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void setType() {
        formula.setType(eClauseType.CONJUNCTIVE);
        assertEquals(eClauseType.CONJUNCTIVE, formula.getType());
        formula.setType(eClauseType.DISJUNCTIVE);
        assertEquals(eClauseType.DISJUNCTIVE, formula.getType());
    }

    @Test
    void add() {
        try {
            formula.add(literal);
            System.out.println(formula.toString());
            assertEquals(1, formula.size());
            assertTrue(formula.contains(literal));
            formula.add(formula.negation());
            System.out.println(formula.toString());
            assertEquals(2, formula.size());
            formula.add(formula.negation());
            System.out.println(formula.toString());
            assertEquals(3, formula.size());
            formula.add(formula);
            System.out.println(formula.toString());
            assertEquals(4, formula.size());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }

    }

    @Test
    void negation() {
        try {
            assertEquals(eClauseType.CONJUNCTIVE, formula.negation().getType());
            formula.add(literal);
            Formula neg = formula.negation();
            assertEquals(eClauseType.CONJUNCTIVE, neg.getType());
            assertEquals("(~test)", neg.toString());
            // todo more test cases
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_clone() {
        Formula another = formula.clone();
        try {
            formula.add(literal);
            assertNotEquals(formula, another);
            assertFalse(formula.equals(another));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_hashCode() {
        assertEquals(formula.hashCode(), formula.hashCode());
        assertEquals(formula.hashCode(), formula.clone().hashCode());
        try {
            assertNotEquals(formula.hashCode(), formula.negation().hashCode());
            formula.add(literal);
            formula.add(formula.negation().clone());
            assertEquals(formula.hashCode(), formula.clone().hashCode());
            assertNotEquals(formula.hashCode(), formula.negation().hashCode());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_equals() {
        assertTrue(formula.equals(formula));
        assertTrue(formula.equals(formula.clone()));
        assertFalse(formula.equals(literal));
        try {
            assertFalse(formula.equals(formula.negation()));

            formula.add(literal.negation());
            assertTrue(formula.equals(formula));
            assertTrue(formula.equals(formula.clone()));
            assertFalse(formula.equals(literal));
            assertFalse(formula.equals(formula.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_toString() {
        try {
            formula.add(literal);
            System.out.println(formula.toString());
            assertEquals("(test)", formula.toString());
            formula.add(formula.negation());
            System.out.println(formula.toString());
            assertEquals("((~test)|test)", formula.toString());
            formula.add(formula.negation());
            System.out.println(formula.toString());
            assertEquals("(((test)&~test)|(~test)|test)", formula.toString());
            formula.add(formula);
            System.out.println(formula.toString());
            assertEquals("(((test)&~test)|(test&~test)|(~test)|test)", formula.toString());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Disabled("not implemented")
    @Test
    void iterator() {
    }
}