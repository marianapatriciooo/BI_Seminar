package test.fr.ufrt.bi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.primefaces.event.SlideEndEvent;

import fr.ufrt.bi.config.Config;
import fr.ufrt.bi.controllers.SearchBean;
import fr.ufrt.bi.evaluators.Evaluation;
import fr.ufrt.bi.sampling.HashMapRKeys;
import fr.ufrt.bi.sampling.Sampling;
import fr.ufrt.bi.sampling.SamplingType;

public class Eval2 {

	private SearchBean sb;
	private HashMap<Integer, Double> map = new HashMap<>();
	private ArrayList<BigInteger[]> map2 = new ArrayList<>();
	ArrayList<Integer> a = new ArrayList<>();
	private String typ;
	private String sim;
	private HashMapRKeys hashMapRKeys = new HashMapRKeys();

	public Eval2(SamplingType type) {
		sb = new SearchBean();
		sb.setSamplingType(type);
		typ = type.name();
		// create();
	}

	public ArrayList<BigInteger[]> evaluate(ArrayList<Integer> startingPattern, ArrayList<Integer> endPattern, double smilarity) {
		a = hashMapRKeys.getItemTransactions(startingPattern.get(0));
		String s = String.valueOf(startingPattern.get(0));
		for (Integer integer : startingPattern) {
			s = s + "," + integer;
		}

		sim = String.valueOf(smilarity);
		sb.setPattern(String.valueOf(startingPattern.get(0)));//(s);
		sb.search();
		LinkedList<LinkedList<Integer>> l = sb.getPatterns();

		for (int i = 0; i < 5; i++) {
			if (l.size() == 0)
				break;

			//System.out.println("NNNNNNN ->" + sb.getWeights().length);
			int y = 0;
			map2.add(sb.getSampling().getWeights().clone());
			
			for (LinkedList<Integer> linkedList : l) {

				double sim = getSimilarity(new ArrayList<Integer>(linkedList), endPattern);
				map.put(i, sim);
				if(sim >0.8)
					break;
				// if(i==0|| i==999 || i== 10 || i==500){
//				System.out.println(map2.size());
//				System.out.println(map2.get(0).length);
//				// }
				if(i<300){
				
				if (sim >= smilarity) {
					sb.userEvaluation(y, true);
				} else
					sb.userEvaluation(y, false);	
				}
				
				y++;
			}
			sb.calc();
			l = sb.getPatterns();
		}

		// System.out.println(map2);
		writeFile2();
		writeFile();
		return map2;
	}

	private void writeFile() {
		String eol = System.getProperty("line.separator");

		try (Writer writer = new FileWriter("/Users/larissaleite/Downloads/BI" + sim + "_" + typ + ".csv")) {
			for (Map.Entry<Integer, Double> entry : map.entrySet()) {
				writer.append(entry.getKey().toString()).append(',').append(entry.getValue().toString()).append(eol);
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

	private void writeFile2() {
		String eol = System.getProperty("line.separator");

		try (Writer writer = new FileWriter("/Users/larissaleite/Downloads/BI" + sim + "_" + typ + ".csv")) {
			for (int i = 0; i < map2.size(); i++) {
				for (int j = 0; j < map2.get(i).length; j++) {

					writer.append((map2.get(i)[j]).toString()).append(",");

				}
				writer.append(eol);
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
		
		try (Writer writer = new FileWriter("/Users/larissaleite/Downloads/BI" + sim + "_" + typ + ".csv")) {
			for (int i = 1; i < map2.size(); i++) {
				int sum1=0;
				int sum2=0;
				for (int j = 0; j < map2.get(i).length; j++) {

					if((map2.get(i)[j]).compareTo((map2.get(i-1)[j]))>0){
						sum1++;
					}
					if((map2.get(i)[j]).compareTo((map2.get(i-1)[j]))<0){
						sum2++;
					}
					//writer.append((map2.get(i)[j]).toString()).append(",");

				}
				writer.append(String.valueOf(sum1)).append(",").append(String.valueOf(sum2));
				writer.append(eol);
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

	public double getSimilarity(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		Set<Integer> intersection = new HashSet<>(list1);
		Set<Integer> union = new HashSet<>(list1);
		Set<Integer> s2 = new HashSet<>(list2);
		union.addAll(s2);
		intersection.retainAll(list2);
		return (double) intersection.size() / (double) union.size();
	}

	public void create() {
		BufferedReader br = null;

		String fileName = "/Users/larissaleite/Downloads/retail.dat.txt";

		try {
			br = new BufferedReader(new FileReader(fileName));

			String line = "";
			String datSplitBy = " ";

			int transaction = 1;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(datSplitBy);
				LinkedList<Integer> lines = new LinkedList<Integer>();

				for (String value : values) {
					Integer integ = Integer.parseInt(value);
					hashMapRKeys.addValue(integ, transaction);
				}
				transaction++;
				// System.out.println(transaction);
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
}
