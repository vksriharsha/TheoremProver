package edu.iastate.cs472.proj2;

/**
 * @author Kumara Sri Harsha Vajjhala (harshavk@iastate.edu)
 */

import java.util.LinkedList;
import java.util.Objects;

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

        for(Literal l : literals){

            clause += l.toString();
            if(i != literals.size()-1){
                clause += " || ";
            }
            i++;
        }

        return clause.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        return Objects.equals(literals, clause.literals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals);
    }
}
