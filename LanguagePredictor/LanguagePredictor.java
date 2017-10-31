import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class LanguagePredictor
{
  //main method used to make the engVocab and freVocab maps
  public static void main(String[] args) 
  {
    HashMap<String, Integer> engVocab = readVocabulary("eng_vocab.txt");
    HashMap<String, Integer> freVocab = readVocabulary("fre_vocab.txt");
    classifyDocuments(engVocab, freVocab, "C:\\Users\\Lea\\Documents\\U0\\COMP 202\\test\\", 20);
  }
  
  //this method reads the text file and inputs everything into a HashMap
  public static HashMap<String,Integer> readVocabulary(String fileName) 
  {
    HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
    
    try
    {
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      
      String text;
      
      while((text=br.readLine())!=null)
      {
        //splits up the text by line
        String[] line = text.split("\n");
        //splits up each line by space because that is what separates each key (word) from its value (count number)
        
        String[] words = line[0].split(" ");
        
        //the key is the first part of the line
        String key = words[0];
        //need to cast the second part of the line into an integer
        int value = Integer.parseInt(words[1]);
        
        //adds the key and value to the HashMap
        wordCount.put(key, value);
      }
    }
    catch(FileNotFoundException e)
    {
      System.out.println("File was not found.");
    }
    catch(IOException e)
    {
      System.out.println("There was a problem reading the file.");
    }
    
    return wordCount;
  }
  
  //this method reads through a given file and looks at whether the words are in English or French then makes a decision
  public static void classifyDocuments(HashMap<String,Integer> engVocab, HashMap<String,Integer> freVocab, 
                                       String directory, int nFiles) 
  {
    for(int i=1; i<=nFiles; i++)
    {
      try
      {
        String fileName = directory + i + ".txt";
        
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        
        int engCounter=0;
        int freCounter=0;
        
        String line;
        
        while((line=br.readLine())!=null)
        {
          String[] words = line.split(" ");
          
          for(int j=0; j<words.length; j++)
          {
            words[j]=words[j].toLowerCase();
            words[j]=words[j].replaceAll("[^a-z]","");
            
            //checks to see if a word is in the engVocab map
            if(engVocab.containsKey(words[j]))
            {
              //if the words is also in freVocab and appears more often, then it is a French word
              if(freVocab.containsKey(words[j]) && engVocab.get(words[j])<freVocab.get(words[j]))
              {
                freCounter++;
              }
              //otherwise an English word
              else
              {
                engCounter++;
              }
            }
            else if(freVocab.containsKey(words[j]))
            {
              //don't need an if to see if the word was also seen in engVocab because it would have been detected above
              freCounter++;
            }
            
          }
        }
        
        //this is where the language of the text will be determined
        String  winner="";
        
        if(engCounter>=freCounter)
        {
          winner="English";
        }
        else
        {
          winner="French";
        }
        
        System.out.println(i + "\t" + "English: " + engCounter + "\t" + "French: " + freCounter + "\t" + "Decision: " + winner);
      }
      catch(FileNotFoundException e)
      {
        System.out.println("File not found.");
      }
      catch(IOException e)
      {
        System.out.println("There was a problem reading the file.");
      }
    }
  }
  
  /*****************************************************************************
    Put the output of classifyDocuments here, and a sentence to describe whether
    your program worked.
    
    1 English: 35 French: 2 Decision: English 
    2 English: 75 French: 8 Decision: English 
    3 English: 155 French: 4 Decision: English 
    4 English: 404 French: 9 Decision: English 
    5 English: 98 French: 11 Decision: English 
    6 English: 238 French: 10 Decision: English 
    7 English: 69 French: 1 Decision: English 
    8 English: 220 French: 16 Decision: English 
    9 English: 77 French: 5 Decision: English 
    10 English: 616 French: 37 Decision: English 
    11 English: 13 French: 436 Decision: French 
    12 English: 2 French: 173 Decision: French 
    13 English: 7 French: 59 Decision: French 
    14 English: 17 French: 258 Decision: French 
    15 English: 8 French: 155 Decision: French 
    16 English: 6 French: 125 Decision: French 
    17 English: 10 French: 250 Decision: French 
    18 English: 44 French: 470 Decision: French 
    19 English: 8 French: 175 Decision: French 
    20 English: 16 French: 213 Decision: French 
    
    According to the output above, my LanguagePredictor can correctly identify whether 
    a certain text is in English or French.
    
    
    
    
    *****************************************************************************/
}