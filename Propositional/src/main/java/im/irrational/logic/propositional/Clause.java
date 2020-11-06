package im.irrational.logic.propositional;

import java.util.*;

public class Clause implements ILogicFormula, Iterable<ILogicFormula> {
    private final HashSet<ILogicFormula> elements = new HashSet<>();
    private eClauseType type;

    public Clause() {
        this(eClauseType.DISJUNCTIVE);
    }

    public Clause(final Clause e) {
        this(e.type, e.elements);
    }

    public Clause(final eClauseType type) {
        this.type = Objects.requireNonNull(type);
    }

    public Clause(final eClauseType type, final ILogicFormula... elements) {
        this.type = type;
        for (ILogicFormula l : elements) {
            this.elements.add(l.clone());
        }
    }

    public Clause(final eClauseType type, final Iterable<ILogicFormula> elements) {
        this(type);
        for (ILogicFormula l : elements) {
            this.elements.add(l.clone());
        }
    }

    /**
     * testing if the formula is CNF
     *
     * @return true if the formula is CNF
     */
    static Boolean isCNF(final Clause formula) {
        if (formula.type == eClauseType.CONJUNCTIVE) {
            for (ILogicFormula clause : formula.elements) {
                if (clause instanceof Clause) {
                    Clause f = (Clause) clause;
                    if (f.type == eClauseType.DISJUNCTIVE) {
                        for (ILogicFormula literal : f) {
                            if (!(literal instanceof Literal)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static Clause convertToCNF(final Clause c) throws FormulaError {
        Objects.requireNonNull(c);
        Clause cnf = new Clause(eClauseType.CONJUNCTIVE);
        if (c.size() == 0){
            return cnf;
        } else if (c.size() == 1 || c.type == eClauseType.CONJUNCTIVE) {
            /*
             If φ has the form P ^ Q, then:
			 CONVERT(P) must have the form P1 ^ P2 ^ ... ^ Pm, and
			 CONVERT(Q) must have the form Q1 ^ Q2 ^ ... ^ Qn,
			 where all the Pi and Qi are disjunctions of elements.
			 So return P1 ^ P2 ^ ... ^ Pm ^ Q1 ^ Q2 ^ ... ^ Qn.
            */
            for (ILogicFormula clause : c) {
                cnf.addAsCNF(clause);
            }
            return cnf;
        } else if (c.type == eClauseType.DISJUNCTIVE){
            /*
             If φ has the form P v Q, then:
			 CONVERT(P) must have the form P1 ^ P2 ^ ... ^ Pm, and
			 CONVERT(Q) must have the form Q1 ^ Q2 ^ ... ^ Qn,
			 where all the Pi and Qi are disjunctions of elements.
			 So we need a CNF formula equivalent to
			 (P1 ^ P2 ^ ... ^ Pm) v (Q1 ^ Q2 ^ ... ^ Qn).
			 So return (P1 v Q1) ^ (P1 v Q2) ^ ... ^ (P1 v Qn)
			 ^ (P2 v Q1) ^ (P2 v Q2) ^ ... ^ (P2 v Qn)
			 ...
			 ^ (Pm v Q1) ^ (Pm v Q2) ^ ... ^ (Pm v Qn)
			 */
            Clause p = new Clause(eClauseType.DISJUNCTIVE);
            Iterator<ILogicFormula> it = c.iterator();
            if (it.hasNext()) {
                ILogicFormula clause = it.next();
                if (clause instanceof Literal) {
                    p.add(clause);
                } else if (clause instanceof Clause) {
                    p.add(convertToCNF((Clause) clause));
                } else {
                    throw new FormulaError(String.format("Unknown logic element type: %s", clause.getClass().getName()));
                }
            }
            p = convertToCNF(p);

            Clause q = new Clause(eClauseType.DISJUNCTIVE);
            while (it.hasNext()) {
                ILogicFormula clause = it.next();
                if (clause instanceof Literal) {
                    q.add(clause);
                } else if (clause instanceof Clause) {
                    q.add(convertToCNF((Clause) clause));
                } else {
                    throw new FormulaError(String.format("Unknown logic element type: %s", clause.getClass().getName()));
                }
            }

            q = convertToCNF(q);

            for (ILogicFormula pClause : p) {
                for (ILogicFormula qClause : q) {
                    Clause disjunctive = new Clause(eClauseType.DISJUNCTIVE);
                    disjunctive.add(pClause);
                    disjunctive.add(qClause);
                    cnf.add(disjunctive);
                }
            }
            return cnf;
        } else {
            throw new FormulaError(String.format("formula must be a type of conjunctive or disjunctive, not %s", c.type.toString()));
        }
    }

    public int size() {
        return elements.size();
    }

    public boolean contains(final ILogicFormula other){
        return elements.contains(other);
    }

    /***
     * @deprecated this function is removed, it is not logically correct to compare elements of Clause without the type of the clause
     */
    @Deprecated(since = "a0.2", forRemoval = true)
    public boolean containsAll(final Collection<?> other) {
        return elements.containsAll(other);
    }

    /***
     * @deprecated this function is removed, it is not logically correct to compare elements of Clause without the type of the clause
     */
    @Deprecated(since = "a0.2", forRemoval = true)
    public boolean containsAll(final Clause other){
        return elements.containsAll(other.elements);
    }

    public eClauseType getType(){
        return type;
    }

    public void setType(eClauseType t){
        this.type = t;
    }

    public void add(final ILogicFormula clause) throws FormulaError {
        if (this.getClass() == clause.getClass()){
            Clause f = (Clause) clause;
            if (f.type == this.type){
                for (ILogicFormula c : f) {
                    this.add(c.clone());
                }
            } else if(f.size() > 0){
                this.elements.add(f.clone());
            }
        }
        else{
            this.elements.add(clause.clone());
        }
    }

    private void addAsCNF(final ILogicFormula clause) throws FormulaError {
        if (clause instanceof Literal) {
            this.add(clause);
        } else if (clause instanceof Clause) {
            this.add(convertToCNF((Clause) clause));
        } else {
            throw new FormulaError(String.format("Unknown logic element type: %s", clause.getClass().getName()));
        }
    }

    /**
     * simplify representations by turning Clause of 1 clause to the clause
     */
    public Clause simplify() throws FormulaError {
        Clause simplified = new Clause(this.getType());
        for (ILogicFormula clause : this) {
            if (clause instanceof Clause) {
                Clause formula = (Clause) clause;
                if (formula.size() == 1) {
                    for (ILogicFormula f : formula) {
                        simplified.add(f);
                    }
                } else {
                    simplified.add(formula.simplify());
                }
            } else simplified.add(clause);
        }
        return simplified;
    }

    public Clause negation() throws FormulaError {
        Clause newC = new Clause();
        newC.type = this.type.neg();
        for (ILogicFormula l : elements) {
            newC.add(l.negation());
        }
        return newC;
    }

    /***
     * shallow copy, do not use
     * @return
     */
    public Clause clone() {
        Clause clonedClause;
        try {
            clonedClause = (Clause) super.clone();
        } catch (CloneNotSupportedException e) {
            clonedClause = new Clause(this);
        }
        return clonedClause;
    }

    @Override
    public int hashCode() {
        return 31 * elements.hashCode() + type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Clause other = (Clause) obj;
        if (this.isEmpty() && other.isEmpty()) {
            return true;
        } else if (this.type == other.type) {
            return this.elements.equals(other.elements);
        }
        return false;
    }

    private boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public String toString(){
        ArrayList<String> clauseStrs = new ArrayList<>(this.elements.size());
        for (ILogicFormula clause : elements) {
            clauseStrs.add(clause.toString());
        }
        Collections.sort(clauseStrs);  // return consistent strings
        return "(" + String.join(this.type.getSymble(), clauseStrs) + ")";
    }

    public Iterator<ILogicFormula> iterator() {
        return this.elements.iterator();
    }

    public Clause toCNF() throws FormulaError {
        return convertToCNF(this);
    }

    public Boolean isCNF() {
        return isCNF(this);
    }

    public HashSet<ILogicFormula> getElements() {
        return elements;
    }
}
