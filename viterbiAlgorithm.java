/**
 * 
 * @author Lea Collin
 *
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;

public class viterbiAlgorithm {
	static double maximum = 0;
	static double fraction = 0;
	
	//these were used to calculate the length distributions of each region
	//histograms were created based on the frequency of each region length observed
	static List<Integer> mixedDistribution = new ArrayList<Integer>();
	static List<Integer> hydrophobicDistribution = new ArrayList<Integer>();
	static List<Integer> hydrophilicDistribution = new ArrayList<Integer>();
	
	//these were used to calculate the total  length of each region to be used to determine the different AA frequencies in each region
	static double totalHydropho = 0;
	static double totalHydrophil = 0;
	static double totalMixed = 0;
	
	//these hashmaps were used to keep track of the counts of each AA labeled as being part of a certain region
	//then the count was divided by the total length of the region to get the frequencies
	static Map<String, Double> mixedFrequencies = new HashMap<String, Double>();
	static Map<String, Double> hydrophobicFrequencies = new HashMap<String, Double>();
	static Map<String, Double> hydrophilicFrequencies = new HashMap<String, Double>();
	public static void main(String[] args) throws IOException {
		
		/*
		 * PLEASE NOTE: file name should be given as a command-line argument
		 */
		
		//for all matrices:
		//row 1 = hydrophobic (Hp)
		//row 2 = hydrophilic (Hl)
		//row 3 = mixed (M)
		
		//all probabilities are in log base 10 so that probabilities do not all become zero with long protein sequences
		
		//initial-state probability matrix
		double initial = Math.log10(1.0/3.0);
		//transition-state probability matrix
		//col 1 = hydrophobic (Hp)
		//col 2 = hydrophilic (Hl)
		//col 3 = mixed (M)
		double [][] transition = {{Math.log10(4.0/5.0), Math.log10(1.0/25.0), Math.log10(4.0/25.0)}, 
				                 {Math.log10(3.0/80.0), Math.log10(7.0/8.0), Math.log10(7.0/80.0)}, 
				                 {Math.log10(1.0/14.0), Math.log10(1.0/14.0), Math.log10(6.0/7.0)}};
				
	    //emission probability matrix
		double [][] emission = {{Math.log10(3.0/40.0), Math.log10(1.0/30.0)}, 
								{Math.log10(1.0/40.0), Math.log10(1.0/15.0)}, 
								{Math.log10(1.0/20.0), Math.log10(1.0/20.0)}};
		
		//arraylist to see if current AA is hydrophobic or not using ArrayList.contains()
		ArrayList<String> hydrophobic = new ArrayList<String>();
		hydrophobic.add("A");
		hydrophobic.add("V");
		hydrophobic.add("I");
		hydrophobic.add("L");
		hydrophobic.add("M");
		hydrophobic.add("F");
		hydrophobic.add("Y");
		hydrophobic.add("W");
		
		//reading from file
		String filename = args[0];
		ArrayList<String> sequences = readFile(filename);
		
		String [] AA = {"A", "C", "D", "E", "F", "G", "H", "I" ,"K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "Y"};
		
		//this is where the viterbi method is called and all of the output is written to a file
		try{
			PrintWriter writer = new PrintWriter("analysis.txt", "UTF-8");
			for(int i = 0; i < sequences.size(); i++){
				String seq = sequences.get(i);
				if(i%2 == 0){
					writer.println("Protein name:");
					writer.println(seq + "\n");
					writer.println();
				}
				else{
					writer.println("Amino acid sequence:");
					writer.println(seq + "\n");
					writer.println();
					writer.println("Region analysis:");
					writer.println(viterbi(initial, transition, emission, seq, hydrophobic) + "\n");
					writer.println();
					writer.println("Length of hydrophobic region: " + maximum);
					writer.println("Fraction of mixed AA: " + fraction);
				}
			}
			
			//now that we have the total count of each AA in each region, simply divide by total length of respective region 
			//to get the frequency of the AA in that particular region
			for(int i=0; i<AA.length; i++){
				mixedFrequencies.put(AA[i], mixedFrequencies.get(AA[i])/totalMixed);
				hydrophilicFrequencies.put(AA[i], hydrophilicFrequencies.get(AA[i])/totalHydrophil);
				hydrophobicFrequencies.put(AA[i], hydrophobicFrequencies.get(AA[i])/totalHydropho);
			}
			
			Collections.sort(hydrophobicDistribution);
			Collections.sort(hydrophilicDistribution);
			Collections.sort(mixedDistribution);
			writer.println("Hydrophobic distribution: " + hydrophobicDistribution);
			writer.println("Hydrophilic distribtuion: " + hydrophilicDistribution);
			writer.println("Mixed distribution: " + mixedDistribution + "\n");
			
			writer.println("Mixed frequencies: " + mixedFrequencies);
			writer.println("Hydrophobic frequencies: " + hydrophobicFrequencies);
			writer.println("Hydrophilic frequencies: " + hydrophilicFrequencies);
			writer.close();
		} catch(Exception e){
			System.out.println("Error");
		}
	}
	
	public static String viterbi(double initial, double [][] transition, double [][] emission, String sequence, ArrayList<String> hydrophobic){
		int x = 3;
		int y = sequence.length();
		double [][] scores = new double[x][y];
		char [][] traceback = new char[x][y];
		//row 1: hydrophobic (Hp)
		//row 2: hydrophilic (Hl)
		//row 3: mixed (M)
		
		//this will be the returned string with the path indicating whether each AA is in a hydrophobic, hydrophilic, or mixed region
		String path = "";
		
		
		
		//initialization of score matrix
		for(int i=0; i<x; i++){
			if (hydrophobic.contains(Character.toString(sequence.charAt(0)))){
				scores[i][0] = initial + emission[i][0];
			}
			else{
				scores[i][0] = initial + emission[i][1];
			}
		}
		//filling in score matrix according to Viterbi algorithm
		for(int i=1; i<y; i++){
			for(int j=0; j<x; j++){
				//Hp will calculate the score of being in state j (which is either Hp (j=0) Hl (j=1) or M (j=2) given previous state is Hp
				//Hl will calculate score of being in state j given previous state is Hl
				//M will calculate score of being in state j given previous state is M
				double maxScore = 0;
				double Hp = scores[0][i-1] + (transition[0][j]);
				double Hl = scores[1][i-1] + (transition[1][j]);
				double M = scores[2][i-1] + (transition[2][j]);
				
				if(Hp > Hl && Hp > M){
					maxScore = Hp;
					traceback[j][i] = 'H';
				}
				else if(Hl > Hp && Hl > M){
					maxScore = Hl;
					traceback[j][i] = 'L';
				}
				else{
					maxScore = M;
					traceback[j][i] = 'M';
				}
				
				if (hydrophobic.contains(Character.toString(sequence.charAt(i)))){
					scores[j][i] = maxScore + (emission[j][0]);
				}
				else{
					scores[j][i] = maxScore + (emission[j][1]);
				}
			}
		}
		
		double hp = scores[0][y-1];
		double hl = scores[1][y-1];
		double m = scores[2][y-1];
		
		//these were used to calculate the longest hydrophobic region and the largest fraction of AA being annotated as mixed
		double hydropho = 0;
		double mixed = 0;
		
		int hydrophilLen = 0;
		int hydrophoLen = 0;
		int mixedLen = 0;
		
		//traceback procedure
		int row = -1;
		int col = y-1;
		if(hp > hl && hp > m){
			path = "H";
			row = 0;
			hydropho++;
			hydrophoLen++;
			totalHydropho++;
			
			try{
				String a = sequence.charAt(y-1) + "";
				hydrophobicFrequencies.put(a, hydrophobicFrequencies.get(a) + 1);
			}catch(NullPointerException i){
				String a = sequence.charAt(y-1) + "";
				hydrophobicFrequencies.put(a, 1.0);
			}
		}
		else if(hl > hp && hl > m){
			path = "L";
			row = 1;
			hydrophilLen++;
			totalHydrophil++;
			
			try{
				String a = sequence.charAt(y-1) + "";
				hydrophilicFrequencies.put(a, hydrophilicFrequencies.get(a) + 1);
			}catch(NullPointerException i){
				String a = sequence.charAt(y-1) + "";
				hydrophilicFrequencies.put(a, 1.0);
			}
		}
		else{
			path = "M";
			row = 2;
			mixed++;
			mixedLen++;
			totalMixed++;
			
			try{
				String a = sequence.charAt(y-1) + "";
				mixedFrequencies.put(a, mixedFrequencies.get(a) + 1);
			}catch(NullPointerException i){
				String a = sequence.charAt(y-1) + "";
				mixedFrequencies.put(a, 1.0);
			}
		}
		
		while(col>0){
			char temp = traceback[row][col];
			path = temp + path;
			if(temp == 'H'){
				row = 0;
				hydropho++;
				hydrophoLen++;
				totalHydropho++;
				
				try{
					String a = sequence.charAt(col-1) + "";
					hydrophobicFrequencies.put(a, hydrophobicFrequencies.get(a) + 1);
				}catch(NullPointerException i){
					String a = sequence.charAt(col-1) + "";
					hydrophobicFrequencies.put(a, 1.0);
				}
				
				if(hydrophilLen > mixedLen && hydrophilLen != 0){
					hydrophilicDistribution.add(hydrophilLen);
					hydrophilLen = 0;
				}
				else if (mixedLen != 0){
					mixedDistribution.add(mixedLen);
					mixedLen = 0;
				}
				
			}
			else if(temp == 'L'){
				row = 1;
				hydrophilLen++;
				totalHydrophil++;
				//use this line when determining longest hydrophobic region
				if (hydropho > maximum){
					maximum = hydropho;
				}
				
				try{
					String a = sequence.charAt(col-1) + "";
					hydrophilicFrequencies.put(a, hydrophilicFrequencies.get(a) + 1);
				}catch(NullPointerException i){
					String a = sequence.charAt(col-1) + "";
					hydrophilicFrequencies.put(a, 1.0);
				}
				
				if(hydrophoLen > mixedLen && hydrophoLen != 0){
					hydrophobicDistribution.add(hydrophoLen);
					hydrophoLen = 0;
				}
				else if (mixedLen != 0){
					mixedDistribution.add(mixedLen);
					mixedLen = 0;
				}
			}
			else{
				row = 2;
				mixed++;
				mixedLen++;
				totalMixed++;
				if (hydropho > maximum){
					maximum = hydropho;
				}
				hydropho = 0;
				
				try{
					String a = sequence.charAt(col-1) + "";
					mixedFrequencies.put(a, mixedFrequencies.get(a) + 1);
				}catch(NullPointerException i){
					String a = sequence.charAt(col-1) + "";
					mixedFrequencies.put(a, 1.0);
				}
				
				if(hydrophoLen > hydrophilLen && hydrophoLen != 0){
					hydrophobicDistribution.add(hydrophoLen);
					hydrophoLen = 0;
				}
				else if (hydrophilLen != 0){
					hydrophilicDistribution.add(hydrophilLen);
					hydrophilLen = 0;
				}
			}
			col--;
			
			if (mixed/sequence.length() > fraction){
				fraction = mixed/sequence.length();
			}
		}
		
		if(hydrophilLen != 0){
			hydrophilicDistribution.add(hydrophilLen);
		}
		else if(hydrophoLen != 0){
			hydrophobicDistribution.add(hydrophoLen);
		}
		else{
			mixedDistribution.add(mixedLen);
		}
		
		return path;
	}
	
	//simple method to print out 1-dimensional matrix
	//used for debugging
	public static void print1DMatrix(double[] a){
		for (int i=0; i<a.length; i++){
			System.out.println(a[i]);
		}
	}
	
	//simple method to print out 2-dimensional matrix
	public static void print2DMatrix(double[][] a){
		for(int i=0; i<a.length; i++){
			for(int j=0; j<(a[i].length); j++){
				System.out.print(a[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	//simple method to print out 2-dimensional character matrix
	public static void print2Matrix(char[][] a){
		for(int i=0; i<a.length; i++){
			for(int j=0; j<(a[i].length); j++){
				System.out.print(a[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public static ArrayList<String> readFile(String filename){
		ArrayList<String> proteins= new ArrayList<String>();
		try {
            BufferedReader brs = new BufferedReader(new FileReader(filename));
            
            String currLine;
            String seq = "";
            // go through fasta formatted file
            currLine = brs.readLine();
            proteins.add(currLine);
            while ((currLine = brs.readLine()) != null) {
            	if(currLine.charAt(0) !='>'){
            		seq+=currLine;
            	}
            	else{
            		proteins.add(seq);
                    seq = "";
                    proteins.add(currLine);
            	}
            }
            proteins.add(seq);
    		brs.close();

        } 
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
		catch (IOException e) {
            e.printStackTrace();
        }
		return proteins;
	}
}
