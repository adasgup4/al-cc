//java.util is used for the data structures such as Hashtable, LikedHashSet, etc as well as Iterators, 
//java.io is used for file input, bufferedreader etc

import java.util.*;
import java.io.*;

//Apartment List Coding Challenge (ALCC)

public class alcc
{
	// This line of code is used to calculate runtime

	static long startTime = System.currentTimeMillis();

	/*
	 * Defining the variable that will hold the final value
	 * Defining the seed word, in this case 'LISTY'
	 * Defining the HashMap that will contain all the data in the ordered format (based on number of letters)
	 * Defining the list that will store all words that are part of the social network
	 */

	static int socialnetwork = 0;
	static String seed = "LISTY";
	static Hashtable<Integer, LinkedHashSet<String>> data = new Hashtable<>();
	static Queue<String> SocNet = new LinkedList<String>();

	// Main function -> This is where we begin reading the file, and initiate the calculation of Edit Distance
	// Once that function is done iterating, the result is printed out from this main function

	public static void main(String[] args)
	{
		// Reading the data from the file into a data structure
		
		FileReader fr = null;
		BufferedReader br = null;
		try
		{
			File dict = new File("dictionary.txt");
			fr = new FileReader(dict);
			br = new BufferedReader(fr);
		}
		// This try catch block was needed to avoid a situation where the file could not be found.
		catch(FileNotFoundException ex)
		{
			System.err.println("File Not Found: " + ex.getMessage());
		}

		/*
		 * The data structure that is best for such a purpose would be one that is easy to search and delete from.
		 * Most often that works with a Hash Table (or Hash Map). This has been defined above.
		 */
		
		LinkedHashSet<String> setw;
		ArrayList<String> listw = new ArrayList<String>();;
		String word;

		// Parse through the contents of the file and store in the Hashtable.
		// Also, while saving the words the first time, go through the first iteration of findingFriends for LISTY.
		
		try
		{
			while ((word = br.readLine()) != null)
			{
				// Add the word into the word list
				listw.add(word);
				//If the Hashtable already contains a key with this length value, add the word to that key
				if (data.containsKey(word.length()))
				{
					setw = data.get(word.length());
					setw.add(word);
				}
				// If not, create a new key with the value set to the LinkedHashSet<string> type, and add the word there. Then, add that to the overall Hashtable.
				else
				{
					setw = new LinkedHashSet<String>();
					setw.add(word);
					data.put(word.length(), setw);
				}
				// This is where we calculate the first round of indingFriends, only for those words that are +/- 1 letter in size compared to the seed word.
				if (Math.abs(word.length() - seed.length()) <= 1)
				{
					if (editDistance(word,seed) == 1)
					{
						SocNet.add(word);
					}
				}
			}
			//run the isFriend() function once all the initial words have been added into SocNet
			runIsFriend();
		}
		// This try catch block was required to prevent the IOexceptions that might occur when reading the file
		catch (IOException ex)
		{
			System.err.println("Error Reading File: " + ex.getMessage());
		}

		// This part of the code is used to calculate runtime
		
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

		// This line prints out the final size of the social network
		System.out.println(socialnetwork);
	}

	/*
	 * This function is used to go through the entire set of SocNet, and find all of those words' friends.
	 * This calls upon isFriend() which actually goes through adding, editing and deleting to find friends
	 */

	public static void runIsFriend()
	{
		// We continue running this function while our SocNet queue has contents in it
		// Since new friends are constnatly added here, this becomes our iterator and this function continues to run
		while (!SocNet.isEmpty())
		{
			String nextfriend = SocNet.poll();
			isFriend(nextfriend);
		}
	}

	/*
	 * This function sends words to the check function from three different values on the Hashtable
	 * The three values are the length, and the length +/- 1, for the three operations. Each word is tested.
	 * If the word is a friend, it is added to the social network, and the count of the size of our social network increases
	 * If not, the program moves on.
	 */

