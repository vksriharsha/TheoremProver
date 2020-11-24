package edu.iastate.cs472.proj2;

/**
 * @author Kumara Sri Harsha Vajjhala (harshavk@iastate.edu)
 */

import java.util.LinkedList;

public class ConjunctiveNormalForm {

    private LinkedList<Clause> clauses;

    public LinkedList<Clause> getClauses() {
        return clauses;
    }

    public void setClauses(LinkedList<Clause> clauses) {
        this.clauses = clauses;
    }

    @Override
    public String toString() {
        String cnf = "";
        LinkedList<Clause> clauses = this.getClauses();

        int i=0;
        for(Clause c : clauses){
            cnf += c.toString();
            if(i != clauses.size()-1)
                cnf += "\n";

            i++;
        }

        return cnf.trim();
    }
}
