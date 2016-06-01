package fr.ufrt.bi.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import fr.ufrt.bi.sampling.HashMapRKeys;

public class Config {
	
	public static LinkedList<LinkedList<Integer>> loadDataset() {
	//public static HashMapRKeys loadDataset() {
		LinkedList<LinkedList<Integer>> dataset = new LinkedList<LinkedList<Integer>>();
		
		BufferedReader br = null;
		
		String fileName = "/Users/larissaleite/Downloads/retail10.txt";

		HashMapRKeys hashMapRKeys = new HashMapRKeys();
        try {
        	br = new BufferedReader(new FileReader(fileName));
			
			String line = "";
			String datSplitBy = " ";
			
			int transaction = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(datSplitBy);
				LinkedList<Integer> lines = new LinkedList<Integer>();
				
				for (String value : values) {
					Integer integ = Integer.parseInt(value);
					lines.add(integ);
				}
				
				dataset.add(lines);
				/*for (String value : values) {
					hashMapRKeys.addValue(Integer.parseInt(value), transaction);
				}
				transaction++;*/
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return dataset;
       // return hashMapRKeys;
	}

}
