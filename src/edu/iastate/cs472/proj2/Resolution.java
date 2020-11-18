package edu.iastate.cs472.proj2;

import java.io.File;
import java.util.*;

public class Resolution {

    private CNFBuilder cnfBuilder;
    List<String> sentencesToProve;

    public Resolution(File file) {
        cnfBuilder = new CNFBuilder(file);

        cnfBuilder.buildCNFForAll();

        sentencesToProve = cnfBuilder.getSyntaxParser().getTheoremReader().getSentencesToProve();

        for (String sentence : sentencesToProve) {
            cnfBuilder.getSyntaxParser().tokenizeClause("~(" + sentence + ")");
        }

    }


    public boolean PL_RESOLUTION(List<ExpressionTree> KB, ExpressionTree alpha) {

        HashSet<Clause> kbClauses = new HashSet<>();
        LinkedList<Clause> KBList = new LinkedList<>();

        for (ExpressionTree kbTree : KB) {
            LinkedList currentClauses = kbTree.getCnf().getClauses();
            kbClauses.addAll(currentClauses);
            KBList.addAll(currentClauses);

        }
        LinkedList<Clause> alphaClauses = alpha.getCnf().getClauses();
        kbClauses.addAll(alphaClauses);

        HashSet<Clause> newClauses = new HashSet<>();


        Clause prevResolved = alphaClauses.get(0);

        newClauses.add(prevResolved);

        while (true) {

            HashSet<Clause> fresh = new HashSet<>();

            for (Clause Ci : kbClauses) {
                for (Clause Cj : newClauses) {
                    HashSet<Clause> resolvent = PL_RESOLVE(Ci, Cj);
                    if (resolvent.size() == 1) {
                        for (Clause r : resolvent) {

                            if (r.getLiterals().size() == 0) return true;
                        }

                        fresh.addAll(resolvent);
                    }
                }
            }

            System.out.println("New Clauses : " + newClauses);
            System.out.println("Fresh : " + fresh);
            kbClauses.addAll(newClauses);

            if (kbClauses.containsAll(fresh)) return false;

            newClauses.clear();
            newClauses.addAll(fresh);

            fresh.clear();
        }


    }


    public HashSet<Clause> PL_RESOLVE(Clause Ci, Clause Cj) {
        LinkedList<Literal> Ci_Literals = new LinkedList<>();

        for (Literal l : Ci.getLiterals()) {
            Ci_Literals.add(l);
        }

        LinkedList<Literal> Cj_Literals = new LinkedList<>();

        for (Literal l2 : Cj.getLiterals()) {
            Cj_Literals.add(l2);
        }
        Literal[] Ci_Arr = Ci_Literals.toArray(new Literal[Ci_Literals.size()]);
        Literal[] Cj_Arr = Cj_Literals.toArray(new Literal[Cj_Literals.size()]);

        boolean resolved = false;

        for (Literal li : Ci_Arr) {
            for (Literal lj : Cj_Arr) {

                if (("~" + li.getName()).equals(lj.getName()) || li.getName().equals("~" + lj.getName())) {
                    if (!resolved) {
                        Ci_Literals.remove(li);
                        Cj_Literals.remove(lj);
                        resolved = true;
                    }
                }
            }
        }

        if (resolved) {

            for (Literal li : Ci_Arr) {
                for (Literal lj : Cj_Arr) {

                    if (li.getName().equals(lj.getName())) {
                        Cj_Literals.remove(lj);
                    }
                }
            }
        }

        LinkedList<Literal> combinedLiterals = new LinkedList<>();
        combinedLiterals.addAll(Ci_Literals);
        combinedLiterals.addAll(Cj_Literals);

        Clause outputClause = new Clause();
        outputClause.setLiterals(combinedLiterals);

        HashSet<Clause> outputSet = new HashSet<>();
        if (resolved) {
            outputSet.add(outputClause);

        } else {
            outputSet.add(Ci);
            outputSet.add(Cj);
        }

        return outputSet;
    }


//    public boolean subset(HashSet<Clause> superset, HashSet subset ){
//
//
//    }

    public void printKnowledgeBase() {

        System.out.println("knowledge base in clauses:");
        for (ExpressionTree e : cnfBuilder.getExpressionTrees()) {
            System.out.println(e.getCnf());
            System.out.println();
        }
    }


    public static void main(String[] args) {
        Resolution res = new Resolution(new File("/Users/harshavk/Desktop/Docs/p1.txt"));

        res.printKnowledgeBase();

        int i = 0;
        for (String sentence : res.sentencesToProve) {
            System.out.println("****************");
            System.out.println("Goal sentence " + (i + 1) + ":");
            System.out.println();
            System.out.println(sentence);
            System.out.println("****************");

            ExpressionTree sentenceToProveExprTree = res.cnfBuilder.getSyntaxParser()
                    .getExpressionTree(res.cnfBuilder.getSyntaxParser().tokenizeClause("~(" + sentence + ")"));

            res.cnfBuilder.buildCNFBottomUp(sentenceToProveExprTree);

            System.out.println();
            System.out.println("Negated goal in clauses:");
            System.out.println();
            System.out.println(sentenceToProveExprTree.getCnf());


            boolean isEntails = res.PL_RESOLUTION(res.cnfBuilder.getExpressionTrees(), sentenceToProveExprTree);

            System.out.println(isEntails);
            System.out.println();


        }


    }
}
