import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class ComputeThread extends Thread {
	private BigInteger[] arr;
	private Function<BigInteger, BigInteger> [] functions;
	public int start;
	public int end;
	public List<BigInteger> results = new ArrayList<>();
	public int size;
	//public BigInteger[]results = new BigInteger[end-start]; 
	
	public ComputeThread(BigInteger[] arr, int start, int end, Function<BigInteger, BigInteger> [] functions) {
		this.arr = arr;
		this.start = start;
		this.end = end;
		this.functions= functions;
		this.size = end-start;	
	}
	
	public List<BigInteger> getResult() {
		return results;
	}
	public void setResult(List<BigInteger> results) {
		this.results = results;
	}

	@Override
	public void run() {

		// System.out.println("Running... start="+start+", end="+end+", results.size="+results.size());
		
		// while (start<end && start<results.size()) {
		while (start<end) {			
			// System.out.println("Current function: " + start);
			results.add(functions[start].apply(arr[start]));
			start++;
			// System.out.println(results.toString());
		}
	}
	
	
}
