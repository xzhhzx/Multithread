import java.math.BigInteger;
import java.util.function.BinaryOperator;

public class ReduceThread extends Thread {
	private BigInteger [] arr;
	private int start;
	private int end;
	public BigInteger sub_result;
	public BinaryOperator<BigInteger> binOp;

	public ReduceThread(BigInteger[] arr, int start, int end, BinaryOperator<BigInteger> binOp) {
		this.arr = arr;
		this.start = start;
		this.end = end;
		this.binOp = binOp;
	}

	@Override
	public void run() {
		sub_result = arr[start];
		for(int i=start+1; i<end; ++i){
			sub_result = binOp.apply(sub_result, arr[i]);
		}
		System.out.println("sub_result: "+sub_result);
	}
}