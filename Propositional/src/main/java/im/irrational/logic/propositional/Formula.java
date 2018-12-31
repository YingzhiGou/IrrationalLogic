package im.irrational.logic.propositional;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Formula implements ILogicFormula, Iterable<ILogicFormula> {
    HashSet<ILogicFormula> clauses;
    eClauseType type;

    public Formula(){
        this(eClauseType.DISJUNCTIVE);
    }

    public Formula(final Formula e){
        this();
        type = e.type;
        for (ILogicFormula l : e.clauses){
            clauses.add(l.clone());
        }
    }

    public Formula(eClauseType type) {
        this.type = type;
        clauses = new HashSet<ILogicFormula>();
    }

    public Formula(eClauseType type, ILogicFormula... clauses) {
        this.type = type;
        this.clauses = new HashSet<>();
        for (ILogicFormula l : clauses) {
            this.clauses.add(l.clone());
        }
    }

    public int size(){
        return clauses.size();
    }

    /**
     * testing if the formula is CNF
     *
     * @return true if the formula is CNF
     */
    static Boolean isCNF(Formula formula) {
        if (formula.type == eClauseType.CONJUNCTIVE) {
            for (ILogicFormula clause : formula.clauses) {
                if (clause instanceof Formula) {
                    Formula f = (Formula) clause;
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

    public static Formula convertToCNF(final Formula c) throws FormulaError{
        if (c == null){
            return null;
        } else if (c.size() == 0){
            Formula newFormula = new Formula(eClauseType.CONJUNCTIVE);
            return newFormula;
        } else if (c.size() == 1){
            Formula newClause = new Formula(eClauseType.CONJUNCTIVE);
            newClause.addAll(c);
            return newClause;
        } else if (c.type == eClauseType.CONJUNCTIVE){
            /*
             If φ has the form P ^ Q, then:
			 CONVERT(P) must have the form P1 ^ P2 ^ ... ^ Pm, and
			 CONVERT(Q) must have the form Q1 ^ Q2 ^ ... ^ Qn,
			 where all the Pi and Qi are disjunctions of clauses.
			 So return P1 ^ P2 ^ ... ^ Pm ^ Q1 ^ Q2 ^ ... ^ Qn.
            */
            Formula newClause = new Formula(eClauseType.CONJUNCTIVE);
            newClause.addAll(c);
            return newClause;
        } else if (c.type == eClauseType.DISJUNCTIVE){
            /*
             If φ has the form P v Q, then:
			 CONVERT(P) must have the form P1 ^ P2 ^ ... ^ Pm, and
			 CONVERT(Q) must have the form Q1 ^ Q2 ^ ... ^ Qn,
			 where all the Pi and Qi are dijunctions of clauses.
			 So we need a CNF formula equivalent to
			 (P1 ^ P2 ^ ... ^ Pm) v (Q1 ^ Q2 ^ ... ^ Qn).
			 So return (P1 v Q1) ^ (P1 v Q2) ^ ... ^ (P1 v Qn)
			 ^ (P2 v Q1) ^ (P2 v Q2) ^ ... ^ (P2 v Qn)
			 ...
			 ^ (Pm v Q1) ^ (Pm v Q2) ^ ... ^ (Pm v Qn)
			 */
            Formula disjunctive = new Formula(c);
            while(disjunctive.size()>2){
                Formula tmp = new Formula(eClauseType.DISJUNCTIVE);
                tmp.addAll(disjunctive);
                disjunctive = convertToCNF(tmp);
            }
            //todo incomplete implementation!!!
        }
        return null;
    }

    public boolean contains(final Object other){
        return clauses.contains(other);
    }

    public boolean containsAll(final Collection<?> other) {
        return clauses.containsAll(other);
    }

    public eClauseType getType(){
        return type;
    }

    public void setType(eClauseType t){
        this.type = t;
    }

    public void add(final ILogicFormula clause) throws FormulaError {
        if (this.getClass() == clause.getClass()){
            Formula f = (Formula) clause;
            if (f.type == this.type){
                this.addAll(f.clone());
            } else if(f.size() > 0){
                this.clauses.add(f.clone());
            }
        }
        else{
            this.clauses.add(clause.clone());
        }
    }

    private void addAll(final Iterable<ILogicFormula> c) throws FormulaError {
        for (ILogicFormula clause : c) {
            if (clause instanceof Literal) {
                this.add(clause);
            } else if (clause instanceof Formula) {
                this.add(convertToCNF((Formula) clause));
            } else {
                throw new FormulaError(String.format("Unknown logic element type: %s", clause.getClass().getName()));
            }
        }
    }

    public Formula negation() throws FormulaError {
        Formula newC = new Formula();
        newC.type = type.neg();
        for(ILogicFormula l : clauses){
            newC.add(l.negation());
        }
        return newC;
    }

    public Formula clone() {
        return new Formula(this);
    }

    @Override
    public int hashCode() {
        return 31 * clauses.hashCode() + type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Formula other = (Formula) obj;
        if (this.type == other.type) {
            return this.clauses.equals(other.clauses);
        }
        return false;
    }

    @Override
    public String toString(){
        ArrayList<String> clauseStrs = new ArrayList<>(this.clauses.size());
        for (ILogicFormula clause : clauses){
            clauseStrs.add(clause.toString());
        }
        Collections.sort(clauseStrs);  // return consistent strings
        return "(" + String.join(this.type.getSymble(), clauseStrs) + ")";
    }

    @NotNull
    public Iterator<ILogicFormula> iterator() {
        return this.clauses.iterator();
    }

    public Formula toCNF() throws FormulaError {
        return convertToCNF(this);
    }

    public Boolean isCNF() {
        return isCNF(this);
    }
}
