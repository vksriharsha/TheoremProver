package edu.iastate.cs472.proj2;

/**
 * @author Kumara Sri Harsha Vajjhala (harshavk@iastate.edu)
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TheoremReader {

    private List<String> knowledgeBase;
    private List<String> sentencesToProve;

    public TheoremReader(File file){
        try {
            readTheoremFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addStringToKnowledgeBase(String newKnowledge){
        if(knowledgeBase == null){
            knowledgeBase = new ArrayList<String>();
        }
        knowledgeBase.add(newKnowledge);
    }

    public void addStringToSentencesToProve(String newSentenceToProve){
        if(sentencesToProve == null){
            sentencesToProve = new ArrayList<String>();
        }
        sentencesToProve.add(newSentenceToProve);
    }

    public List<String> getKnowledgeBase(){
        return this.knowledgeBase;
    }

    public List<String> getSentencesToProve(){
        return this.sentencesToProve;
    }

    public void readTheoremFile(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);

        boolean KBLines = false;
        boolean SPLines = false;
        boolean isPreviousLineEmpty = false;

        String KBconcatenatedLine = "";
        String SPconcatenatedLine = "";

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if(line.isEmpty()){
                if(isPreviousLineEmpty){
                    if(KBLines){
                        KBLines = false;
                    }
                }
                isPreviousLineEmpty = true;

                if(KBLines && !KBconcatenatedLine.equals("")){
                    addStringToKnowledgeBase(KBconcatenatedLine);
                    KBconcatenatedLine = "";
                }
                else if(SPLines && !SPconcatenatedLine.equals("")){
                    addStringToSentencesToProve(SPconcatenatedLine);
                    SPconcatenatedLine = "";
                }

            }
            else if(line.equals("Knowledge Base:")){
                KBLines = true;
                SPLines = false;
                isPreviousLineEmpty = false;
            }
            else if(line.equals("Prove the following sentences by refutation:")){
                SPLines = true;
                KBLines = false;
                isPreviousLineEmpty = false;
            }
            else{
                if(KBLines){
                    KBconcatenatedLine += line;
                }
                else if(SPLines){
                    SPconcatenatedLine += line;
                }
                isPreviousLineEmpty = false;
            }
        }

        if(!SPconcatenatedLine.equals("") && !SPconcatenatedLine.isEmpty()){
            addStringToSentencesToProve(SPconcatenatedLine);
        }

    }
    public static void main(String[] args) {


        File ProblemFile = new File("/Users/harshavk/Desktop/Docs/p1.txt");
        TheoremReader tr = new TheoremReader(ProblemFile);

        System.out.println(tr.getKnowledgeBase());
        System.out.println(tr.getSentencesToProve());


    }
}
