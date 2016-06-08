package test.fr.ufrt.bi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import fr.ufrt.bi.sampling.SamplingType;

public class Evaluation2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<ArrayList<BigInteger[]>> x = new ArrayList<>();
		Eval2 e = new Eval2(SamplingType.FREQUENCY);
		ArrayList<Integer> l= new ArrayList<Integer>(Arrays.asList(475));
		ArrayList<Integer> l2= new ArrayList<Integer>(Arrays.asList(235,475,1277,1311,2602,4642));
		
		//x.add(e.evaluate(l, l2, 0.2));
		//l= new ArrayList<Integer>(Arrays.asList(2768));
		//l2= new ArrayList<Integer>(Arrays.asList(1814,1818,2062,2768,3319,3511,4436,4655));
		x.add(e.evaluate(l, l2, 0.2));
		
//		try (Writer writer = new FileWriter("F:\\BIS\\averages.csv")) {
//			for (int k = 0; k < x.size(); k++) {
//				
//				ArrayList<BigInteger[]> map2=x.get(k);
//			
//			for (int i = 1; i < map2.size(); i++) {
//				int sum1=0;
//				int sum2=0;
//				for (int j = 0; j < map2.get(i).length; j++) {
//
//					if((map2.get(i)[j]).compareTo((map2.get(i-1)[j]))>0){
//						sum1++;
//					}
//					if((map2.get(i)[j]).compareTo((map2.get(i-1)[j]))<0){
//						sum2++;
//					}
//					//writer.append((map2.get(i)[j]).toString()).append(",");
//
//				}
//				writer.append(String.valueOf(sum1)).append(",").append(String.valueOf(sum2));
//				writer.append(System.getProperty("line.separator"));
//				}
//			writer.append("------").append(System.getProperty("line.separator"));
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace(System.err);
//		}
//		
	}

}
