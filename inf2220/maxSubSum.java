/**
 * Linear-time maximum contigous subsecuwnce algorithm.
 */





class Run {
	public static void main(String[] args)
	{
		int[] a = {1, -3, 4, 5, 2, -3, -5, 6, 19};
		int b = maxSubSum(a);
		System.out.println(b);
		return;
	}

	public static int maxSubSum( int[] a)
	{	
		int maxSum = 0, thisSum = 0;

		for (int j= 0; j<a.length; j++) {
			thisSum+=a[j];
			if(thisSum > maxSum)
				maxSum = thisSum;
			else if (thisSum < 0) 
				thisSum = 0; 

		}
		return maxSum;
}
}