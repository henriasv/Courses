import java.util.Scanner;
import java.io.File;

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

	public void remove(T x)
	{
		root = remove(x, root);
	}

	public T findMin()
	{
		return findMin(root).content;
	}

	public T findMax()
	{
		return findMax(root).content;
	}

	public void printTree()
	{
		printTree(root);
	}

	private BinaryNode<T> findMin(BinaryNode<T> t)
	{
		if (t==null)
			return null;
		else if (t.left == null)
			return t;
		return findMin(t.left);
	}

	private BinaryNode<T> findMax(BinaryNode<T> t)
	{
		if (t != null)
			while (t.right != null)
				t = t.right;

		return t;  
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

	private BinaryNode<T> remove(T x, BinaryNode<T> t)
	{
		if (t==null)
			return t; // Nothing to remove
		int compareResult = x.compareTo(t.content);

		if (compareResult < 0)
		{
			t.left = remove(x, t.left);
		}
		else if (compareResult > 0)
		{
			t.right = remove(x, t.right);
		}
		else if (t.left != null && t.right != null) // two children
		{
			t.content = findMin(t.right).content;
			t.right = remove(t.content, t.right);
		}
		else 
			t = (t.left != null) ? t.left : t.right; // ternary operator
		return t;
	}

}

class TestDictionary
{
	public static void main(String[] args)
	{
		String filename = args[0];
		dict = new Dictionary(filename);
		dict.print();
		run();
	}

	static Dictionary dict;

	public static void run()
	{
		Scanner sc = new Scanner(System.in);
		while (true)
		{	
			System.out.println("Enter a word: ");
			String word = sc.next();
			boolean isFound = dict.search(word);
			if (isFound)
				System.out.println("Found word");
			else
				System.out.println("Conldnt find word");
		}
	}
}

class Dictionary
{
	public Dictionary() 
	{

	}

	public Dictionary(String filename)
	{
		readFile(filename);
	}

	BinarySearchTree<String> tree;

	public void readFile(String filename)
	{
		System.out.println("Reading words from " + filename);
		File inFile = null;
		try
		{
			inFile = new File(filename);
		}
		catch (NullPointerException e)
		{
			System.out.println("File not found!");
			System.exit(1);
		}

		Scanner scanner = null;
		try 
		{
			scanner = new Scanner(inFile);
		}
		catch (java.io.FileNotFoundException e)
		{
			System.out.println("File not found!");
			System.exit(1);
		}

		tree = new BinarySearchTree<String>(); 
		while (scanner.hasNext())
			tree.insert(scanner.next());

	}

	public void print()
	{
		tree.printTree();
	}


	public boolean search(String word)
	{
		if(tree.contains(word))
		{
			return true;
		}
		else
			return false;

	}
}


