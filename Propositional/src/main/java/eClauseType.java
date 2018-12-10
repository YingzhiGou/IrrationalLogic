package im.irrational.logic.propositional;

public enum eClauseType {
    CONJUNCTIVE {
        @Override
        public String getSymble(){
            return "&";
        }
        @Override
        public eClauseType neg(){
            return DISJUNCTIVE;
        }
    },
    DISJUNCTIVE {
        @Override
        public eClauseType neg(){
            return CONJUNCTIVE;
        }
        @Override
        public String getSymble(){
            return "|";
        }
    };

    public eClauseType neg(){
        return values()[ordinal()+1];
    }

    public abstract String getSymble();
}
