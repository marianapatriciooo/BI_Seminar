package fr.ufrt.bi.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HashMapRKeys {

	private HashMap<Integer, ArrayList<Integer>> hashMap = new HashMap<Integer, ArrayList<Integer>>();

	public void addValue(Integer item, int value) {
		ArrayList<Integer> tempList = null;

		if (hashMap.containsKey(item)) {
			tempList = hashMap.get(item);
		}
		if (tempList == null)
			tempList = new ArrayList<Integer>();
		
		tempList.add(value);
		hashMap.put(item, tempList);
	}

	public ArrayList<Integer> getItemTransactions(int key) {
		return hashMap.get(key);
	}

	/**
	 * Gets the intersection of transactions that contains all items
	 * @param items
	 * @return
	 */
	public ArrayList<Integer> getTransactionsItems(int[] items) {
		ArrayList<Set<Integer>> transactions = new ArrayList<Set<Integer>>();

		for (int item : items) {
			Set<Integer> set = new HashSet<Integer>(hashMap.get(item));
			transactions.add(set);
		}

		Set<Integer> intersectedTransactions = transactions.get(0);
		for (int i = 1; i < transactions.size(); i++) {
			intersectedTransactions.retainAll(transactions.get(i));
		}
		return new ArrayList<Integer>(intersectedTransactions);
	}

	protected int size() {
		return hashMap.size();
	}

	protected HashMap<Integer, ArrayList<Integer>> getMap() {
		return hashMap;
	}

}