	public static void isFriend(String nextfriend)
	{
		// Storing the words that are relevant
		// removing yields length - 1,
		// editing yields length
		// adding yields length + 1

		int removesize = nextfriend.length() - 1;
		int editsize = nextfriend.length();
		int addsize = nextfriend.length() + 1;

		// These three linkedHashSets are used to store the set of values that are of the right size
		
		LinkedHashSet remove = data.get(removesize);
		LinkedHashSet edit = data.get(editsize);
  		LinkedHashSet add = data.get(addsize);

  		// An iterator is then set up to go through the linkedHashSets and identify friends
  		// (for each of the three cases)

  		Iterator remiter = null;
		if (remove != null)	remiter = remove.iterator();
  		else remiter = null; 

  		if (remiter != null)
  		{
  			while (remiter.hasNext())
  			{
  				String nextword = (String)remiter.next();
  				if (editDistance(nextfriend, nextword) == 1)
  				{
  					SocNet.add(nextword);
  					remiter.remove();
  					socialnetwork++;
  				}
  			}
  		}

  		Iterator edititer = null;
		if (edit != null) edititer = edit.iterator();
  		else edititer = null; 

  		if (edititer != null)
  		{
  			while (edititer.hasNext())
  			{
  				String nextword = (String)edititer.next();
  				if (editDistance(nextfriend, nextword) == 1)
  				{
  					SocNet.add(nextword);
  					edititer.remove();
  					socialnetwork++;
  				}
  			}
  		}

  		Iterator additer = null;
		if (add != null) additer = add.iterator();
  		else additer = null; 

  		if (additer != null)
  		{
  			while (additer.hasNext())
  			{
  				String nextword = (String)additer.next();
  				if (editDistance(nextfriend, nextword) == 1)
  				{
  					SocNet.add(nextword);
  					additer.remove();
  					socialnetwork++;
  				}
  			}
  		}
	}


	/* 
	 * Twostrings a, b: lengths i and j respectively
	 * Defining the Edit-Distance or Levenshtein Distance based on the formula:
	 * Lev_a,b(i, j) = max(i, j) if min(i, j) = 0
	 * Lev_a,b(i, j) = min(x, y, z) otherwise
	 * x = Lev_a,b(i - 1, j) + 1
	 * y = Lev_a,b(i, j - 1) + 1
	 * z = Lev_a,b(i - 1, j - 1) + 1_(a_i!=b_j)
	 * This can be calculated using a matrix mathod, where the matrix has i+1 rows and j+1 columns, and the edit-distance between each pair is calculated and stored in the matrix
	 * That method has been attempted below.
	 * The last term of 'z' is calculated using 'cost' which is 0 if the letters are the same, and 1 if they are different.
	 * 
	 * SAMPLE MATRIX: Levenshtein Distance of FRIST vs LISTY = 3
	 * 
	 * | | |L|I|S|T|Y|
	 * | |0|1|2|3|4|5|
	 * |F|1|1|2|3|4|5|
	 * |R|2|2|2|3|4|5|
	 * |I|3|3|2|3|4|5|
	 * |S|4|4|3|2|3|4|
	 * |T|5|5|4|3|2|3| <-- This number is the Edit Distance or Levenshtein Distance
	 * 
	 * As we can see, the Levenshtein distance is formed in the bottom right square as '3'.
	 */

	public static int editDistance(String a, String b)
	{
		//Step 1: set up conditions for when one word length is 0, and also set up a matrix
		int i = a.length();
		int j = b.length();
		if (i == 0) return j;
		if (j == 0) return i;
		int[][] matrix = new int[i+1][j+1];
		//Step 2: initialize first row and column of matrix:
		matrix[0][0] = 0;
		for (int count = 1; count < i+1; count++)
		{
			matrix[i][0] = i;
		}
		for (int count = 1; count < j+1; count++)
		{
			matrix[0][j] = j;
		}
		// Step 3: Check each character of String a vs each character of b
		// If a[i] == b[j], cost = 0;
		// If a[i] != b[j], cost = 1;
		int cost = 0;
		for (int counti = 1; counti < i+1; counti++)
		{
			for (int countj = 1; countj < j+1; countj++)
			{
				if (a.charAt(counti-1) == b.charAt(countj-1)) cost = 0;
				else cost = 1;

				// This is where we take the minimum of the three parameters that make up the Levenshtein Distance formula

				int smaller_neighbor = Math.min(matrix[counti-1][countj] + 1, matrix[counti][countj-1] + 1);
				matrix[counti][countj] = Math.min(smaller_neighbor, matrix[counti-1][countj-1] + cost);
			}
		}
		// Step 4: Distance should be the number in the last row and column of the matrix
		return matrix[i][j];
	}
}