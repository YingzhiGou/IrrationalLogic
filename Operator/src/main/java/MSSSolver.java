import im.irrational.logic.propositional.*;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IteratorInt;

import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Math.abs;

/**
 * find all the maximum satisfiable subsets
 */
public class MSSSolver {
    private HashMap<String, Integer> dictWord2Int = new HashMap<>();
    private HashMap<Integer, String> dictInt2Word = new HashMap<>();

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

    public Vec<VecInt> encode(final Clause formula) throws FormulaError {
        Clause cnfFormula = formula.toCNF();
        if (!cnfFormula.isCNF()) {
            // formula is not in conjunctive normal form
            throw new FormulaError(String.format("failed to convert formula to CNF: formula=%s, cnf=%s", formula.toString(), cnfFormula.toString()));
        }
        Vec<VecInt> encodedFormula = new Vec<>();
        for (ILogicFormula disjunctiveClouse : cnfFormula) {
            VecInt encodedClause = new VecInt();
            if (disjunctiveClouse instanceof Literal) {
                encodedClause.push(encode((Literal) disjunctiveClouse));
            } else if (disjunctiveClouse instanceof Clause) {
                for (ILogicFormula element : (Clause) disjunctiveClouse) {
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
            throw new FormulaError(String.format("failed to decode %d, word does not exist.", value));
        }
    }

    public Clause decode(final VecInt values) throws FormulaError {
        Clause clause = new Clause(eClauseType.DISJUNCTIVE);
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
            clause.add(decode(disjunctiveClause));
        }
        return clause;
    }
}
