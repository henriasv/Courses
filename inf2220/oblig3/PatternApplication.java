import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class PatternFinder {

	PatternFinder(String wildcard_) {
		wildcard = (wildcard_.toCharArray())[0];
		wildcardString = wildcard_;
	}

	char wildcard;
	String wildcardString;
	public int boyer_moore_horspool (char[] needle, char[] haystack) {
		return  boyer_moore_horspool_wildcard_easy (needle, haystack, 0);
	}

	public int boyer_moore_horspool_wildcard_easy (char[] needle, char[] haystack, int startpoint) {
		if (needle.length > haystack.length) {return -1;}

		// Find properties of the rightmost part of the needle, right of the last wiildcard
		String needleString = new String(needle);
		String[] needleStringSplitted = needleString.split("_");
		int length_last_part_of_needle = needleStringSplitted[needleStringSplitted.length-1].length();

		int[] bad_shift = new int[256];

		for (int i = 0; i<256; i++) {
			bad_shift[i] = length_last_part_of_needle;
		}

		int offset = startpoint, scan = 0;
		int last = needle.length - 1;
		int maxoffset = haystack.length - needle.length;

		for (int i = needle.length - length_last_part_of_needle; i<last; i++) {
			bad_shift[needle[i]] = last - i;
		}

		while (offset <= maxoffset) {
			for (scan = last; needle[scan] == haystack[scan+offset] || needle[scan] == wildcard; scan --) {
				if (scan == 0) {
					return offset;
				}
			}
			offset += bad_shift[haystack[offset+last]];
		}
		return -1;
	}

	public int find_wildcard() {
		return -1;
	}

} // End of classls


class PatternApplication {

	public static void main(String[] args)
	{
		Scanner sc_needle = null;
		Scanner sc_haystack = null;
		try {
			sc_needle = new Scanner(new File(args[0]));
			sc_haystack = new Scanner(new File(args[1]));
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(1);
		}
		char[] needle = sc_needle.next().toCharArray();
		char[] haystack = sc_haystack.next().toCharArray();
		System.out.println(needle);
		System.out.println(haystack);

		PatternFinder pfinder = new PatternFinder("_");

		int patoffset = 0;
		while (true)  {
			patoffset = pfinder.boyer_moore_horspool_wildcard_easy(needle, haystack, patoffset);
			if (patoffset == -1) {
				break;
			}
			System.out.println("Pattern found in haystack in position " + Integer.toString(patoffset));
			System.out.print(Arrays.copyOfRange(haystack, 0, patoffset));
			System.out.print("\033[1;31m");
			System.out.print((char[]) Arrays.copyOfRange(haystack, patoffset, patoffset+needle.length));
			System.out.print("\033[0m");
			System.out.println(Arrays.copyOfRange(haystack, patoffset+needle.length, haystack.length));
			patoffset ++; 
		}
	}
}