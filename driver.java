
import java.io.*;
import java.util.*;

public class driver {
	//variables
	static Scanner scan = new Scanner(System.in);
	static String line;
	static KeyWord[] keys, hashedKeys;
	static Stack<KeyWord> keyStack = new Stack<KeyWord>();
	static Stack<KeyWord> addedStack = new Stack<KeyWord>();
	static Stack<Character> letterStack = new Stack<Character>();
	static Letter[] letters;
	static boolean second, second2 = false;
	static int wordCount, totalLines, totalWords, totalKeyWords, maxValue, tableSize = 0;

	//files
	static File keyFile = new File("keys.txt");
	static File textFile = new File("text.txt");
	static Scanner inputKeys = null;
	static Scanner inputText = null;
	
	
	public static void main(String args[]) throws IOException{
		int i = -1;
		//check for files
		try {
				i++;
				inputKeys = new Scanner(keyFile);
				i++;
				inputText = new Scanner(textFile);
		} catch(FileNotFoundException e) {
			if(i == 0) {
				System.out.println("keys.txt not found!");
				System.exit(0);
			}
			else if(i == 1)	{
				System.out.println("text.txt not found!");
				System.exit(0);
			}
			else {
				System.out.println("A file was not found");
				System.exit(0);
			}
		}
		//save, count and sort keys
		createKeys();
		//hash the keys
		hashKeys();
		//read text file and count
		textRead();
		//print table
		printTable();
		
		
	}

	//hash function
	private static void hashKeys() {
		//define variables and initialize arrays
		hashedKeys = new KeyWord[keys.length];
		tableSize = hashedKeys.length;
		maxValue = tableSize/2;
		boolean added = false;
		
		//put the keys into a stack
		for(int i = keys.length-1; i >= 0; i--) {
			keyStack.push(keys[i]);
		}
		
		//goes though each key and finds a spot in the hash array
		while(!keyStack.isEmpty()){
			KeyWord temp = keyStack.pop();
			Letter first = findFirst(temp);
			Letter last = findLast(temp);
			added = false;
			//loop until a spot is found
			while(added == false) {
				if(hashedKeys[(temp.word.length() + first.gValue + last.gValue)%tableSize] == null) {
					hashedKeys[(temp.word.length() + first.gValue + last.gValue)%tableSize] = temp;
					addedStack.push(temp);
					first.gValue = 0;
					last.gValue = 0;
					added = true;
					if(second) {
						second = false;
						second2 = true;
					}
				}
				//if spot not found at first increment g values
				else {
					while(first.gValue != maxValue && second != true && added != true) {
						first.gValue++;
						//if spot found enter key
						if(hashedKeys[(temp.word.length() + first.gValue + last.gValue)%tableSize] == null) {
							hashedKeys[(temp.word.length() + first.gValue + last.gValue)%tableSize] = temp;
							addedStack.push(temp);
							first.gValue = 0;
							added = true;
						}
						//if second pass then increment last g values
						if(second2) {
							last.gValue++;
							if(hashedKeys[(temp.word.length() + maxValue + last.gValue)%tableSize] == null) {
								hashedKeys[(temp.word.length() + maxValue + last.gValue)%tableSize] = temp;
								addedStack.push(temp);
								first.gValue = 0;
								second2 = false;
								added = true;
							}
						}
						//if spot not first found then incremnt first g values
						if(first.gValue == maxValue && second == false && added == false) {
							first.gValue = 0;
							keyStack.push(temp);
							temp = addedStack.pop();
							hashedKeys[(temp.word.length() + first.gValue + last.gValue)%tableSize] = null;
							first = findFirst(temp);
							last = findLast(temp);
							first.gValue++;
							second = true;
						}
					}
				}
			}
		}
	}

	//find the first letter of the word in the letter array
	private static Letter findFirst(KeyWord temp) {
		for(int i = 0; (i < letters.length) || (letters[i] != null); i++)
			if(letters[i].letter == temp.word.charAt(0))
				return letters[i];
		
		return null;
	}

	//find last letter of the word in letter array
	private static Letter findLast(KeyWord temp) {
		for(int i = 0; (i < letters.length) || (letters[i] != null); i++)
			if(letters[i].letter == temp.word.charAt(temp.word.length()-1))
				return letters[i];
		
		return null;
	}

