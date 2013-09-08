public class BinarySearchTree<T extends Comparable<? super T>>
{
	private static class BinaryNode<T>
	{
		// Constructor for leaf node
		BinaryNode(T theContent)
		{
			this(theContent, null, null)
		}

		// Constructor for node with children
		BinaryNode(T theContent, BinaryNode left_child_tree, BinaryNode right_child_tree)
		{
			content = theContent;
			left = left_child_tree; 
			right = right_child_tree;
		}

		T content;
		BinaryNode<T> left;
		BinaryNode<T> right;
	}

	public BinarySearchTree() 
	{
		root = null;
	}

	/** Public interface to finding an element in the tree
	 *
	 */
	public boolean contains(T x)
	{
		return contains(x, root);
	}

	/** Recursive function to find element in a subtree
	 * @param x, item to be found
	 * @param t, subtree to be checked
	 * @return true if x if found, false otherwise
	 */
	private boolean contains(T x, BinaryNode<T> t)
	{
		if (t == null)
			return false;

		int compareResult = x.compareTo(t.content);

		if(compareResult < 0) 
			return contains(x, left);
	}

} 