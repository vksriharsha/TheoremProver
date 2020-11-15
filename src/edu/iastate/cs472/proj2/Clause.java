package edu.iastate.cs472.proj2;

import java.util.LinkedList;

public class Clause {
    private LinkedList<Literal> literals;

    public LinkedList<Literal> getLiterals() {
        return literals;
    }

    public void setLiterals(LinkedList<Literal> literals) {
        this.literals = literals;
    }

    @Override
    public String toString() {
        String clause = "";
        LinkedList<Literal> literals = this.getLiterals();

        int i=0;
        clause += "(";
        for(Literal l : literals){

            clause += l.toString();
            if(i != literals.size()-1){
                clause += " || ";
            }
            i++;
        }
        clause += ")";

        return clause.trim();
    }
}
