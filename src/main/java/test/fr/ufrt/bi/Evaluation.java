package test.fr.ufrt.bi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import fr.ufrt.bi.sampling.SamplingType;
public class Evaluation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Eval e = new Eval(SamplingType.FREQUENCY_BIASED_SUBSETING);
//		ArrayList<Integer> l= new ArrayList<Integer>(Arrays.asList(11421));
//		ArrayList<Integer> l2= new ArrayList<Integer>(Arrays.asList(3323,38,2363,11421,449));
		ArrayList<Integer> l= new ArrayList<Integer>(Arrays.asList(475));
		ArrayList<Integer> l2= new ArrayList<Integer>(Arrays.asList(235,475,1277,1311,2602,4642));
		
		e.evaluate(l, l2, 0.2);
		
	}

}
