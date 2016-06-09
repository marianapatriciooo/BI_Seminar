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
	
	protected LinkedList<LinkedList<Integer>> interestingPatterns = new LinkedList<LinkedList<Integer>>();
	
	protected int searchResultsSize=0;
	protected int denominator=0;
	private BigInteger[] weights;
	protected BigInteger powerSetSum = new BigInteger("0");
	protected LinkedList<Integer> samples;
	protected LinkedList<Evaluation> relevantFeedback;
	protected int searchedItem;
	
	protected int[] positives;
	protected int[] negatives;
	
	protected LinkedList<Evaluation> evaluations;
	
	private final double euler = 1.6;
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
		this.evaluations =relevantEvals;
		this.transactionIndexMap = transactionIndexMap;
		
		createWeights();
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
	public void updateWeights(LinkedList<Evaluation> evaluations){
		this.evaluations=evaluations;
		for (int transaction=0; transaction<getWeights().length; transaction++) {
			double powerof =Math.pow(euler, (positives[transaction] - negatives[transaction]));
			long updatedWeighttt = (long) (getWeights()[transaction].longValue()*powerof);
			BigInteger updatedWeight= BigInteger.valueOf(updatedWeighttt);
			
			if (getWeights()[transaction].compareTo(updatedWeight) > 0) {
				BigInteger updatePowerSet = getWeights()[transaction].subtract(updatedWeight);
				this.powerSetSum = this.powerSetSum.subtract(updatePowerSet);
			} else {
				BigInteger updatePowerSet = updatedWeight.subtract(getWeights()[transaction]);
				this.powerSetSum = this.powerSetSum.add(updatePowerSet);
			}

			getWeights()[transaction] = updatedWeight;
			
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
		this.samples = new LinkedList<Integer>();
		
		dist = randomBigInteger(powerSetSum);
		BigInteger iterationvalue=dist;
		for(int i =0; i< searchResultsSize;i++){
			
			if (iterationvalue.compareTo(getWeights()[i]) < 0){
				samples.add(i);
				System.out.println("Samples has itemset number: " + i);
				System.out.println("Iteration value: " +  iterationvalue + " Random: " + dist );
				i=searchResultsSize;
			}
			else{
				iterationvalue = iterationvalue.subtract(getWeights()[i]);
			}
		}
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
		for (int i=0; i<samples.size();i++){
			interestingPatterns.add(calculateSubset(samples.get(i), positives, negatives, evaluations));
		}	
		System.out.println();
		System.out.println("Interesting Patterns found:");
		
		for (int i =0; i<interestingPatterns.size();i++){
			for(int j =0; j<interestingPatterns.get(i).size();j++){
				System.out.print(interestingPatterns.get(i).get(j)+ " ");
			}
			System.out.println();
		}
		
	}
	
	public void clearPatterns() {
		interestingPatterns.clear();
	}
	
	/**
	 *Defined for each of the sampling algorithms (frequency based and area based)
	 * @param sampleIndex - the location of the tuple in the input matrix (original data -all itemsets)
	 * @return the subset calculated for the given itemset
	 */
	public abstract LinkedList<Integer> calculateSubset(Integer sampleIndex, int[] posit, int [] negat, LinkedList<Evaluation> evaluations);
	
	public LinkedList<LinkedList<Integer>> getPatterns(){
		return interestingPatterns;
	}

	public BigInteger[] getWeights() {
		return weights;
	}

	public void setWeights(BigInteger[] weights) {
		this.weights = weights;
	}
	
}
