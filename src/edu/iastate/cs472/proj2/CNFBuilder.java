package edu.iastate.cs472.proj2;

import sun.awt.image.ImageWatched;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CNFBuilder {

    private SyntaxParser syntaxParser;
    private List<ExpressionTree> expressionTrees;

    private static final String AND = "&&";
    private static final String OR = "||";
    private static final String NOT = "~";
    private static final String IMPL = "=>";
    private static final String DIMPL = "<=>";
    private static final String LPAR = "(";
    private static final String RPAR = ")";

    public CNFBuilder(File file) {
        syntaxParser = new SyntaxParser(file);
        expressionTrees = syntaxParser.getExpressionTrees();

        for(ExpressionTree et : expressionTrees){
            buildCNFBottomUp(et);
        }

    }

    public void buildCNFBottomUp(ExpressionTree node) {

        if (node.getLeftNode() != null) {
            buildCNFBottomUp(node.getLeftNode());
        }
        if (node.getRightNode() != null) {
            buildCNFBottomUp(node.getRightNode());
        }

        if (node.getElement().equals(AND)) {

            ConjunctiveNormalForm leftNodeCNF = node.getLeftNode().getCnf();
            ConjunctiveNormalForm rightNodeCNF = node.getRightNode().getCnf();

            ConjunctiveNormalForm currentNodeCNF = applyANDClause(leftNodeCNF, rightNodeCNF);

            node.setCnf(currentNodeCNF);

        } else if (node.getElement().equals(OR)) {

            ConjunctiveNormalForm leftCNF = node.getLeftNode().getCnf();
            ConjunctiveNormalForm rightCNF = node.getRightNode().getCnf();

            ConjunctiveNormalForm currentNodeCNF = applyORClause(leftCNF, rightCNF);

            node.setCnf(currentNodeCNF);
        } else if (node.getElement().equals(NOT)) {

            ConjunctiveNormalForm rightNodeCNF = node.getRightNode().getCnf();
            ConjunctiveNormalForm currentNodeCNF = applyNOTClause(rightNodeCNF);

            node.setCnf(currentNodeCNF);

        } else if (node.getElement().equals(IMPL)) {

            ConjunctiveNormalForm leftCNF = node.getLeftNode().getCnf();
            ConjunctiveNormalForm rightCNF = node.getRightNode().getCnf();

            ConjunctiveNormalForm leftCNFNegation = applyNOTClause(leftCNF);

            ConjunctiveNormalForm currentNodeCNF = applyORClause(leftCNFNegation, rightCNF);

            node.setCnf(currentNodeCNF);

        } else if (node.getElement().equals(DIMPL)) {

            ConjunctiveNormalForm leftCNF = node.getLeftNode().getCnf();
            ConjunctiveNormalForm rightCNF = node.getRightNode().getCnf();

            ConjunctiveNormalForm leftCNFNegation = applyNOTClause(leftCNF);
            ConjunctiveNormalForm rightCNFNegation = applyNOTClause(rightCNF);

            ConjunctiveNormalForm leftCNFImpl = applyORClause(leftCNFNegation, rightCNF);
            ConjunctiveNormalForm rightCNFImpl = applyORClause(rightCNFNegation, leftCNF);

            ConjunctiveNormalForm currentNodeCNF = applyANDClause(leftCNFImpl, rightCNFImpl);

            node.setCnf(currentNodeCNF);

        } else {

            Literal currentNodeLiteral = new Literal();
            currentNodeLiteral.setName(node.getElement());

            LinkedList<Literal> literalsList = new LinkedList<>();
            literalsList.add(currentNodeLiteral);

            Clause currentNodeClause = new Clause();
            currentNodeClause.setLiterals(literalsList);

            LinkedList<Clause> clausesList = new LinkedList<>();
            clausesList.add(currentNodeClause);

            ConjunctiveNormalForm currentNodeCNF = new ConjunctiveNormalForm();
            currentNodeCNF.setClauses(clausesList);

            node.setCnf(currentNodeCNF);

        }

    }


    public ConjunctiveNormalForm applyANDClause(ConjunctiveNormalForm CNF1, ConjunctiveNormalForm CNF2) {

        ConjunctiveNormalForm currentNodeCNF = new ConjunctiveNormalForm();
        LinkedList<Clause> currentNodeLinkedList = new LinkedList<>();

        currentNodeLinkedList.addAll(CNF1.getClauses());
        currentNodeLinkedList.addAll(CNF2.getClauses());

        currentNodeCNF.setClauses(currentNodeLinkedList);

        return currentNodeCNF;
    }


    public ConjunctiveNormalForm applyORClause(ConjunctiveNormalForm CNF1, ConjunctiveNormalForm CNF2) {

        ConjunctiveNormalForm currentNodeCNF = new ConjunctiveNormalForm();
        LinkedList<Clause> currentNodeClauses = new LinkedList<>();

        LinkedList<Clause> leftNodeClauses = CNF1.getClauses();
        LinkedList<Clause> rightNodeClauses = CNF2.getClauses();

        for (Clause lc : leftNodeClauses) {
            for (Clause rc : rightNodeClauses) {
                LinkedList<Literal> combinedLiterals = new LinkedList<>();
                LinkedList<Literal> leftClauseLiterals = lc.getLiterals();
                LinkedList<Literal> rightClauseLiterals = rc.getLiterals();

                combinedLiterals.addAll(leftClauseLiterals);
                combinedLiterals.addAll(rightClauseLiterals);

                Clause combinedClause = new Clause();
                combinedClause.setLiterals(combinedLiterals);

                currentNodeClauses.add(combinedClause);

            }
        }

        currentNodeCNF.setClauses(currentNodeClauses);

        return currentNodeCNF;
    }


    public ConjunctiveNormalForm applyNOTClause(ConjunctiveNormalForm CNF) {

        ConjunctiveNormalForm currentNodeCNF = new ConjunctiveNormalForm();
        LinkedList<Clause> currentNodeClauses = new LinkedList<>();
        LinkedList<Clause> allClauses = CNF.getClauses();

        for (int i = 0; i < allClauses.get(0).getLiterals().size(); i++) {
            Literal clause1Literal = allClauses.get(0).getLiterals().get(i);
            Literal negatedClause1Literal = new Literal();

            if (clause1Literal.getName().startsWith(NOT)) {
                negatedClause1Literal.setName(clause1Literal.getName().substring(1));
            } else {
                negatedClause1Literal.setName(NOT + clause1Literal.getName());
            }

            if (allClauses.size()>1) {

                for (int j = 0; j < allClauses.get(1).getLiterals().size(); j++) {
                    Literal clause2Literal = allClauses.get(1).getLiterals().get(j);
                    Literal negatedClause2Literal = new Literal();


                    if (clause2Literal.getName().startsWith(NOT)) {
                        negatedClause2Literal.setName(clause2Literal.getName().substring(1));
                    } else {
                        negatedClause2Literal.setName(NOT + clause2Literal.getName());
                    }

                    if (allClauses.size() > 2) {

                        for (int k = 0; j < allClauses.get(2).getLiterals().size(); j++) {
                            Literal clause3Literal = allClauses.get(2).getLiterals().get(k);
                            Literal negatedClause3Literal = new Literal();

                            if (clause3Literal.getName().startsWith(NOT)) {
                                negatedClause3Literal.setName(clause3Literal.getName().substring(1));
                            } else {
                                negatedClause3Literal.setName(NOT + clause3Literal.getName());
                            }


                            LinkedList<Literal> currentClauseList = new LinkedList<>();
                            currentClauseList.add(negatedClause1Literal);
                            currentClauseList.add(negatedClause2Literal);
                            currentClauseList.add(negatedClause3Literal);

                            Clause currentClause = new Clause();
                            currentClause.setLiterals(currentClauseList);

                            currentNodeClauses.add(currentClause);

                        }


                    } else {

                        LinkedList<Literal> currentClauseList = new LinkedList<>();
                        currentClauseList.add(negatedClause1Literal);
                        currentClauseList.add(negatedClause2Literal);

                        Clause currentClause = new Clause();
                        currentClause.setLiterals(currentClauseList);

                        currentNodeClauses.add(currentClause);
                    }


                }

            } else {

                LinkedList<Literal> currentClauseList = new LinkedList<>();
                currentClauseList.add(negatedClause1Literal);

                Clause currentClause = new Clause();
                currentClause.setLiterals(currentClauseList);

                currentNodeClauses.add(currentClause);
            }

        }

        currentNodeCNF.setClauses(currentNodeClauses);

        return currentNodeCNF;

    }


//    public ConjunctiveNormalForm applyNOTClause2(ConjunctiveNormalForm CNF){
//
//        LinkedList<Clause> allClauses = CNF.getClauses();
//
//
//        return null;
//    }
//
//    public List<Clause> recursiveNOT(List<Clause> listOfClauses){
//
//
//        if(listOfClauses.size() == 1){
//
//            List<Clause> currentClausesList = new LinkedList<>();
//            for (int i = 0; i < listOfClauses.get(0).getLiterals().size(); i++) {
//
//                Literal clause1Literal = listOfClauses.get(0).getLiterals().get(i);
//                Literal negatedClause1Literal = new Literal();
//
//                if (clause1Literal.getName().startsWith(NOT)) {
//                    negatedClause1Literal.setName(clause1Literal.getName().substring(1));
//                } else {
//                    negatedClause1Literal.setName(NOT + clause1Literal.getName());
//                }
//
//                LinkedList<Literal> currentLiteralsList = new LinkedList<>();
//                currentLiteralsList.add(negatedClause1Literal);
//
//                Clause currentClause = new Clause();
//                currentClause.setLiterals(currentLiteralsList);
//
//                currentClausesList.add(currentClause);
//
//            }
//
//            return currentClausesList;
//        }
//        else{
//            Clause clause1 = listOfClauses.get(0);
//            Clause clause2 = listOfClauses.get(1);
//
//            for (int i = 0; i < clause1.getLiterals().size(); i++) {
//
//
//
//            }
//
//        }
//    }

    public static void main(String[] args) {

        CNFBuilder cb = new CNFBuilder(new File("/Users/harshavk/Desktop/Docs/p1.txt"));

        for(ExpressionTree e : cb.expressionTrees){
            System.out.println(e.getCnf());
            System.out.println();
        }

    }

}
