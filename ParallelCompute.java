import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.*;

public class ParallelCompute {

	public static BigInteger[] parallelComputeFunctions(BigInteger[] data, Function<BigInteger, BigInteger>[] functions, int threadCount) 
			throws NullPointerException, InterruptedException {
		if(data==null||functions==null)
			throw new NullPointerException("No input");
		if (data.length==0||functions.length == 0||data.length!= functions.length||threadCount<=0)
			throw new IllegalArgumentException("Argument is not correct!");


		BigInteger[] resultArray= new BigInteger[data.length];
		List <ComputeThread> list = new ArrayList<ComputeThread>();

		threadCount = Math.min(data.length, threadCount);

		// Number of functions inside a single thread
		int threadSize = data.length/threadCount;
		int extraThreadNum = data.length % threadCount;

		// Start and end indices for each thread
		int[] start_index = new int[threadCount];
		int[] end_index = new int[threadCount];
		for(int i=0; i<threadCount; i++){
			if(i<extraThreadNum){
				end_index[i] = start_index[i] + threadSize + 1;
				start_index[i+1] = end_index[i];
			}
			else{
				end_index[i] = start_index[i] + threadSize;
				if(i == threadCount-1) break;	
				start_index[i+1] = end_index[i];
			}		
		}


		for (int i=0; i<threadCount; i++) {
			ComputeThread compute = new ComputeThread(data, start_index[i], Math.min(end_index[i], data.length), functions);	
			// System.out.println("Thread "+i+" range: "+start_index[i]+","+end_index[i]);	
			compute.start();
			list.add(compute);
		}
		
		/* Anonymous inner class */
		// 1.Create ```threadCount``` threads and start()
		// for(int i=0; i<threadCount; i++){		// ```threadCount``` threads
		// 	new Thread(){
		// 		@Override
		// 		public void run(){
		// 			i++;
		// 			for(int j=0; j<threadSize; j++){			// Inside each thread
		// 				// Apply function on input data, generating result
		// 				resultArray[i*threadSize + j] = functions[i*threadSize + j].apply(data[i*threadSize + j]);			
		// 			}
		// 		}
		// 	}.start();
		// }

		try{
			for (int i=list.size()-1; i>=0; i--) {
				list.get(i).join();
				System.out.println("=== Thread "+ i +" done!");
			}
		} catch(InterruptedException e){
			System.out.println("InterruptedException!");
		}
		
		// Combine all sub-results into a whole result array
		int p=0;
		for(int i=0; i<threadCount; i++){		
			ComputeThread c = list.get(i);	
			for(int j=0; j < c.size; j++){
				resultArray[p++] = c.results.get(j);		// Local position map to global position
			}
		}
		return resultArray;
	}


	public static BigInteger parallelReduceArray(BigInteger[] data, BinaryOperator<BigInteger> binOp, int threadCount)
			throws InterruptedException {
		
		threadCount = Math.min(data.length/2, threadCount);		// Limit maximum number of thread to half of data.length
		int length = data.length;

		while(threadCount > 0){
			
			List<ReduceThread> thr_list = new ArrayList<ReduceThread>();

			// 1.Distribute workload
			long startTime = System.nanoTime();
			int threadSize = length/threadCount;		// Number of elements inside a single thread
			int extraThreadNum = length % threadCount;

			int[] start_index = new int[threadCount];
			int[] end_index = new int[threadCount];
			for(int i=0; i<threadCount; i++){
				if(i<extraThreadNum){
					end_index[i] = start_index[i] + threadSize + 1;
					start_index[i+1] = end_index[i];
				}
				else{
					end_index[i] = start_index[i] + threadSize;
					if(i == threadCount-1) break;	
					start_index[i+1] = end_index[i];
				}		
			}
			long endTime = System.nanoTime();        
			// System.out.println(" +++ Performance timing 1: " + (endTime - startTime) + " ns");



			// 2.Create threadCount threads & start
			startTime = System.nanoTime();
			for (int i=0; i<threadCount; i++) {
				ReduceThread thr = new ReduceThread(data, start_index[i], end_index[i], binOp);		
				thr.start();
				// System.out.println("=== Thread "+i+ " started!");
				thr_list.add(thr);
			}
			endTime = System.nanoTime();        
			// System.out.println(" +++ Performance timing 2: " + (endTime - startTime) + " ns");


			// 3.Wait for threadCount results & Store sub-results in data
			startTime = System.nanoTime();
			try{
				for (int i=0; i<threadCount; i++) {
					thr_list.get(i).join();
					// System.out.println("=== Thread "+ i +" done!");
					data[i] = thr_list.get(i).sub_result;
				}
			} catch(InterruptedException e){
				System.out.println("InterruptedException!");
			}
			endTime = System.nanoTime();        
			// System.out.println(" +++ Performance timing 3: " + (endTime - startTime) + " ns");


			// 4.Again, perform parallel operations on results (fan-in)
			// Only need threadCount/2 threads iteratively
			length = threadCount;
			threadCount /= 2;	 
		}

		return data[0];
	}

	public static void main(String[]args) {
		
		System.out.println("+++++++++++++ Test: parallelComputeFunctions +++++++++++++");
		
		/* Testing 1 */
		int [] a1 = new int [] {0,1,2,3,4,5,6,7};
		BigInteger [] data = Arrays.stream(a1).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
		Function[] functions = {(x)->BigInteger.valueOf(0),
								(x)->BigInteger.valueOf(11),
								(x)->BigInteger.valueOf(22),
								(x)->BigInteger.valueOf(33),
								(x)->BigInteger.valueOf(44),
								(x)->BigInteger.valueOf(55),
								(x)->BigInteger.valueOf(66),
								(x)->BigInteger.valueOf(77)};

		/* Testing 2 :large array */
		// int [] a1 = new int [1000];
		// Arrays.fill(a1, 2);
		// BigInteger [] data = Arrays.stream(a1).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);

		// Function[] functions = new Function[1000];
		// Function<BigInteger, BigInteger> f = x -> x.pow(2);
		// Arrays.fill(functions, f);
		
		
		long startTime = System.nanoTime();
		try{
			BigInteger[] res = parallelComputeFunctions(data, functions, 2);
			System.out.println("\n====== Result ======");
			for (int i=0;i<res.length;i++) {
				System.out.print(res[i]+" ");
			}
		} catch (Exception e) {System.out.println(e);} 
		long endTime = System.nanoTime();        
		System.out.println(" \n+++ Performance timing: " + (endTime - startTime) + " ns");

		
		
		System.out.println("\n\n+++++++++++++ Test: parallelReduceArray +++++++++++++");
		// int [] a2 = new int [10000];
		// Arrays.fill(a2, 1257421);
		// BigInteger [] data2 = Arrays.stream(a2).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);

		BinaryOperator<BigInteger> bop = (a, b) -> a.multiply(b);		// Creating a BinaryOperator with Lambda expression
		try{		
			// Different number of threads
			for(int i=1; i<10; i++){

				int [] a2 = new int[10];
				Arrays.fill(a2, 2);

				BigInteger [] data2 = Arrays.stream(a2).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
				startTime = System.nanoTime();
				BigInteger res2 = parallelReduceArray(data2, bop, i);
				endTime = System.nanoTime();        
				System.out.println(" +++ Total Performance timing (threadCount = " + i + "): " + (endTime - startTime) + " ns\n");
				System.out.println("Result(threadCount = " + i + "): " + res2 + "\n==============\n");
			}
		} catch(Exception e){}

	}
}
