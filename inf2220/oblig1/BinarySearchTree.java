import java.util.Scanner;
import java.io.File;
import java.util.*;

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
		dict.oblig1init();
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
			System.out.println("-----------------------------------");
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
	String alphabet_string = "abcdefghijklmnopqrstuvwxyz";
	char[] alphabet = alphabet_string.toCharArray();

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

	public void oblig1init()
	{
		tree.remove("familie");
		tree.insert("familie");
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
		{
			System.out.println("Word not found");
			String[] similar_words = findSimilar(word);
			if (similar_words.length > 0)
			{
				System.out.println("Suggested similar words: ");
				for (String similar_word : similar_words)
					System.out.println(similar_word);
			}
			else
				System.out.println("No suggestions for similar words");
			return false;
		}
	}

	private String[] search(String[] words)
	{
		ArrayList<String> found_words = new ArrayList<>(); 
		for (String word : words)
		{
			if (tree.contains(word))
				found_words.add(word);
		}
		return found_words.toArray(new String[0]);
	}

	private String[] findSimilar(String word)
	{
		ArrayList<String> similar_words_list = new ArrayList<>();
		similar_words_list.addAll(findSimilarSwappedNextToEachother(word));
		similar_words_list.addAll(findSimilarReplacedOneLetter(word));
		similar_words_list.addAll(findSimilarRemovedOneLetter(word));
		similar_words_list.addAll(findSimilarAddedOneLetter(word));
		String[] similar_words = (String[]) similar_words_list.toArray(new String[0]);
		/* Magic solution with the new String[0]
		
		System.out.println("Printing similiar words:");
		for (int i = 0; i<similar_words.length; i++) 
		{
			System.out.println(similar_words[i]);
		}
		*/
		return search(similar_words);
	}

	private ArrayList<String> findSimilarSwappedNextToEachother(String word)
	{
		ArrayList<String> words = new ArrayList<>();

		for (int i = 0; i<word.length() - 1; i ++)
		{
			words.add(swap(i, i+1, word));
		}
		words.trimToSize();
		return words;
	}

	private ArrayList<String> findSimilarReplacedOneLetter(String word)
	{
		ArrayList<String> words = new ArrayList<>();
		for (int i = 0; i<word.length(); i++)
		{
			for (int j = 0; j<alphabet.length; j++)
			{
				if (word.charAt(i) != alphabet[j])
				{
					words.add(switchChar(i, alphabet[j], word));
				}

			}
		}
		words.trimToSize();
		return words;
	}

	private ArrayList<String> findSimilarRemovedOneLetter(String word)
	{
		ArrayList<String> words = new ArrayList<>();
		for (int i = 0; i<word.length()+1; i++)
			for (int j = 0; j<alphabet.length; j++)
				words.add(addChar(i, alphabet[j], word));
		words.trimToSize();
		return words;
	}

	private ArrayList<String> findSimilarAddedOneLetter(String word)
	{
		ArrayList<String> words = new ArrayList<>();
		for (int i = 0; i<word.length(); i++)
			words.add(removeChar(i, word));
		words.trimToSize();
		return words;
	}

	private String swap(int i, int j, String s)
	{
		char[] word_array = s.toCharArray();
		char ch_i = word_array[i];
		word_array[i] = word_array[j];
		word_array[j] = ch_i;
		return new String(word_array);
	}
	private String switchChar(int i, char c, String s)
	{
		char[] tmp = s.toCharArray();
		tmp[i] = c;
		return new String(tmp);
	}

	private String addChar(int i, char c, String s)
	{
		char[] tmp = s.toCharArray();
		char[] ret = new char[tmp.length+1];
		int j; // To be used in tweo for loops, hence declared
		
		for (j = 0; j < i; j++)
			ret[j] = tmp[j];

		ret[i] = c;
		
		for (j = i+1; j<ret.length; j++)
			ret[j] = tmp[j-1];
		return new String(ret);
	}

	private String removeChar(int i, String s)
	{
		char[] tmp = s.toCharArray();
		char[] ret = new char[tmp.length-1];
		int j;
		for (j = 0; j < i; j++)
			ret[j] = tmp[j];

		for (j=i; j<ret.length; j++)
			ret[j] = tmp[j+1];
		return new String(ret);

	}
}




