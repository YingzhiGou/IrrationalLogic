package im.irrational.logic.propositional;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Clause implements ILogicFormula, Iterable<ILogicFormula> {
    HashSet<ILogicFormula> elements;
    eClauseType type;

    public Clause() {
        this(eClauseType.DISJUNCTIVE);
    }

    public Clause(final Clause e) {
        this();
        type = e.type;
        for (ILogicFormula l : e.elements) {
            elements.add(l.clone());
        }
    }

    public Clause(eClauseType type) {
        this.type = type;
        elements = new HashSet<ILogicFormula>();
    }

    public Clause(eClauseType type, ILogicFormula... elements) {
        this.type = type;
        this.elements = new HashSet<>();
        for (ILogicFormula l : elements) {
            this.elements.add(l.clone());
        }
    }

    /**
     * testing if the formula is CNF
     *
     * @return true if the formula is CNF
     */
    static Boolean isCNF(Clause formula) {
        if (formula.type == eClauseType.CONJUNCTIVE) {
            for (ILogicFormula clause : formula.elements) {
                if (clause instanceof Clause) {
                    Clause f = (Clause) clause;
                    if (f.type == eClauseType.DISJUNCTIVE) {
                        for (ILogicFormula literal : f) {
                            if (literal instanceof Literal) {
                            } else {
                                return false;
                            }
                        }
                        return true;
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
        Clause cnf = new Clause(eClauseType.CONJUNCTIVE);
        if (c == null){
            return null;
        } else if (c.size() == 0){
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

    public boolean contains(final Object other){
        return elements.contains(other);
    }

    public boolean containsAll(final Collection<?> other) {
        return elements.containsAll(other);
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
    public Clause simplified() throws FormulaError {
        Clause simplified = new Clause(this.getType());
        for (ILogicFormula clause : this) {
            if (clause instanceof Clause) {
                Clause formula = (Clause) clause;
                if (formula.size() == 1) {
                    for (ILogicFormula f : formula) {
                        simplified.add(f);
                    }
                } else {
                    simplified.add(formula.simplified());
                }
            } else simplified.add(clause);
        }
        return simplified;
    }

    public Clause negation() throws FormulaError {
        Clause newC = new Clause();
        newC.type = type.neg();
        for (ILogicFormula l : elements) {
            newC.add(l.negation());
        }
        return newC;
    }

    public Clause clone() {
        return new Clause(this);
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
        if (this.type == other.type) {
            return this.elements.equals(other.elements);
        }
        return false;
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

    @NotNull
    public Iterator<ILogicFormula> iterator() {
        return this.elements.iterator();
    }

    public Clause toCNF() throws FormulaError {
        return convertToCNF(this);
    }

    public Boolean isCNF() {
        return isCNF(this);
    }
}
