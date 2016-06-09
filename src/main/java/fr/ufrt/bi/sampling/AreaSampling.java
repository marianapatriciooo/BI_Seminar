package fr.ufrt.bi.sampling;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import fr.ufrt.bi.evaluators.Evaluation;

public class AreaSampling extends Sampling{
	
	public AreaSampling(HashMap<Integer, Integer> transactionIndexMap, LinkedList<LinkedList<Integer>> matrix, LinkedList<Evaluation> relevantEvals, int searchItem) {
		super(transactionIndexMap, matrix, relevantEvals, searchItem);
	}
	
	/**
	 * Area based
	 * 
	 * Creates a vector with the weight of each tuple
	 * 
	 */
	public void createWeights(){
		calculateNumberOfTuples();
		
		this.setWeights(new BigInteger[searchResultsSize]);
		
		this.positives = new int[searchResultsSize];
		this.negatives = new int[searchResultsSize];

		for (int i=0; i<searchResultsSize;i++){
			int itemsetSize = transactions.get(i).size();
			BigInteger itemsetSizeBig = BigInteger.valueOf(itemsetSize);
			BigInteger weight =  itemsetSizeBig.multiply(BigInteger.valueOf((long) (Math.pow(2, itemsetSize-1))));
			getWeights()[i] = weight;
			powerSetSum = powerSetSum.add(weight);
			positives[i] = 0;
			negatives[i] = 0;
		}
		System.out.println("Weights matrix created " + getWeights().length + " powerset sum: " +powerSetSum);
	}

	/**
	 * Given the number of the tuple (recorded in the sample matrix)it gets that tuple from the dataset
	 * With that tuple, calculates the probabilities of each size of subset to be generated and saves it in the int[] probabilityList
	 * After, given the probabilities, calculates which sizes of subsets to generate
	 * Given that selection, generates ONE random subset of the size selected for generation
	 * @param sample_nb_itemset - the location of the tuple in the input matrix (original data -all itemsets)
	 */
	public LinkedList<Integer> calculateSubset(Integer sample_nb_itemset,  int[] posit, int[] negat, LinkedList<Evaluation> evals) {
		LinkedList<Integer> itemset = transactions.get(sample_nb_itemset);
		int nb_items = (int)itemset.size();
		Integer indexOfSearchedItem=0;
		//the list of the probabilities for each subset size
		//each cell corresponds to its index size. example probabilityList[5] will be the probability of generating a subset of size 5
		//probabilityList will have the range for which the size will be chosen given a random generated number
		//probabilityList has as many lines as items on the tuple, all the possible sizes of a subset
		int[][] probabilityList = new int[nb_items][2];
		int denominator =0;
		int size=itemset.size();
		for (int i=0;i<itemset.size();i++){
			denominator=denominator + size;
			size--;
			if(itemset.get(i)==searchedItem){
				indexOfSearchedItem=i;
			}
		}
		int marker=0;
		System.out.println("Winner intervals for itemset " + sample_nb_itemset + ":" );
		//creates the intervals for which each size will be the "winner"
		for(int i=0; i<nb_items;i++){
			
				probabilityList[i][0]=marker;
				probabilityList[i][1]=(marker+i) ;
				marker = marker+i+1;
			
			
			System.out.println((i+1) + ": [" + probabilityList[i][0] + ", " +probabilityList[i][1]  + "]");
		}
		Random r = new Random();
		int winner_number =r.nextInt(denominator);
		System.out.println("The Random is: " + winner_number);
		System.out.println("The denominator for the subset generator is: " + denominator);
		
		int subsetsize=0;
		for(int i=0; i<nb_items; i++){
			if(winner_number>=probabilityList[i][0] && winner_number<=probabilityList[i][1]){
				subsetsize=i;
			}
		}
		
		//creates a list with the number of items of the input itemset
		//shuffles (randomly) and takes the first n indexes
		System.out.println("The chosen size is: " + (subsetsize+1));
		System.out.println();
		Integer[] arr = new Integer[nb_items];
		    for (int i = 0; i < arr.length; i++) {
		        arr[i] = i;
		    }
		    //Keeping in mind that if the searched item is no on the subset, the last index will be replaced with the searched item
		    System.out.println("The shuffled array from which the first n indexes of items in the itemset will be picked is:");
		    Collections.shuffle(Arrays.asList(arr));
		    System.out.println(Arrays.toString(arr));
		    
		 //Copy the first n indexes to an arraylist to order them
		    Integer[] arr_list = new Integer[subsetsize+1];
		    boolean hasSearchedItem=false;
		    for (int i = 0; i < subsetsize+1; i++) {
		    	arr_list[i]=arr[i];
		    	if(arr[i]==indexOfSearchedItem){
		    		hasSearchedItem=true;
		    	}
		    }
		    if(hasSearchedItem==false){
		    	arr_list[subsetsize]=indexOfSearchedItem;
		    }
		   Arrays.sort(arr_list);
		   
		//We go to the original itemset to ge the items on the first n indexes of the arr
		 LinkedList <Integer> pattern = new LinkedList<Integer>();
		 for(int i=0; i<arr_list.length;i++){
			 pattern.add(itemset.get(arr_list[i]));
		 }


		return pattern;
		
		/**
		//How to get the number of digits of an integer
		Integer nb = itemset.get(0);
		int length = (int) Math.log10(nb) + 1;
		System.out.println(length);
		**/
	}
}
