package fr.ufrt.bi.sampling;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import fr.ufrt.bi.evaluators.Evaluation;

public class FrequencySampling_biasedSubsetting extends Sampling {
	
	private double alpha = 1.1;
	private HashMap<Integer, Double> allitemWeights;
	
	public FrequencySampling_biasedSubsetting(HashMap<Integer, Integer> transactionIndexMap, LinkedList<LinkedList<Integer>> dataset, LinkedList<Evaluation> relevantEvals, int searchItem) {
		super(transactionIndexMap, dataset, relevantEvals, searchItem);
		System.out.println("Nb of transactions resulting from the search " + transactions.size());
		initializeItemWeights();
	}
	
	public void initializeItemWeights(){
		allitemWeights = new HashMap<Integer, Double>();
		for (int i=0; i<transactions.size();i++){
			for(int j=0; j<transactions.get(i).size();j++){
				if (!allitemWeights.containsKey(transactions.get(i).get(j))){
					allitemWeights.put(transactions.get(i).get(j), 0.5);
				}
			}
		}
	}
	
	/**
	 * 
	 * @return the L - the average length of the itemsets that have been evaluated positively
	 */
	public double averageSizePositiveFeedback(double sizeitemsetPicked){
		double averageSize =sizeitemsetPicked/2;
		if(evaluations.size()>0){
			double numberPositives =0;
			double sumLenghts =0;
			for(int i =0; i<evaluations.size(); i++){
				if(evaluations.get(i).getEval()==true){
					numberPositives++;
					sumLenghts =sumLenghts + evaluations.get(i).getPattern().size();
				}
			}
			if(numberPositives>0){
				averageSize = sumLenghts/numberPositives;

			}
			
		}
		System.out.println("Average Size: " + averageSize);
		return averageSize;
	}
	
	
	/**
	 * Frequency based
	 * 
	 * Creates a vector with the weight of each tuple
	 * the weight is given by w = 2^(nb of items)-1
	 */
	public void createWeights(){
		calculateNumberOfTuples();
		
		this.weights = new BigInteger[searchResultsSize];
		
		this.positives = new int[searchResultsSize];
		this.negatives = new int[searchResultsSize];

		for (int i=0; i<searchResultsSize;i++){
			int itemsetSize = transactions.get(i).size();
			BigInteger weight = BigInteger.valueOf((long) (Math.pow(2, itemsetSize)));
				
			weights[i] = weight;
			powerSetSum = powerSetSum.add(weight);
			positives[i] = 0;
			negatives[i] = 0;
		}
		System.out.println("Weights matrix created " + weights.length + " powerset sum: " +powerSetSum);
	}
	
	
	


	/**
	 * To sum up, if:
	 * - w(i) is the weight associated with i,
	 * - i is evaluated positively P times,
	 * - i is evaluated negatively N times,
	 * the updated weight of i should be w(i) * \alpha^(P-N) (the same rule used for the transactions).
	 * 
	 * then
	 * - Let t be the selected transaction.
	 * - Let W be the sum of the weights of the items in t
	 * - Let L be the average length of the itemsets that have been evaluated positively, 
	 * - For each item i in t, compute the probability p(i) = min { 1, L * w(i) / W }
	 * @param sampleIndex - the location of the tuple in the input matrix (original data - all itemsets)
	 * @return the subset calculated for the given itemset
	 */
	public LinkedList<Integer> calculateSubset(Integer sampleIndex, int[]posit, int [] negat, LinkedList<Evaluation> eval) {
		this.evaluations=eval;
		LinkedList<Integer> itemset = transactions.get(sampleIndex);
		double[] outputListBinary = new double[itemset.size()];
		double[] itemweights = new double[itemset.size()];
		Random r = new Random();
		double nbevals=0;
		LinkedList<Integer> relevantEvals =new LinkedList<Integer>();
		
		//Make a vector with bias
		for(int i=0; i<itemset.size(); i++){
			Integer item = itemset.get(i);
			int positives=0;
			int negatives=0;
			for(int j=0; j<evaluations.size();j++){
				if(evaluations.get(j).itemIsContainedinPattern(itemset.get(i))){
					if(evaluations.get(j).getEval()==true){
						positives++;
						if(!relevantEvals.contains(j)){
							relevantEvals.add(j);
						}
					}
					else{
						negatives++;
						if(!relevantEvals.contains(j)){
							relevantEvals.add(j);
						}
					}
						
				}	
			}
			//for each item get the alpha to the power of the difference between p an n
				//nb of relevant evals - total
				//double denom = relevantEvals.size()+1;
			double diff = (positives-(negatives));
			itemweights[i]=Math.pow(alpha, diff);
		}
		
		double sumofweights=0;
		
		
		//apply the bias to the previous weights - p(i-1)*bias
		for(int i=0; i<itemset.size(); i++){
			System.out.println("BIASSS " + itemweights[i]);
			Integer item = itemset.get(i);
			double itemweight = allitemWeights.get(item);
			System.out.println("Bias to apply to previous weight - ITEM: " + item + ", weight: " + itemweight);
			//the original the previous weight, itemweight, times the bias alpha to the power of (p-n)
			itemweights[i] = (itemweights[i] * itemweight);
			System.out.println("previous probability times the alpha to the power of (p-n) - ITEM: " + item + ", weight: " + itemweight);
			sumofweights=sumofweights+itemweights[i];
		}
		
		//normalize the biased probabilities and update the hasmap with the probabilities
		for(int i=0; i<itemset.size(); i++){
			double L = averageSizePositiveFeedback((double)itemset.size());
			itemweights[i] = Math.min(1,((L*itemweights[i])/sumofweights));
			System.out.println("Sum of weights: " + sumofweights + " FinalProbability: " +itemweights[i] );
			allitemWeights.put(itemset.get(i), itemweights[i]);
		}
		
		
		
		
		System.out.println("Sample itemset to generate a subset: ");
		
		//do a random - float - to see if it is bigger or smaller than the probability given
		for (int i=0;i<itemset.size();i++){
			outputListBinary[i]=r.nextDouble();
			if(outputListBinary[i]<itemweights[i]){
				outputListBinary[i]=1;
			}
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
