import java.util.*;
import java.io.*;

public class LanguageLearner
{
  //the main method is used to create the HashMaps of the English and French vocab words
  //and to write these HashMaps to a text file
  public static void main(String[] args) 
  {
    HashMap<String, Integer> engCounter = countWords("C:\\Users\\Lea\\Documents\\U0\\COMP 202\\train\\eng\\", 20);
    HashMap<String, Integer> freCounter = countWords("C:\\Users\\Lea\\Documents\\U0\\COMP 202\\train\\fre\\", 20);
    writeVocabulary(engCounter, "eng_vocab.txt");
    writeVocabulary(freCounter, "fre_vocab.txt");
  }
  
  //creates HashMap of each word in the files and how many times the word shows up
  public static HashMap<String, Integer> countWords(String directory, int nFiles) 
  {
    HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
    
    //there are no files starting in 0
    for(int i=1; i<=nFiles; i++)
    {
      try
      {
        String fileName = directory + i + ".txt";
        
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        
        String line;
        
        //want to read through the file until it is null
        while((line=br.readLine())!=null)
        {
          String[] words = line.split(" ");
          
          for(int j=0; j<words.length; j++)
          {
            words[j]=words[j].toLowerCase();
            //this will get rid of any characters not in the alphabet
            //only words will matter in determining if something is in French or English
            words[j]=words[j].replaceAll("[^a-z]","");
            //if the HashMap already contains the current word
            if(wordCount.containsKey(words[j]))
            {
              //will add 1 to the number of times the word has shown up
              //i.e. adds one to the value of the key
              wordCount.put(words[j], wordCount.get(words[j])+1);
            }
            else
            {
              //HashMap hasn't seen the word yet, the initial count is 1
              wordCount.put(words[j], 1);
            }
          }
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
    }
    
    return wordCount;
  }
 
  public static void writeVocabulary(HashMap<String, Integer> vocab, String fileName) 
  {
    //need to put the keys of the vocab HashMap in order to use the Collections.sort method
    List<String> keys = new ArrayList<String>(vocab.keySet());
    //sorts the keys of the vocab HashMap
    Collections.sort(keys);
    
    try
    {
      FileWriter fw = new FileWriter(fileName);
      BufferedWriter bw = new BufferedWriter(fw);
      
      //all the keys are sorted now
      for(int i=1; i<keys.size(); i++)
      {
        //need to run through the keys array and write it to the file
        //along with the corresponding value from the vocab HashMap
        bw.write(keys.get(i) + " " + vocab.get(keys.get(i)) + "\n");
      }
      
      bw.close();
    }
    catch(FileNotFoundException e)
    {
      System.out.println("File was not found.");
    }
    catch(IOException e)
    {
      System.out.println("There was a problem reading the file.");
    }
  }
}