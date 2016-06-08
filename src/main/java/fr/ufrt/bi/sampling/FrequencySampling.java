package fr.ufrt.bi.sampling;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import fr.ufrt.bi.evaluators.Evaluation;

public class FrequencySampling extends Sampling {
	
	public FrequencySampling(HashMap<Integer, Integer> transactionIndexMap, LinkedList<LinkedList<Integer>> dataset, LinkedList<Evaluation> relevantEvals, int searchItem) {
		super(transactionIndexMap, dataset, relevantEvals, searchItem);
	}
	
	/**
	 * Frequency based
	 * 
	 * Creates a vector with the weight of each tuple
	 * the weight is given by w = 2^(nb of items)-1
	 */
	public void createWeights(){
		calculateNumberOfTuples();
		
		this.setWeights(new BigInteger[searchResultsSize]);
		
		this.positives = new int[searchResultsSize];
		this.negatives = new int[searchResultsSize];

		for (int i=0; i<searchResultsSize;i++){
			int itemsetSize = transactions.get(i).size();
			BigInteger weight = BigInteger.valueOf((long) (Math.pow(2, itemsetSize)));
				
			getWeights()[i] = weight;
			powerSetSum = powerSetSum.add(weight);
			positives[i] = 0;
			negatives[i] = 0;
		}
		System.out.println("Weights matrix created " + getWeights().length + " powerset sum: " +powerSetSum);
	}

	/**
	 * Given the number of the tuple (recorded in the sample matrix) it gets that tuple from the dataset
	 * Randomly calculates a binary matrix with each item of the tuple and a 50-50 chance of it being in the chosen subset
	 * This way, all the subsets of the powerset have the same probability of being chosen, regardless of its size
	 * @param sampleIndex - the location of the tuple in the input matrix (original data -all itemsets)
	 * @return the subset calculated for the given itemset
	 */
	public LinkedList<Integer> calculateSubset(Integer sampleIndex, int[] posit, int[] negat, LinkedList<Evaluation> eval) {
		LinkedList<Integer> itemset = transactions.get(sampleIndex);
		int[] outputListBinary = new int[(int)itemset.size()];
		
		Random r = new Random();
		
		System.out.println("Sample itemset to generate a subset: ");
		
		for (int i=0;i<itemset.size();i++){
			outputListBinary[i]=r.nextInt(2);
			//Makes sure the searched item is retrieved in the subset found
			if(itemset.get(i) == searchedItem){
				outputListBinary[i]=1;
			}
			System.out.print(itemset.get(i) + " ");
		}
		
		System.out.println();
		System.out.println("Generated subset: ");
		
		LinkedList <Integer> pattern = new LinkedList<Integer>();
		for (int i=0;i<outputListBinary.length;i++){
			if(outputListBinary[i]==1){
				pattern.add(itemset.get(i));
				System.out.print(itemset.get(i) + " ");
			}
		}
		System.out.println();
		return pattern;
	}
}
