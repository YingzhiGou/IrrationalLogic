import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;

import java.util.HashMap;

/**
 * find all the maximum satisfiable subsets
 */
public class MSSSolver<Literal extends im.irrational.logic.propositional.Literal, Clause extends im.irrational.logic.propositional.Clause> {
    private HashMap<String, Integer> dictWord2Int = new HashMap<>();
    private HashMap<Integer, String> dictInt2Word = new HashMap<>();

    public VecInt encode(Literal literal) {

    }

    public Vec<VecInt> encode(Clause clause) {

    }
}
