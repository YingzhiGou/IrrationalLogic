package im.irrational.logic.propositional.operators;

import im.irrational.logic.propositional.*;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.pb.SolverFactory;
import org.sat4j.specs.*;
import org.sat4j.tools.ModelIterator;

import java.util.*;

import static java.lang.Math.abs;

/**
 * find all the maximum satisfiable subsets
 */
public class MSSSolver {
    private final HashMap<String, Integer> dictWord2Int = new HashMap<>();
    private final HashMap<Integer, String> dictInt2Word = new HashMap<>();
    private final HashSet<Integer> tempVariables = new HashSet<>();
    private SAT4JSolverType solverType = SAT4JSolverType.DEFAULT;

    public MSSSolver() {
    }

    public List<Clause> findAllMaxSatisfiableSubFormulas(final Clause kb, final Clause softFormula, final Clause hardFormula, final int solverTimeout) throws FormulaError, Timeout {
        // init solver
        ISolver solver = null;
        switch (this.solverType) {
            case LIGHT:
                solver = SolverFactory.newLight();
                break;
            case DEFAULT:
            default:
                solver = SolverFactory.newDefault();
        }
        solver.setTimeout(solverTimeout);
        // clear temporary variables
        this.tempVariables.clear();
        // add kb
        try {
            addHardClauses(solver, kb);
        } catch (ContradictionException e) {
            throw new FormulaError(String.format("Inconsistent Knowledge Base"));
        }
        // add hard clauses
        try {
            addHardClauses(solver, hardFormula);
        } catch (ContradictionException e) {
            throw new FormulaError(e.getMessage());
        }
        // add soft clauses
        HashMap<Integer, VecInt> selectorClauseMap = addSoftClauses(solver, softFormula);
        // create selector vector
        VecInt selectors = new VecInt();
        for (Integer selector : selectorClauseMap.keySet()){
            selectors.push(selector);
        }
        LinkedList<Clause> MSSes = new LinkedList<>();
        boolean unsat = true;
        // find maximum subsat
        try {
            for (int bound = selectorClauseMap.size(); bound>0; bound--){
                try{
                    IConstr selectionConstraint = solver.addAtLeast(selectors, bound);
                    ModelIterator mi = new ModelIterator(solver);
                    while (mi.isSatisfiable()){
                        unsat = false;
                        int[] model = mi.model();
                        // create maxsat subset according to the model
                        Clause mss = (hardFormula == null? new Clause(eClauseType.CONJUNCTIVE) : hardFormula.toCNF());
                        VecInt blocking = new VecInt();
                        for (int value : model){
                            if (selectorClauseMap.containsKey(value)){
                                blocking.push(-value);
                                VecInt clause = selectorClauseMap.get(value);
                                Clause disjunctiveClause = new Clause(eClauseType.DISJUNCTIVE);
                                for (IteratorInt it = clause.iterator(); it.hasNext();){
                                    int literal = it.next();
                                    disjunctiveClause.add(decode(literal));
                                    blocking.push(-literal);
                                }
                                mss.add(disjunctiveClause);
                            }
                        }
                        try{
                            solver.addBlockingClause(blocking);
                        } catch (ContradictionException e){
                            // removing constraint if it has been added?
                        }
                        // this is a hack
                        // to keep the maximum sets
                        mss = mss.toCNF();
                        boolean isMaximum = true;
                        LinkedList<Clause> redundant = new LinkedList<>();
                        for (Clause existingMSS: MSSes){
                            if (existingMSS.containsAll(mss)){
                                isMaximum = false;
                                break;
                            } else if (mss.containsAll(existingMSS)){
                                redundant.push(existingMSS);
                            }
                        }
                        if (isMaximum) {
                            // add maximum set
                            MSSes.add(mss);
                        }
                        // remove sets that are no longer maximum
                        for (Clause toRemove : redundant){
                            MSSes.remove(toRemove);
                        }
                    }
                    // backtrack
                    solver.removeConstr(selectionConstraint);
                } catch (ContradictionException e){

                }
            }
            if (unsat){
                // no soft clauses can be added to the max set, result contains only hard formula
                MSSes.add(hardFormula.toCNF());
                unsat = false;
            }
        } catch (TimeoutException e){
            throw new Timeout(e.getMessage());
        } finally {
            if (unsat){
                MSSes = null;
            }
        }
        return MSSes;
    }

