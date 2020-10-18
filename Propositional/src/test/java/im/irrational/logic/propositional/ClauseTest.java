package im.irrational.logic.propositional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ClauseTest {
    Clause clause;
    Literal literal;
    @BeforeEach
    void setUp() {
        clause = new Clause();
        literal = new Literal("test", true);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void size() {
        assertEquals(clause.size(), 0);
        try {
            clause.add(new Literal("test", true));
            assertEquals(1, clause.size());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void toCNF() {
        try {
            // {\displaystyle \neg (B\lor C)}
            clause = new Clause(eClauseType.DISJUNCTIVE, new Literal("B", true), new Literal("C", true)).negation();
            assertTrue(clause.isCNF());
            // {\displaystyle \neg B\land \neg C}
            Clause cnf = clause.toCNF();
            assertTrue(cnf.isCNF());
            assertEquals("(~B&~C)", cnf.toString());

            // {\displaystyle (A\land B)\lor C}
            clause = new Clause(eClauseType.DISJUNCTIVE,
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("A", true),
                            new Literal("B", true)),
                    new Literal("C", true));
            assertFalse(clause.isCNF());
            // {\displaystyle (A\lor C)\land (B\lor C)}
            cnf = clause.toCNF();
            assertTrue(cnf.isCNF());
            assertEquals("((A|C)&(B|C))", cnf.toString());

            // {\displaystyle A\land (B\lor (D\land E))}
            clause = new Clause(eClauseType.CONJUNCTIVE,
                    new Literal("A", true),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("B", true),
                            new Clause(eClauseType.CONJUNCTIVE,
                                    new Literal("D", true),
                                    new Literal("E", true))));
            assertFalse(clause.isCNF());
            // {\displaystyle A\land (B\lor D)\land (B\lor E)}
            cnf = clause.toCNF();
            assertTrue(cnf.isCNF());
            assertEquals("((B|D)&(B|E)&A)", cnf.toString());

        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void contains() {
        assertFalse(clause.contains(literal));
        try {
            clause.add(literal);
            assertTrue(clause.contains(literal));
            assertTrue(clause.contains(literal.clone()));
            assertFalse(clause.contains(literal.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
        Clause anotherClause = new Clause();
        assertFalse(clause.contains(anotherClause));
        anotherClause.setType(eClauseType.CONJUNCTIVE);
        try {
            clause.add(anotherClause);
            assertTrue(clause.contains(literal));
            assertFalse(clause.contains(anotherClause));
            assertFalse(clause.contains(anotherClause.clone()));
            assertFalse(clause.contains(anotherClause.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
        try {
            anotherClause.add(literal.negation());
            clause.add(anotherClause);
            assertTrue(clause.contains(literal));
            assertFalse(clause.contains(new Clause(eClauseType.CONJUNCTIVE)));
            assertTrue(clause.contains(anotherClause));
            assertTrue(clause.contains(anotherClause.clone()));
            assertFalse(clause.contains(anotherClause.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
        }
    }

    @Test
    void contaionsAll() {
        try {
            clause.add(literal);
            clause.add(literal.negation());
            Literal[] literals = new Literal[2];
            assertFalse(clause.containsAll(Arrays.asList(literals)));
            literals[0] = literal;
            literals[1] = literal.negation();
            assertTrue(clause.containsAll(Arrays.asList(literals)));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void getType() {
        assertEquals(eClauseType.DISJUNCTIVE, clause.getType());
        try {
            assertEquals(eClauseType.CONJUNCTIVE, clause.negation().getType());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void setType() {
        clause.setType(eClauseType.CONJUNCTIVE);
        assertEquals(eClauseType.CONJUNCTIVE, clause.getType());
        clause.setType(eClauseType.DISJUNCTIVE);
        assertEquals(eClauseType.DISJUNCTIVE, clause.getType());
    }

    @Test
    void add() {
        try {
            clause.add(literal);
//            System.out.println(clause.toString());
            assertEquals(1, clause.size());
            assertTrue(clause.contains(literal));
            clause.add(clause.negation());
//            System.out.println(clause.toString());
            assertEquals(2, clause.size());
            clause.add(clause.negation());
//            System.out.println(clause.toString());
            assertEquals(3, clause.size());
//            System.out.println(String.format("%s + %s", clause.toString(), clause.toString()));
            clause.add(clause);
//            System.out.println(clause.toString());
            assertEquals(3, clause.size());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }

    }

    @Test
    void negation() {
        try {
            assertEquals(eClauseType.CONJUNCTIVE, clause.negation().getType());
            clause.add(literal);
            Clause neg = clause.negation();
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
        Clause another = clause.clone();
        try {
            clause.add(literal);
            assertNotEquals(clause, another);
            assertFalse(clause.equals(another));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_hashCode() {
        assertEquals(clause.hashCode(), clause.hashCode());
        assertEquals(clause.hashCode(), clause.clone().hashCode());
        try {
            assertNotEquals(clause.hashCode(), clause.negation().hashCode());
            clause.add(literal);
            clause.add(clause.negation().clone());
            assertEquals(clause.hashCode(), clause.clone().hashCode());
            assertNotEquals(clause.hashCode(), clause.negation().hashCode());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_equals() {
        assertTrue(clause.equals(clause));
        assertTrue(clause.equals(clause.clone()));
        assertFalse(clause.equals(literal));
        try {
            assertFalse(clause.equals(clause.negation()));

            clause.add(literal.negation());
            assertTrue(clause.equals(clause));
            assertTrue(clause.equals(clause.clone()));
            assertFalse(clause.equals(literal));
            assertFalse(clause.equals(clause.negation()));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_toString() {
        try {
            clause.add(literal);
//            System.out.println(clause.toString());
            assertEquals("(test)", clause.toString());
            clause.add(clause.negation());
//            System.out.println(clause.toString());
            assertEquals("((~test)|test)", clause.toString());
            clause.add(clause.negation());
//            System.out.println(clause.toString());
            assertEquals("(((test)&~test)|(~test)|test)", clause.toString());
            clause.add(clause);
//            System.out.println(clause.toString());
            assertEquals("(((test)&~test)|(~test)|test)", clause.toString());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void test_simplified() {
        try {
            clause.add(literal);
            assertEquals("(test)", clause.simplify().toString());
            clause.add(clause.negation());
            assertEquals("(test|~test)", clause.simplify().toString());
            clause.add(clause.negation());
            assertEquals("((test&~test)|test|~test)", clause.simplify().toString());
            clause.add(clause);
            assertEquals("((test&~test)|test|~test)", clause.simplify().toString());
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void isCNF() {
        try {
            assertFalse(clause.isCNF());
            assertTrue(clause.negation().isCNF());

            clause.add(literal);
            assertFalse(clause.isCNF());
            assertTrue(clause.negation().isCNF());

            clause.add(clause.negation());
            assertFalse(clause.isCNF());
            assertTrue(clause.negation().isCNF());

            clause.add(clause.negation());
            assertFalse(clause.isCNF());
//            System.out.println(clause.toString());
//            System.out.println(clause.negation().toString());
            assertFalse(clause.negation().isCNF());
//            System.out.println(clause.negation().simplified().toString());
            assertTrue(clause.negation().simplify().isCNF());
//            System.out.println(clause.negation().toString());

            clause.setType(eClauseType.CONJUNCTIVE);
//            System.out.println(clause.toString());
            assertFalse(clause.isCNF());
            assertFalse(clause.negation().isCNF());

        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }

    }
}