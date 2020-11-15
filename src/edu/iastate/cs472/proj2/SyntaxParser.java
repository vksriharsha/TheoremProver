package edu.iastate.cs472.proj2;

import java.io.File;
import java.util.*;

public class SyntaxParser {

    private TheoremReader theoremReader;
    private List<List<String>> knowledgeBaseTokens;
    private Map<String, Integer> inputPrecedence;
    private Map<String, Integer> stackPrecedence;

    private static final String AND = "&&";
    private static final String OR = "||";
    private static final String NOT = "~";
    private static final String IMPL = "=>";
    private static final String DIMPL = "<=>";
    private static final String LPAR = "(";
    private static final String RPAR = ")";


    public SyntaxParser(File file) {
        theoremReader = new TheoremReader(file);
        knowledgeBaseTokens = new ArrayList<List<String>>();
        inputPrecedence = new HashMap<>();
        stackPrecedence = new HashMap<>();

        inputPrecedence.put(NOT, 5);
        inputPrecedence.put(AND, 4);
        inputPrecedence.put(OR, 3);
        inputPrecedence.put(IMPL, 2);
        inputPrecedence.put(DIMPL, 1);
        inputPrecedence.put(LPAR, 6);
        inputPrecedence.put(RPAR, 0);

        stackPrecedence.put(NOT, 5);
        stackPrecedence.put(AND, 4);
        stackPrecedence.put(OR, 3);
        stackPrecedence.put(IMPL, 2);
        stackPrecedence.put(DIMPL, 1);
        stackPrecedence.put(LPAR, -1);
        stackPrecedence.put(RPAR, 0);

        for (String s : theoremReader.getKnowledgeBase()) {
            knowledgeBaseTokens.add(tokenizeClause(s));
        }

        for(List<String> clause : knowledgeBaseTokens){
            System.out.print(clause + " : ");
            System.out.print(convertToPostfix(clause));
            System.out.println();
        }

    }



    public List<String> tokenizeClause(String clause) {

        char[] clauseChars = clause.toCharArray();
        List<String> tokens = new ArrayList<String>();

        String sequence = "";

        boolean prevCharAnd = false;
        boolean prevCharOr = false;
        boolean prevCharImpl = false;
        boolean prevCharDImpl = false;

        for (char c : clauseChars) {

            if (c == '(') {
                if (!sequence.equals("")) {
                    if(!sequence.trim().equals(""))
                        tokens.add(sequence.trim());
                    sequence = "";
                }
                tokens.add("(");
                prevCharAnd = false;
                prevCharOr = false;
                prevCharImpl = false;
                prevCharDImpl = false;


            } else if (c == ')') {

                if (!sequence.equals("")) {
                    if(!sequence.trim().equals(""))
                        tokens.add(sequence.trim());
                    sequence = "";
                }
                tokens.add(")");
                prevCharAnd = false;
                prevCharOr = false;
                prevCharImpl = false;
                prevCharDImpl = false;


            } else if (c == '~') {
                if (!sequence.equals("")) {
                    if(!sequence.trim().equals(""))
                        tokens.add(sequence.trim());
                    sequence = "";
                }

                tokens.add(NOT);
                prevCharAnd = false;
                prevCharOr = false;
                prevCharImpl = false;
                prevCharDImpl = false;


            } else if (c == '&') {
                if (!sequence.equals("")) {
                    if(!sequence.trim().equals(""))
                        tokens.add(sequence.trim());
                    sequence = "";
                }
                if (prevCharAnd) {
                    tokens.add(AND);
                    prevCharAnd = false;
                    prevCharOr = false;
                    prevCharImpl = false;
                    prevCharDImpl = false;

                }
                prevCharAnd = true;
                prevCharOr = false;
                prevCharImpl = false;
                prevCharDImpl = false;
            }
            else if (c == '|') {
                if (prevCharOr) {
                    if (!sequence.equals("")) {
                        if(!sequence.trim().equals(""))
                            tokens.add(sequence.trim());
                        sequence = "";
                    }

                    tokens.add(OR);
                    prevCharAnd = false;
                    prevCharOr = false;
                    prevCharImpl = false;
                    prevCharDImpl = false;

                }
                prevCharAnd = false;
                prevCharOr = true;
                prevCharImpl = false;
                prevCharDImpl = false;
            }
            else if (c == '<') {
                prevCharAnd = false;
                prevCharOr = false;
                prevCharImpl = false;
                prevCharDImpl = true;
            }

            else if (c == '=') {
                if (prevCharDImpl) {
                    prevCharAnd = false;
                    prevCharOr = false;
                    prevCharImpl = false;
                    prevCharDImpl = true;

                } else {
                    prevCharAnd = false;
                    prevCharOr = false;
                    prevCharImpl = true;
                    prevCharDImpl = true;
                }
            } else if (c == '>') {
                if (prevCharImpl) {

                    if (!sequence.equals("")) {
                        if(!sequence.trim().equals(""))
                            tokens.add(sequence.trim());
                        sequence = "";
                    }

                    tokens.add(IMPL);
                    prevCharAnd = false;
                    prevCharOr = false;
                    prevCharImpl = false;
                    prevCharDImpl = false;


                } else if (prevCharDImpl) {
                    if (!sequence.equals("")) {
                        if(!sequence.trim().equals(""))
                            tokens.add(sequence.trim());
                        sequence = "";
                    }

                    tokens.add(DIMPL);
                    prevCharAnd = false;
                    prevCharOr = false;
                    prevCharImpl = false;
                    prevCharDImpl = false;
                }

            } else {
                sequence += c;
            }


        }

        if (!sequence.equals("")) {
            if(!sequence.trim().equals(""))
                tokens.add(sequence.trim());
            sequence = "";
        }

        return tokens;
    }


