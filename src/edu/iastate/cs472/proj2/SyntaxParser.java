package edu.iastate.cs472.proj2;

/**
 * @author Kumara Sri Harsha Vajjhala (harshavk@iastate.edu)
 */

import java.io.File;
import java.util.*;

public class SyntaxParser {

    private TheoremReader theoremReader;
    private List<List<String>> knowledgeBaseTokens;
    private Map<String, Integer> inputPrecedence;
    private Map<String, Integer> stackPrecedence;
    private List<ExpressionTree> expressionTrees;

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
        expressionTrees = new LinkedList<>();

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

        for (List<String> clause : knowledgeBaseTokens) {

            List<String> postfix = convertToPostfix(clause);
            ExpressionTree tree = convertPostfixToExpressionTree(postfix);

            this.expressionTrees.add(tree);
        }

    }


    public TheoremReader getTheoremReader() {
        return theoremReader;
    }

    public void setTheoremReader(TheoremReader theoremReader) {
        this.theoremReader = theoremReader;
    }

    public ExpressionTree getExpressionTree(List<String> clause){

        List<String> postfix = convertToPostfix(clause);
        ExpressionTree tree = convertPostfixToExpressionTree(postfix);

        return tree;
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
                    if (!sequence.trim().equals(""))
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
                    if (!sequence.trim().equals(""))
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
                    if (!sequence.trim().equals(""))
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
                    if (!sequence.trim().equals(""))
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
            } else if (c == '|') {
                if (prevCharOr) {
                    if (!sequence.equals("")) {
                        if (!sequence.trim().equals(""))
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
            } else if (c == '<') {
                prevCharAnd = false;
                prevCharOr = false;
                prevCharImpl = false;
                prevCharDImpl = true;
            } else if (c == '=') {
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
                        if (!sequence.trim().equals(""))
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
                        if (!sequence.trim().equals(""))
                            tokens.add(sequence.trim());
                        sequence = "";
                    }

                    tokens.add(DIMPL);
                    prevCharAnd = false;
                    prevCharOr = false;
                    prevCharImpl = false;
                    prevCharDImpl = false;
                }

            }
            else if(c == ' '){
                prevCharAnd = false;
                prevCharOr = false;
                prevCharImpl = false;
                prevCharDImpl = false;
            }
            else {
                sequence += c;
            }


        }

        if (!sequence.equals("")) {
            if (!sequence.trim().equals(""))
                tokens.add(sequence.trim());
            sequence = "";
        }

        return tokens;
    }


    public List<String> convertToPostfix(List<String> infixClause) {

        List<String> postfixClause = new ArrayList<String>();
        Stack<String> operatorStack = new Stack<>();


        for (String token : infixClause) {

            if (token.equals(NOT) || token.equals(AND) || token.equals(OR)
                    || token.equals(IMPL) || token.equals(DIMPL) || token.equals(LPAR) || token.equals(RPAR)) {

                if (operatorStack.empty()) {
                    operatorStack.push(token);
                } else {
                    String stackTop = operatorStack.peek();

                    if (stackPrecedence.get(stackTop) > inputPrecedence.get(token)) {

                        while (!operatorStack.empty() && stackPrecedence.get(operatorStack.peek()) > inputPrecedence.get(token)) {
                            String topToken = operatorStack.pop();
                            postfixClause.add(topToken);

                        }
                        if (token == RPAR && operatorStack.peek() == LPAR) {
                            operatorStack.pop();
                        } else {
                            operatorStack.push(token);
                        }

                    } else if (stackPrecedence.get(stackTop) == inputPrecedence.get(token)) {
                        if (token == NOT && stackTop == NOT) {
                            operatorStack.pop();
                        } else if(token == RPAR){
                            while (!operatorStack.empty() && stackPrecedence.get(operatorStack.peek()) >= inputPrecedence.get(token)) {
                                String topToken = operatorStack.pop();
                                postfixClause.add(topToken);

                            }
                            operatorStack.push(token);
                        }
                        else{
                            operatorStack.push(token);
                        }
                    } else {
                        if (token == RPAR && stackTop == LPAR) {
                            operatorStack.pop();
                        } else {
                            operatorStack.push(token);
                        }
                    }
                }
            } else {
                postfixClause.add(token);
            }

        }

        while (!operatorStack.empty()) {
            String stackElement = operatorStack.pop();
            postfixClause.add(stackElement);
        }

        return postfixClause;
    }


    public ExpressionTree convertPostfixToExpressionTree(List<String> postfixExpression) {

        Stack<ExpressionTree> operandStack = new Stack<>();
        ExpressionTree rootOfTree = null;


        for (String token : postfixExpression) {

            if (token.equals(AND) || token.equals(OR)
                    || token.equals(IMPL) || token.equals(DIMPL)) {

                ExpressionTree rightOperand = operandStack.pop();
                ExpressionTree leftOperand = operandStack.pop();

                ExpressionTree combinedExpression = new ExpressionTree();
                combinedExpression.setElement(token);
                combinedExpression.setLeftNode(leftOperand);
                combinedExpression.setRightNode(rightOperand);

                operandStack.push(combinedExpression);
                rootOfTree = combinedExpression;
            } else if (token.equals(NOT)) {
                ExpressionTree rightOperand = operandStack.pop();
                ExpressionTree combinedExpression = new ExpressionTree();

                combinedExpression.setElement(token);
                combinedExpression.setRightNode(rightOperand);

                operandStack.push(combinedExpression);
                rootOfTree = combinedExpression;
            } else {
                ExpressionTree operand = new ExpressionTree();
                operand.setElement(token);

                operandStack.push(operand);
                rootOfTree = operand;
            }
        }

        return rootOfTree;
    }

    public void printTreeInfix(ExpressionTree node) {

        if (node.getLeftNode() != null) {
            printTreeInfix(node.getLeftNode());
        }
        System.out.print(node.getElement() + ", ");
        if (node.getRightNode() != null) {
            printTreeInfix(node.getRightNode());
        }

    }

    public List<ExpressionTree> getExpressionTrees() {
        return expressionTrees;
    }

    public void setExpressionTrees(List<ExpressionTree> expressionTrees) {
        this.expressionTrees = expressionTrees;
    }

    public static void main(String[] args) {
        SyntaxParser sp = new SyntaxParser(new File("/Users/harshavk/Desktop/Docs/p1.txt"));

    }

}