    /**
     * you are not expected to understand this
     *
     * @return
     * @param solver
     */
//    private HashSet<HashSet<VecInt>> findAllCoMSSes(ISolver solver) {
//        int bound = 1;
//        HashSet<HashSet<VecInt>> comsses = new HashSet<>();
//
//
//    }

    private void addHardClauses(final ISolver solver, final Clause formula) throws FormulaError, ContradictionException {
        if (formula != null) {
            Vec<VecInt> encodedHardFormula = encode(formula);
            for (Iterator<VecInt> it = encodedHardFormula.iterator(); it.hasNext(); ) {
                solver.addClause(it.next());
            }
        }
    }

    private HashMap<Integer, VecInt> addSoftClauses(final ISolver solver, final Clause formula) throws FormulaError {
        HashMap<Integer, VecInt> selectorClauseMap = new HashMap<>();
        if (formula != null) {
            Vec<VecInt> encodedSoftFormula = encode(formula);
            // generate selector variables
            int var = dictInt2Word.size() + tempVariables.size() + 1;

            for (Iterator<VecInt> it = encodedSoftFormula.iterator(); it.hasNext();) {
                while (dictInt2Word.containsKey(var) || tempVariables.contains(var)) {
                    var++;
                }
                tempVariables.add(var);
                // add soft clause with selector variable
                VecInt sWithY = new VecInt();
                VecInt clause = it.next();
                sWithY.pushAll(clause);
                sWithY.push(-var); //deselect the clause by default

                selectorClauseMap.put(var, clause);

                try {
                    solver.addClause(sWithY);
                } catch (ContradictionException e) {
                    throw new FormulaError(String.format("Unexpected Error with the soft formula: %s", formula.toString()));
                }
            }
        }
        return selectorClauseMap;
    }

    enum SAT4JSolverType {
        LIGHT,
        DEFAULT
    }

    public int encode(final Literal literal) {
        String word = literal.getName();
        int value = 0;
        if (dictWord2Int.containsKey(word)) {
            value = dictWord2Int.get(word);
        } else {
            // new value
            value = dictWord2Int.size() + 1;
            while (dictInt2Word.containsKey(value)) {
                value += 1;
            }
            dictWord2Int.put(word, value);
            dictInt2Word.put(value, word);
        }
        if (literal.getValue()) {
            return value;
        } else {
            return -value;
        }
    }

    public VecInt encode(final int[] intArr) {
        return new VecInt(intArr);
    }

    public Vec<VecInt> encode(final Clause formula) throws FormulaError {
        Clause cnfFormula = formula.toCNF();
        if (!cnfFormula.isCNF()) {
            // formula is not in conjunctive normal form
            throw new FormulaError(String.format("failed to convert formula to CNF: formula=%s, cnf=%s", formula.toString(), cnfFormula.toString()));
        }
        Vec<VecInt> encodedFormula = new Vec<>();
        for (ILogicFormula disjunctiveClause : cnfFormula) {
            VecInt encodedClause = new VecInt();
            if (disjunctiveClause instanceof Literal) {
                encodedClause.push(encode((Literal) disjunctiveClause));
            } else if (disjunctiveClause instanceof Clause) {
                for (ILogicFormula element : (Clause) disjunctiveClause) {
                    encodedClause.push(encode((Literal) element));
                }
            }
            encodedFormula.push(encodedClause);
        }
        return encodedFormula;
    }

    public Literal decode(final int value) throws FormulaError {
        if (value == 0) {
            throw new FormulaError("0 is not a valid value to decode.");
        }

        int abs = abs(value);
        if (dictInt2Word.containsKey(abs)) {
            return new Literal(dictInt2Word.get(abs), value > 0);
        } else {
            return new Literal(String.format("Unnamed%d", abs), value>0);
        }
    }

    public Clause decode(final VecInt values, eClauseType type) throws FormulaError {
        Clause clause = new Clause(type);
        for (IteratorInt it = values.iterator(); it.hasNext(); ) {
            int value = it.next();
            clause.add(decode(value));
        }
        return clause;
    }

    public Clause decode(final Vec<VecInt> cnfFormula) throws FormulaError {
        Clause clause = new Clause(eClauseType.CONJUNCTIVE);
        for (Iterator<VecInt> it = cnfFormula.iterator(); it.hasNext(); ) {
            VecInt disjunctiveClause = it.next();
            clause.add(decode(disjunctiveClause, eClauseType.DISJUNCTIVE));
        }
        return clause;
    }
}
