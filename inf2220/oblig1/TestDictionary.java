import java.util.Scanner;
import java.io.File;
import java.util.*;

public class TestDictionary
{
	public static void main(String[] args)
	{
		String filename = null;
		try
		{
			filename = args[0];
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Please provide filepath for dictionary text file when starting the program");
			System.exit(1);
		}
		try
		{
			dict = new Dictionary(filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Dictionary file not found");
			System.exit(1);
		}
		// To fulfill special requirement to remove and insert a word.
		dict.oblig1init();
		run();
	}

	static Dictionary dict;

	public static void run()
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("This is a dictionary. You will be prompted to enter words, and those words will be spell-checked. If you mistype a word, and it is reasonably similar to a word in the dictionary, you will be provided with suggestions for correct words. Exit the program by typing \"q\" \nTo start the program, type \"s\" and press enter, to quit, type anything else and press enter:");
		String s = sc.next();
		if (s.compareTo("s") != 0)
		{
			System.out.println("Quitting...");
			System.exit(0);
		}
		while (true)
		{	
			System.out.println("Enter a word: ");
			String word = sc.next();
			if (word.compareTo("q")==0)
				System.exit(0);
			boolean isFound = dict.search(word);
			System.out.println("-----------------------------------");
		}
	}
}

class BinarySearchTree<T extends Comparable<? super T>>
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

	/** Utility class to do statistics
	 * An instance of this class is passed through recursion when statistics about a binary three in obtained. 
	 */
	private class ThreeStatistics
	{

		ThreeStatistics()
		{
			depth = 0;
			meanDepth = 0;
			num_traversed = 0;
			firstElement = null;
			lastElement = null;
			current_depth = 0;
			nodesOnEachDepth = new ArrayList<Integer>(); // Magic size of array
		}
		// Data structure to carry statistics
		int depth;
		ArrayList<Integer> nodesOnEachDepth;
		double meanDepth;
		T firstElement;
		T lastElement;

		// Data when running statistics
		int current_depth;
		int max_depth;
		int sum_depth;
		int num_traversed;

		public void deeper() {current_depth ++;}
		public void shallower() {current_depth --;}
		
		public void stat()
		{
			num_traversed ++;
			sum_depth += current_depth;
			try
			{
				nodesOnEachDepth.set(current_depth, nodesOnEachDepth.get(current_depth)+1);
			} catch (IndexOutOfBoundsException e)
			{
				nodesOnEachDepth.ensureCapacity(current_depth+1);
				nodesOnEachDepth.add(current_depth, 1);
			}
		}
		public void statLeaf()
		{
			if (current_depth>max_depth)
				max_depth = current_depth;
		}

		public void update()
		{
			depth = max_depth;
			meanDepth = (double) sum_depth/size;
			firstElement = findMin();
			lastElement = findMax();
		}
	}

	private BinaryNode<T> root;
	private int size;

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

	public void printStatistics()
	{
		// depth
		// Nodes on each depth
		// Average depth
		// final and first word
		ThreeStatistics stat = statistics();
		System.out.println("Statistics on binary tree: ");
		System.out.println("Depth: " + Integer.toString(stat.depth));
		System.out.println("Mean node depth: " + Double.toString(stat.meanDepth));
		System.out.println("Nodes on each depth:");
		for (int i = 0; i<stat.nodesOnEachDepth.size(); i++)
		{
			System.out.print("\t"+Integer.toString(i)+":\t");
			System.out.println(Integer.toString(stat.nodesOnEachDepth.get(i)));
		}
		if (size == stat.num_traversed)
		{
			System.out.println("Number of nodes traversed is equal to the number of nodes registred in the binary tree: " + Integer.toString(size));
		}
		else
		{
			System.out.println("Number of nodes traversed is NOT equal to the number of nodes registred in the binary tree, \n Exiting!");
			System.exit(1);
		}
		System.out.println("----------------------------------------------------------");
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

	private boolean contains(T x, BinaryNode<T> t)
	{
		if (t == null)
			return false;

		int compareResult = x.compareTo(t.content);

		if (compareResult < 0) 
			return contains(x, t.left);
		else if (compareResult > 0)
			return contains(x, t.right);
		else
			return true;
	}

	private BinaryNode<T> insert(T x, BinaryNode<T> t)
	{
		// If the root does not exist, ie the subtree is empty
		if (t == null)
		{	
			size ++;
			return new BinaryNode<T>(x, null, null);
		}

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

	/** 
	 * @return ThreeStatistics<T>
	 */
	private ThreeStatistics statistics()
	{
		ThreeStatistics stat = new ThreeStatistics();
		BinaryNode<T> t = root;
		traverseForStatistics(t, stat);
		stat.update();
		return stat;
	}

	/**
	 * Traverses the binary three and counts statistics, helped by the ThreStatistics object.
	 */
	private void traverseForStatistics(BinaryNode<T> t, ThreeStatistics stat)
	{
		stat.stat();
		if (t.left != null)
		{
			stat.deeper();
			traverseForStatistics(t.left, stat);
		}
		if (t.right != null)
		{
			stat.deeper();
			traverseForStatistics(t.right, stat);
		}

		if (t.left == null && t.right==null) 
		{
			stat.statLeaf();
		}
		stat.shallower();
		return;
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
	String alphabet_string = "abcdefghijklmnopqrstuvwxyzæøå";
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

		tree.printStatistics();

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
			System.out.println("Found word: " + word);
			return true;
		}
		else
		{
			System.out.println("Word not found");
			long startTime = System.nanoTime();
			String[] similar_words = findSimilar(word);
			long timeUsed = System.nanoTime()-startTime;

			System.out.println("Found " + Integer.toString(similar_words.length) + " similar words in " + Long.toString(timeUsed/1000) + " us");
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
		// Add all similar words
		similar_words_list.addAll(findSimilarSwappedNextToEachother(word));
		similar_words_list.addAll(findSimilarReplacedOneLetter(word));
		similar_words_list.addAll(findSimilarRemovedOneLetter(word));
		similar_words_list.addAll(findSimilarAddedOneLetter(word));
		// Oneliner to remove duplicates. This solution is somewhat arbitrary, and there should be a better way.
		ArrayList<String> uniqueWords = new ArrayList<String>(new LinkedHashSet<String>(similar_words_list));
		String[] similar_words = (String[]) uniqueWords.toArray(new String[0]);
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


	// Below are utility functions, which support the findSimilar...something... methods.
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