	//print the table of results
	private static void printTable() {
		//print table of stats
		System.out.println("*********************\n**** Statistics *****\n*********************");
		System.out.println("Total Lines Read: " + totalLines);
		System.out.println("Total Words Read: " + totalWords);
		System.out.println("Break Down by Key Word: ");
		for(int i = 0; i < wordCount; i++) {
			System.out.println("\t" + hashedKeys[i].word + ": " + hashedKeys[i].count);
			totalKeyWords+=hashedKeys[i].count;
		}
		System.out.println("Total Key Words: " + totalKeyWords);
	}

	//read the text and count words and keys
	private static void textRead() {
		String token;
		//check if file in folder
		try {
			inputText = new Scanner(textFile);
		} catch(FileNotFoundException e) {
			System.out.println("text.txt not found!");
			System.exit(0);
		}
		
		//go through each line
		while(inputText.hasNext()) {
			line = inputText.nextLine();
			//cont total lines
			if(line.compareTo("") != 0)
				totalLines++;
			
			//tokenize the string and find if the word is a key 
			StringTokenizer fileList = new StringTokenizer(line, " ,", false);
			while(fileList.hasMoreTokens()) {
				token = fileList.nextToken();
				token = token.toLowerCase();
				//increment word counter
				totalWords++;
				checkHash(token);
			}
		}
	}

	//check if the word from the text is hashed
	private static void checkHash(String word) {
		int gFirst = 0, gLast = 0, index = 0;
		
		//go until the g values are max
		while(gFirst <= maxValue && gLast <= maxValue) {
			index = (word.length() + gFirst + gLast)%tableSize;
			if(hashedKeys[index].word.compareTo(word) == 0) {
				hashedKeys[index].count++;
				return;
			}
			//if the word doesnt hash at first then increment gcounter and try again
			if(gFirst != maxValue)
				gFirst++;
			else 
				gLast++;
		}
	}

	//read the keys from the file and count them and hash them
	private static void createKeys() {
		//read the amount of keys and count the first and last letters
		while(inputKeys.hasNext()) {
			inputKeys.nextLine();
			wordCount++;
		}
		inputKeys.close();
		
		try {
			inputKeys = new Scanner(keyFile);
		} catch (FileNotFoundException e) {
			System.out.println("keys.txt not found!");
			System.exit(0);
		} 

		//create array for keys and letters
		keys = new KeyWord[wordCount];
		
		//for each word count the first and last letters
		int i = 0;
		while(inputKeys.hasNext() && i < wordCount) {
			line = inputKeys.nextLine();
			line = line.toLowerCase();
			keys[i] = new KeyWord(line);
			i++;
		}
		//count the first and last letters of the keys
		countLetters();
		
		//calculate frequency for each key
		for(int w = 0; w < keys.length; w++) {
			keys[w].frequency = frequencyCalc(keys[w].word);
		}
		//sort the keys by frequency
		sort();
	}

	//sort the key array (bubble sort)
	private static void sort() {
		int n = keys.length; 
        for (int i = 0; i < n-1; i++) 
            for (int j = 0; j < n-i-1; j++) 
                if (keys[j].frequency < keys[j+1].frequency) 
                { 
                    // swap
                    KeyWord temp = keys[j]; 
                    keys[j] = keys[j+1]; 
                    keys[j+1]= temp; 
                } 
	}

	//calculate the first and last letter frequency
	private static int frequencyCalc(String word) {
		int first = 0;
		int last = 0;
		
		for(int i = 0; i < letters.length; i++) {
			if(letters[i] == null)
				break;
			if(letters[i].letter == word.charAt(0))
				first = letters[i].count;
			if(letters[i].letter == word.charAt(word.length()-1))
				last = letters[i].count;
		}
		
		return first + last;
	}

	//count how many times the first and last letters appear in the keys
	private static void countLetters() {
		for(int i = 0; i < wordCount; i++) {
			String word = keys[i].word;
			letterStack.push(new Character(word.charAt(0)));
			letterStack.push(new Character(word.charAt(word.length()-1)));
		}
		letters = new Letter[letterStack.size()];
		while(letterStack.size() != 0) {
			for(int i = 0; i < letters.length; i++) {
				if(letters[i] == null) {
					letters[i] = new Letter(letterStack.pop());
					break;
				}
				else if(letters[i].letter == letterStack.peek()) {
					letterStack.pop();
					letters[i].count++;
					break;
				}
			}
		}
	}
}
