package im.irrational.logic.propositional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
            fail(formulaError.getMessage());
        }
    }

    @Disabled("not implemented")
    @Test
    void convertToCNF() {
    }

    @Disabled("not implemented")
    @Test
    void contains() {
    }

    @Disabled("not implemented")
    @Test
    void contaionsAll() {
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