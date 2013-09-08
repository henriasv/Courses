public class BinarySearchTree<T extends Comparable<? super T>>
{
	private static class BinaryNode<T>
	{
		// Constructor for leaf node
		BinaryNode(T theContent)
		{
			this(theContent, null, null);
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

	private BinaryNode<T> root;

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

	public void insert(T x)
	{	
		root = insert(x, root);
	}

	public void printTree()
	{
		printTree(root);
	}

	private void printTree(BinaryNode<T> t)
	{
		if (t != null)
		{
			printTree(t.left);
			System.out.println(t.content);
			printTree(t.right);
		}
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
			return contains(x, t.left);
		else if (compareResult > 0)
			return contains(x, t.right);
		else
			return true;
	}

	private BinaryNode<T> insert(T x, BinaryNode<T> t)
	{
		// If the roor does not exist, ie the tree is empty
		if (t == null)
			return new BinaryNode<T>(x, null, null);
		
		int compareResult = x.compareTo(t.content);
		
		if(compareResult < 0)
			t.left = insert(x, t.left);
		else if (compareResult > 0)
			t.right = insert(x, t.right);
		else
			;

		return t;
	}

}

class TestBinaryTree
{
	public static void main(String[] args)
	{
		String[] words = {"hei", "deg", "dette", "er", "en", "samling", "med", "ord"};
		BinarySearchTree<String> tree = new BinarySearchTree<String>(); 
		for (int i = 0; i<words.length; i++)
			tree.insert(words[i]);
		tree.printTree();
	}
}