    public List<String> convertToPostfix(List<String> infixClause){

        List<String> postfixClause = new ArrayList<String>();
        Stack<String> operatorStack = new Stack<>();

        for(String token : infixClause){

            if(token.equals(NOT) || token.equals(AND) || token.equals(OR)
            || token.equals(IMPL) || token.equals(DIMPL) || token.equals(LPAR) || token.equals(RPAR)){

                if(operatorStack.empty()){
                    operatorStack.push(token);
                }
                else{
                    String stackTop = operatorStack.peek();

                    if(stackPrecedence.get(stackTop) > inputPrecedence.get(token)){

                        while(!operatorStack.empty() && stackPrecedence.get(operatorStack.peek()) > inputPrecedence.get(token)) {
                            String topToken = operatorStack.pop();
                            postfixClause.add(topToken);

                        }
                        if(token == RPAR && operatorStack.peek() == LPAR){
                            operatorStack.pop();
                        }
                        else {
                            operatorStack.push(token);
                        }

                    }
                    else if(stackPrecedence.get(stackTop) == inputPrecedence.get(token)){
                        if(token == NOT && stackTop == NOT){
                            operatorStack.pop();
                        }
                        else{
                            while(!operatorStack.empty() && stackPrecedence.get(operatorStack.peek()) >= inputPrecedence.get(token)) {
                                String topToken = operatorStack.pop();
                                postfixClause.add(topToken);

                            }
                            operatorStack.push(token);
                        }
                    }
                    else{
                        if(token == RPAR && stackTop == LPAR){
                            operatorStack.pop();
                        }
                        else {
                            operatorStack.push(token);
                        }
                    }
                }
            }
            else{
                postfixClause.add(token);
            }

        }

        while(!operatorStack.empty()){
            String stackElement = operatorStack.pop();
            postfixClause.add(stackElement);
        }

        return postfixClause;
    }



    public static void main(String[] args) {
        SyntaxParser sp = new SyntaxParser(new File("/Users/harshavk/Desktop/Docs/p1.txt"));

    }

}
