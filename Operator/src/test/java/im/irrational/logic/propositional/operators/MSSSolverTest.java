package im.irrational.logic.propositional.operators;

import im.irrational.logic.propositional.Clause;
import im.irrational.logic.propositional.FormulaError;
import im.irrational.logic.propositional.Literal;
import im.irrational.logic.propositional.eClauseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MSSSolverTest {
    MSSSolver solver;

    @BeforeEach
    void setUp() {
        solver = new MSSSolver();
    }

    @Test
    void MaxSat_classic() {
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
    void MaxSat_classic_redundant_rules() {
        // KB = (~P | ~Q | ~R) & (P12 | P21 | ~B11) & (~P11 | B21)  &  (~P22 | B21)
        // soft = P & Q
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("P", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P12", true),
                                    new Literal("P21", true),
                                    new Literal("B11", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P11", false),
                                    new Literal("B21", true)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P22", false),
                                    new Literal("B21", true))),
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
    void MaxSat_classic_2() {
        // KB = ~P|~Q|~R|~S
        // soft = P & Q & S
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", false),
                            new Literal("P", false),
                            new Literal("Q", false),
                            new Literal("S", false)),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true),
                            new Literal("S", true)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(3, solutions.size());
            assertEquals("[(Q&R&S), (P&R&S), (P&Q&R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_no_kb(){
        // empty kb
        // kb =
        // soft = P & Q
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    null,
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(1, solutions.size());
            assertEquals("[(P&Q&R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_empty_kb(){
        // empty kb
        // kb = {}
        // soft = P & Q
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.DISJUNCTIVE),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(1, solutions.size());
            assertEquals("[(P&Q&R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_classic_3(){
        // KB = (~P|~Q|~R|~S) & (~P|Q)
        // soft = P & Q & S
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("P", false),
                                    new Literal("Q", false),
                                    new Literal("S", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("Q", true))),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true),
                            new Literal("S", true)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(2, solutions.size());
            assertEquals("[(Q&R&S), (P&Q&R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_eliminate_soft_clauses(){
        // KB = (~Q|~R) & (~P|~R)
        // soft = P & Q
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("R", false))),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(1, solutions.size());
            assertEquals("[(R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_no_soft_clauses(){
        // KB = (~Q|~R) & (~P|~R)
        // soft =
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("R", false))),
                    null,
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(1, solutions.size());
            assertEquals("[(R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_empty_clauses(){
        // KB = (~Q|~R) & (~P|~R)
        // soft = ()
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("R", false))),
                    new Clause(eClauseType.DISJUNCTIVE),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(1, solutions.size());
            assertEquals("[(R)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_no_hard_clauses(){
        // KB = (~Q|~R) & (~P|~R)
        // soft = P & Q
        // hard =
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("R", false))),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true)),
                    null, 10);
            assertEquals(1, solutions.size());
            assertEquals("[(P&Q)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_empty_hard_clauses(){
        // KB = (~Q|~R) & (~P|~R)
        // soft = P & Q
        // hard =
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("R", false))),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true)),
                    new Clause(eClauseType.DISJUNCTIVE), 10);
            assertEquals(1, solutions.size());
            assertEquals("[(P&Q)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_inconsistent_kb(){
        // KB = (~P|~Q|~R|~S) & (~P|Q) & (~P|~Q) & P
        // soft = P & Q & S
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("P", false),
                                    new Literal("Q", false),
                                    new Literal("S", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("Q", true)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("Q", false)),
                            new Literal("P", true)),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true),
                            new Literal("S", true)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("P", true)), 10);
            assertNull(solutions);
        } catch (Timeout error) {
            error.printStackTrace();
            fail(error);
        } catch (FormulaError formulaError) {
            formulaError.printStackTrace();
        }
    }

    @Test
    void MaxSat_inconsistent_soft_clauses(){
        // KB = (~Q|~R) & (~P|~R)
        // soft = ~P & P
        // hard = R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("R", false))),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("P", false)),
                    new Clause(eClauseType.DISJUNCTIVE,
                            new Literal("R", true)), 10);
            assertEquals(1, solutions.size());
            assertEquals("[(R&~P)]", solutions.toString());
        } catch (FormulaError | Timeout error) {
            error.printStackTrace();
            fail(error);
        }
    }

    @Test
    void MaxSat_inconsistent_hard_clauses(){
        // KB = (~Q|~R) & (~P|~R)
        // soft = P & Q
        // hard = R & ~R
        try {
            List<Clause> solutions = solver.findAllMaxSatisfiableSubFormulas(
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("R", false),
                                    new Literal("Q", false)),
                            new Clause(eClauseType.DISJUNCTIVE,
                                    new Literal("P", false),
                                    new Literal("R", false))),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("P", true),
                            new Literal("Q", true)),
                    new Clause(eClauseType.CONJUNCTIVE,
                            new Literal("R", true),
                            new Literal("R", false)), 10);
            fail("should not reach here");
        } catch (Timeout error) {
            error.printStackTrace();
            fail(error);
        } catch (FormulaError formulaError) {
            assertEquals("Creating Empty clause ?", formulaError.getMessage());
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
            // check the result, note that the order of the vectors may be different from time to time
            assertEquals(2, encoded.size());

            boolean clause1checked = false, clause2checked = false;

            for (Iterator<VecInt> it = encoded.iterator(); it.hasNext();){
                VecInt clause = it.next();
                if (clause.size() == 1){
                    assertEquals(3, clause.get(0));
                    clause2checked = true;
                } else if (clause.size() == 2){
                    assertEquals(-1, clause.get(0));
                    assertEquals(2, clause.get(1));
                    clause1checked = true;
                }
            }
            assertTrue(clause1checked);
            assertTrue(clause2checked);
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

    @Test
    void MaxSat_exhaustive(){
        this.MaxSat_classic();
        this.MaxSat_classic_2();
        this.MaxSat_classic_3();
        this.MaxSat_classic_redundant_rules();
        this.MaxSat_eliminate_soft_clauses();
        this.MaxSat_empty_clauses();
        this.MaxSat_empty_hard_clauses();
        this.MaxSat_empty_kb();
        this.MaxSat_inconsistent_hard_clauses();
        this.MaxSat_inconsistent_kb();
        this.MaxSat_inconsistent_soft_clauses();
        this.MaxSat_no_hard_clauses();
        this.MaxSat_no_kb();
        this.MaxSat_no_soft_clauses();
    }
}