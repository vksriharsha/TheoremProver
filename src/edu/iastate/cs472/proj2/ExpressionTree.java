package edu.iastate.cs472.proj2;

/**
 * @author Kumara Sri Harsha Vajjhala (harshavk@iastate.edu)
 */

import java.util.LinkedList;

public class ExpressionTree {

    private String element;
    private ExpressionTree leftNode;
    private ExpressionTree rightNode;
    private ConjunctiveNormalForm cnf;

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public ExpressionTree getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(ExpressionTree leftNode) {
        this.leftNode = leftNode;
    }

    public ExpressionTree getRightNode() {
        return rightNode;
    }

    public void setRightNode(ExpressionTree rightNode) {
        this.rightNode = rightNode;
    }

    public ConjunctiveNormalForm getCnf() {
        return cnf;
    }

    public void setCnf(ConjunctiveNormalForm cnf) {
        this.cnf = cnf;
    }

    @Override
    public String toString() {
        LinkedList<Clause> clauses = this.getCnf().getClauses();
        String cnfStr = "";

        for(Clause c : clauses){
            cnfStr += c.toString();
            cnfStr += "\n";
        }

        return cnfStr.trim();
    }
}
