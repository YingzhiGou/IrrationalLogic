package im.irrational.logic.propositional.operators;

import im.irrational.logic.propositional.Clause;
import im.irrational.logic.propositional.FormulaError;
import im.irrational.logic.propositional.Literal;
import im.irrational.logic.propositional.eClauseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MSSSolverTest {
    MSSSolver solver;

    @BeforeEach
    void setUp() {
        solver = new MSSSolver();
    }

    @Test
    void findAllMaxSatisfiableSubFormulas() {
        // KB = ~R | ~P | ~Q
        // soft = P & Q
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", false),
                            new Literal("P", false),
                            new Literal("Q", false)),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(2, solutions.size());
            assertEquals("[(Q&R), (P&R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void encode_literal() {
        int encoded = solver.encode(new Literal("a", true));
        assertEquals(1, encoded);
        encoded = solver.encode(new Literal("a", false));
        assertEquals(-1, encoded);
        encoded = solver.encode(new Literal("b", false));
        assertEquals(-2, encoded);
    }

    @Test
    void encode_array() {
        int[] intArray = new int[] {4,5,6,7,8};
        VecInt encoded = solver.encode(intArray);
        assertEquals(new VecInt(intArray), encoded);
    }

    @Test
    void encode_clause() {
        try {
            Vec<VecInt> encoded = solver.encode(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("a", true),
                            new Literal("b", false)));
            assertEquals(1, encoded.get(0).get(0));
            assertEquals(-2, encoded.get(1).get(0));

            encoded = solver.encode(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("a", false),
                                    new Literal("b", true)),
                            new Literal("c", true)));
            assertEquals(3, encoded.get(0).get(0));
            assertEquals(-1, encoded.get(1).get(0));
            assertEquals(2, encoded.get(1).get(1));

        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void decode_integer() {
        try {
            solver.encode(new Literal("a", true));
            assertEquals(new Literal("a", true), solver.decode(1));
            assertNotEquals(new Literal("a", true), solver.decode(-1));
            assertEquals(new Literal("a", false), solver.decode(-1));

            assertEquals(new Literal("Unnamed123", false), solver.decode(-123));

        } catch (FormulaError e){
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    void decode_VecInt() {
        solver.encode(new Literal("a", false));
        solver.encode(new Literal("b", false));

        int[] intArray = new int[] {1,-2,6,-7,8};
        VecInt vecInt = new VecInt(intArray);
        Clause expected = new Clause(eClauseType.DISJUNCTIVE,
                new Literal("a", true),
                new Literal("b", false),
                new Literal("Unnamed6", true),
                new Literal("Unnamed7", false),
                new Literal("Unnamed8", true));
        try {
            assertEquals(expected, solver.decode(vecInt, eClauseType.DISJUNCTIVE));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
        expected.setType(eClauseType.CONJUNCTIVE);
        try {
            assertEquals(expected, solver.decode(vecInt, eClauseType.CONJUNCTIVE));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }

    @Test
    void decode_VecVecInt() {
        solver.encode(new Literal("a", false));
        solver.encode(new Literal("b", false));

        Vec<VecInt> vec = new Vec<>();
        int[] intArray1 = new int[] {1,-2,6};
        vec.push(new VecInt(intArray1));
        int[] intArray2 = new int[] {2,-7,8,-1};
        vec.push(new VecInt(intArray2));

        Clause expected = new Clause(eClauseType.CONJUNCTIVE,
                new Clause(eClauseType.DISJUNCTIVE,
                        new Literal("a", true),
                        new Literal("b", false),
                        new Literal("Unnamed6", true)),
                new Clause(eClauseType.DISJUNCTIVE,
                        new Literal("b", true),
                        new Literal("Unnamed7", false),
                        new Literal("Unnamed8", true),
                        new Literal("a", false)));

        try {
            assertEquals(expected, solver.decode(vec));
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
            fail(formulaError);
        }
    }
}