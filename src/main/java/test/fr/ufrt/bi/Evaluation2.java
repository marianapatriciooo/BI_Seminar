package test.fr.ufrt.bi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import fr.ufrt.bi.sampling.SamplingType;

public class Evaluation2 {

	public static void main(String[] args) {
		ArrayList<ArrayList<BigInteger[]>> x = new ArrayList<>();
		Eval2 e = new Eval2(SamplingType.FREQUENCY);
		ArrayList<Integer> l= new ArrayList<Integer>(Arrays.asList(475));
		ArrayList<Integer> l2= new ArrayList<Integer>(Arrays.asList(235,475,1277,1311,2602,4642));
		
		x.add(e.evaluate(l, l2, 0.2));
		
	}

}
