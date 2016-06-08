package fr.ufrt.bi.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import fr.ufrt.bi.config.Config;
import fr.ufrt.bi.evaluators.Evaluation;
import fr.ufrt.bi.sampling.AreaSampling;
import fr.ufrt.bi.sampling.FrequencySampling;
import fr.ufrt.bi.sampling.FrequencySampling_biasedSubsetting;
import fr.ufrt.bi.sampling.HashMapRKeys;
import fr.ufrt.bi.sampling.Sampling;
import fr.ufrt.bi.sampling.SamplingType;

@ManagedBean(name = "searchBean")
@ViewScoped
public class SearchBean {

	private String pattern;

	private LinkedList<LinkedList<Integer>> dataset;
	private HashMapRKeys invertedMatrix;
	
	private LinkedList<LinkedList<Integer>> searchResults;
	private HashMapRKeys searchResultsMatrix;
	
	private LinkedList<LinkedList<Integer>> patterns;
	private LinkedList<Integer> frequencies;

	private HashMap<Integer, Integer> transactionIndexMap;

	private LinkedList<Evaluation> evaluations;

	private Sampling sampling;
	private SamplingType samplingType;

	public SearchBean() {
		dataset = Config.loadDataset();

		createInvertedMatrix();

		searchResults = new LinkedList<LinkedList<Integer>>();
		patterns = new LinkedList<LinkedList<Integer>>();

		samplingType = SamplingType.FREQUENCY;

		transactionIndexMap = new HashMap<Integer, Integer>();

		evaluations = new LinkedList<Evaluation>();
	}

	private void createInvertedMatrix() {
		this.invertedMatrix = new HashMapRKeys();

		BufferedReader br = null;

		String fileName = "/Users/larissaleite/Downloads/retail.dat.txt";

		try {
			br = new BufferedReader(new FileReader(fileName));

			String line = "";
			String datSplitBy = " ";

			int transaction = 0;
			System.out.println("...Creating the inverted index...");
			while ((line = br.readLine()) != null) {
				String[] values = line.split(datSplitBy);

				for (String value : values) {
					Integer integ = Integer.parseInt(value);
					invertedMatrix.addValue(integ, transaction);
				}
				transaction++;
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void search() {
		Integer searchedPattern = Integer.parseInt(pattern);

		ArrayList<Integer> transactions = invertedMatrix
				.getItemTransactions(searchedPattern);
		System.out.println("Itemsets that contain " + pattern + ": "
				+ transactions);

		if (transactions != null) {
			for (int i = 0; i < transactions.size(); i++) {
				addtoSearchResults(transactions.get(i));
				transactionIndexMap.put(transactions.get(i), i);

			}
			if (samplingType == SamplingType.FREQUENCY) {
				this.setSampling(new FrequencySampling(transactionIndexMap,
						searchResults, evaluations, searchedPattern));
			}
			if (samplingType == SamplingType.AREA) {
				this.setSampling(new AreaSampling(transactionIndexMap,
						searchResults, evaluations, searchedPattern));
			} else if (samplingType == SamplingType.FREQUENCY_BIASED_SUBSETING) {
				this.setSampling(new FrequencySampling_biasedSubsetting(
						transactionIndexMap, searchResults, evaluations,
						searchedPattern));
			}

			createSearchResultsMap();
			
			getPatternsFromSample();
		}
	}

	private void createSearchResultsMap() {
		this.searchResultsMatrix = new HashMapRKeys();
		
		for (LinkedList<Integer> itemset : searchResults) {
			for (Integer value : itemset) {
				searchResultsMatrix.addValue(value, searchResults.indexOf(itemset));
			}
		}
	}

	private void getPatternsFromSample() {
		this.getSampling().clearPatterns();
		
		for (int i=0; i<5; i++) {
			this.getSampling().calculateSample();
			this.getSampling().calculateOutputPatterns();
		}

		this.setPatterns(getSampling().getPatterns());
		this.getPatternsFrequencies();
		if (this.getPatterns().size() == 0) {
			this.getPatternsFromSample();
		}
	}

	private void getPatternsFrequencies() {
		this.frequencies = new LinkedList<Integer>();
		
		for (LinkedList<Integer> pattern : patterns) {
			int[] items = new int[pattern.size()];
			for (int i = 0; i < pattern.size(); i++) {
				items[i] = pattern.get(i);
			}
			int frequency = this.invertedMatrix.getTransactionsItems(items).size();
			frequencies.add(frequency);
		}
	}

	public void userEvaluation(int patternIndex, boolean feedback) {
		System.out.println("Pattern " + patternIndex + "   feedback "
				+ feedback);

		int[] items = new int[patterns.get(patternIndex).size()];
		for (int i = 0; i < patterns.get(patternIndex).size(); i++) {
			items[i] = patterns.get(patternIndex).get(i);
		}

		ArrayList<Integer> transactionsItems = invertedMatrix
				.getTransactionsItems(items);

		if (feedback) {
			this.getSampling().updatePositives(transactionsItems);
		} else {
			this.getSampling().updateNegatives(transactionsItems);
		}

		this.getSampling().updateWeights(evaluations);

		// after feedback is given, remove from the list of patterns
		//this.patterns.remove(patternIndex);
		//this.frequencies.remove(patternIndex);
		
		//if (patterns.isEmpty()) {
		//	getPatternsFromSample();
		//}
	}
	
	private void addtoSearchResults(int i) {
		LinkedList<Integer> tuple = new LinkedList<>();
		tuple.addAll(dataset.get(i));

		searchResults.add(tuple);
	}

	public SamplingType[] getSamplingTypes() {
		return SamplingType.values();
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public SamplingType getSamplingType() {
		return samplingType;
	}

	public void setSamplingType(SamplingType samplingType) {
		this.samplingType = samplingType;
	}

	public LinkedList<LinkedList<Integer>> getPatterns() {
		return patterns;
	}

	public void setPatterns(LinkedList<LinkedList<Integer>> pattern) {
		this.patterns = pattern;
	}

	public LinkedList<Integer> getFrequencies() {
		return frequencies;
	}

	public void setFrequencies(LinkedList<Integer> frequencies) {
		this.frequencies = frequencies;
	}

	public Sampling getSampling() {
		return sampling;
	}

	public void setSampling(Sampling sampling) {
		this.sampling = sampling;
	}

	public void calc() {
		this.patterns.clear();
		getPatternsFromSample();
	}

}
