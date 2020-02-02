# Multithread

Implement the ***BigInteger [] parallelComputeFunctions (BigInteger [] data, Function <BigInteger, BigInteger> [] functions, int threadCount)*** method in **ParallelCompute**. The method should return an array that contains exactly as many elements as data. The result of the function at index i of functions applied to the element of data at index i should be at each index i of the returned array. For this purpose <u>**threadCount** threads</u> should be created. Spread the load on the threads sensibly, i.e. **the number of calculations to be performed should be as similar as possible for the threads.**

If data or functions is zero, a **NullPointerException** should be thrown. If data or functions have a length of 0 or a different length, or if threadCount is less than or equal to 0, an **IllegalArgumentException** should be thrown.

```
 Example with 99999 elements 0 of 1 tests passing
```

```
 Performance improvement through multiple threads 0 of 1 tests passing
```

Implement the **BigInteger parallelReduceArray method (BigInteger [] data, BinaryOperator <BigInteger> binOp, int threadCount)** in ParallelCompute. You can assume that the binOp that receives the method as a parameter is a **commutative and associative** operation if it is not null. Your method should perform a parallelized "reduce" on the element in data and return the result, i.e. the linkage of all elements of data by the binary operator binOp. For example, if binOp is an addition (or multiplication), the sum (or product) of all elements in the array is calculated and returned. See also slide 621 for an example implementation of a sequential reduce on the lecture's stream implementation.
**<u>ThreadCount threads</u>** should be created for the calculation. For this purpose, the threads should work on parts of the array that are as large as possible. The results of these threads can then be linked sequentially.
If data or binOp is zero, a **NullPointerException** should be thrown. If data has a length of 0, or if threadCount is less than or equal to 0, an IllegalArgumentException should be thrown.

```
 Test with addition as associative operation 0 of 1 tests passing
```

```
 Performance improvement through multiple threads (test with multiplication) 0 of 1 tests passing
```



## Hints

Use java.lang.Thread or your own subclasses for parallelization in this task. Do not use parallel streams here.

**Avoid writing to the same variable** in multiple parallel threads to avoid that the correctness of the calculation depends on the scheduling (time allocation) of the threads. Instead you can e.g. create an array that you pass to the threads and use so that none of the parallel threads access the same index of the array in writing. **(CREW)**

If an **InterruptedException** occurs in one of the last three methods to be implemented while waiting for threads (or elsewhere), you do not have to handle or catch them in your methods; Your method can simply throw them on.

If threadCount is **greater than or equal to n** (for Factorial) or data.length (for ParallelCompute), the methods should still work (if the arguments are in the legal range). In this case it is up to you whether you start more than n or data.length threads.

Note that n (for Factorial) or data.length (for ParallelCompute) is **not necessarily an integer multiple** of threadCount.

Do not artificially degrade the performance of the sequential implementation (e.g. by calling sleep or similar)! Otherwise, points may be deducted.
