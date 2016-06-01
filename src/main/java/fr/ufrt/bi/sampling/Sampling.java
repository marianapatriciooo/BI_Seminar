package fr.ufrt.bi.sampling;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import fr.ufrt.bi.evaluators.Evaluation;

public abstract class Sampling {
	
	protected LinkedList<LinkedList<Integer>> transactions;
	
	//map the transaction index to its position (index) on the weight/positive/negative vectors
	protected HashMap<Integer, Integer> transactionIndexMap;
	
	protected LinkedList<LinkedList<Integer>> interestingPatterns;
	
	protected int searchResultsSize=0;
	protected int denominator=0;
	protected BigInteger[] weights;
	protected BigInteger powerSetSum = new BigInteger("0");
	protected LinkedList<Integer> samples;
	protected LinkedList<Evaluation> relevantFeedback;
	protected int searchedItem;
	
	protected int[] positives;
	protected int[] negatives;
	
	private final double euler = 0.5772156649;
	/**
	 * Create the weights for each tuple - createWeights()
	 * According to the probability given by the weights creates a vector with the indexes of the tuples to be sampled - getSample()
	 * Retrieves the tuples for sampling, creates a set for each and the respective powerset for each
	 * @param transactions
	 * @param searchedItem 
	 */
	public Sampling (HashMap<Integer, Integer> transactionIndexMap, LinkedList<LinkedList<Integer>> transactions, LinkedList<Evaluation> relevantEvals, int searchedItem) {
		this.transactions = transactions;
		
		this.relevantFeedback = relevantEvals;
		this.searchedItem = searchedItem;
		
		this.transactionIndexMap = transactionIndexMap;
		
		createWeights();
		//calculateSample();
		//calculateOutputPatterns();
	}

	/**
	 * Creates a vector with the weight of each tuple
	 */
	public abstract void createWeights();
	
	/**
	 * Given the relevant feedbacks it recalculates the weights
	 * Furthermore the denominator needs to be updated for the probabilities to be normalized
	 * This meaning, the denominator will no longer be the sum of the powersets but the sum of the updated weights
	 */
	public void updateWeights(){
		for (int transaction=0; transaction<weights.length; transaction++) {
			//weights[transaction] = weights[transaction] + BigDecimal.valueOf(Math.pow(euler, (positives[transaction] - negatives[transaction])));
			BigInteger updatedWeight = BigInteger.valueOf((long) Math.pow(euler, (positives[transaction] - negatives[transaction])));
			if (weights[transaction].compareTo(updatedWeight) > 0) {
				BigInteger updatePowerSet = weights[transaction].subtract(updatedWeight);
				this.powerSetSum = this.powerSetSum.subtract(updatePowerSet);
			} else {
				BigInteger updatePowerSet = updatedWeight.subtract(weights[transaction]);
				this.powerSetSum = this.powerSetSum.add(updatePowerSet);
			}
			weights[transaction] = updatedWeight;
		}
	}
	
	public void updatePositives (ArrayList<Integer> transactions) {
		for (int index : transactions) {
			int transactionIndex = transactionIndexMap.get(index);
			positives[transactionIndex]++;
		}
	}
	
	public void updateNegatives (ArrayList<Integer> transactions) {
		for (int index : transactions) {
			int transactionIndex = transactionIndexMap.get(index);
			negatives[transactionIndex]++;
		}
	}
	
	public void calculateNumberOfTuples(){
		this.searchResultsSize=transactions.size();
	}
	
	/**
	 * Gets a sample approximately of the size defined in sampleSize variable
	 * Each tuple gets a probability of being chosen proportional to the size of its powerSet (given by the weight)
	 */
	public void calculateSample(){
		BigInteger dist;
		int samplesize=0;
		this.samples = new LinkedList<Integer>();
		for(int i =0; i< searchResultsSize;i++){
			dist = randomBigInteger(powerSetSum);
			if (dist.compareTo(weights[i]) < 0){
				samples.add(i);
				samplesize++;
				System.out.println("Samples has itemset number: " + i);
			}
		}
		System.out.println("Sample size: " + samplesize);
		System.out.println();
	}

	

	private BigInteger randomBigInteger(BigInteger n) {
		Random rand = new Random();
	    BigInteger result = new BigInteger(n.bitLength(), rand);
	    while( result.compareTo(n) >= 0 ) {
	        result = new BigInteger(n.bitLength(), rand);
	    }
	    return result;
	}

	/**
	 * For each itemset on the sample, calls a method that generates the output subset
	 */
	public void calculateOutputPatterns() {
		interestingPatterns = new LinkedList<LinkedList<Integer>>();
		for (int i=0; i<samples.size();i++){
			interestingPatterns.add(calculateSubset(samples.get(i)));
		}	
		System.out.println();
		System.out.println("Interesting Patterns found:");
		
		for (int i =0; i<interestingPatterns.size();i++){
			for(int j =0; j<interestingPatterns.get(i).size();j++){
				//System.out.print(interestingPatterns.get(i).get(j)+ " ");
			}
			System.out.println();
		}
	}
	
	/**
	 *Defined for each of the sampling algorithms (frequency based and area based)
	 * @param sampleIndex - the location of the tuple in the input matrix (original data -all itemsets)
	 * @return the subset calculated for the given itemset
	 */
	public abstract LinkedList<Integer> calculateSubset(Integer sampleIndex);
	
	public LinkedList<LinkedList<Integer>> getPatterns(){
		return interestingPatterns;
	}
	
}
