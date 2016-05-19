package fr.ufrt.bi.sampling;

import java.util.LinkedList;
import java.util.Random;

import fr.ufrt.bi.evaluators.Evaluation;

public abstract class Sampling {
	
	protected LinkedList<LinkedList<Integer>> dataset;
	
	protected LinkedList<LinkedList<Integer>> interestingPatterns;
	
	protected int datasetSize=0;
	protected int[] weights;
	protected int powerSetSum=0;
	protected LinkedList<Integer> sample;
	protected LinkedList<Evaluation> relevantFeedback;
	protected int searchedItem;
	
	/**
	 * Create the weights for each tuple - create_weights()
	 * According to the probability given by the weights creates a vector with the indexes of the tuples to be sampled - getSample()
	 * Retrieves the tuples for sampling, creates a set for each and the respective powerset for each
	 * @param matrix
	 * @param searchedItem 
	 */
	public Sampling (LinkedList<LinkedList<Integer>> dataset, LinkedList<Evaluation> relevantEvals, int searchedItem) {
		this.dataset = dataset;
		this.relevantFeedback = relevantEvals;
		this.searchedItem = searchedItem;
		
		create_weights();
		calculateSample();
		calculateOutputPatterns();
	}

	/**
	 * Creates a vector with the weight of each tuple
	 */
	public abstract void create_weights();
	/**
	 * Given the relevant feedbacks it recalculates the weights
	 * Furthermore the denominator needs to be updated for the probabilities to be normalized
	 * This meaning, the denominator will no longer be the sum of the powersets but the sum of the updated weights
	 */
	public void update_weights(){
		//NEEDS TO BE IMPLEMENTED STILL
	}
	
	/**
	 * Counts the number of tuples (lines) in the dataset
	 */
	public void calculateNTuples(){
		this.datasetSize=dataset.size();
	}
	
	/**
	 * Gets a sample approximately of the size defined in sampleSize variable
	 * Each tuple gets a probability of being chosen proportional to the size of its powerSet (given by the weight)
	 */
	public void calculateSample(){
		Random r = new Random();
		int dist =0;
		int samplesize=0;
		this.sample = new LinkedList<Integer>();
		for(int i =0; i< datasetSize;i++){
			dist= r.nextInt(powerSetSum);
			if (dist<weights[i]){
				sample.add(i);
				samplesize++;
				System.out.println("Samples has itemset number: " + i);
			}
		}
		System.out.println("Sample size: " + samplesize);
		System.out.println();
	}

	/**
	 * For each itemset on the sample, calls a method that generates the output subset
	 */
	private void calculateOutputPatterns() {
		interestingPatterns = new LinkedList<LinkedList<Integer>>();
		for (int i=0; i<sample.size();i++){
			interestingPatterns.add(calculateSubset(sample.get(i)));
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
	
	/**
	 *Defined for each of the sampling algorithms (frequency based and area based)
	 * @param sampleIndex - the location of the tuple in the input matrix (original data -all itemsets)
	 * @return the subset calculated for the given itemset
	 */
	public abstract LinkedList<Integer> calculateSubset(Integer sampleIndex);
	
	public LinkedList<LinkedList<Integer>> getSample(){
		return interestingPatterns;
	}
	
}