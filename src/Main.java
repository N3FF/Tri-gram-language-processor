import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// Justin Neff

public class Main {

	public static void main(String[] args) {

		BufferedReader in = null; 			// file reader
		Map<String, Word> map = null; 		// Map of the first words
		String[][] file = { 
				{ "alice", "9" }, 
				{ "doyle", "2" }, 
				{ "doyle2", "6" }, 
				{ "london", "18" },
				{ "melville", "8" } }; 		// Different files to read
		try {
			int fileNum = 0; 				//0:alice, 1:doyle, 2:doyle2, 3:london, 4: melville
			in = new BufferedReader(new FileReader(new File(".\\files\\" + file[fileNum][0] + ".txt")));
			String line;
			//skip the title of the files
			int headSize = Integer.parseInt(file[fileNum][1]);
			for (int i = 0; i < headSize; i++) {
				in.readLine();
			}
			
			// List of words read in from a line in the document
			List<String> wordList = null;
			map = new HashMap<>();
			// 3 word set for the trigram model
			Word word[] = new Word[3];
			//read in the document
			while ((line = in.readLine()) != null) {
				// break the line read in into an array
				wordList = Arrays.asList(line.toLowerCase().split(" "));
				// iterate through each word in the array
				Iterator<String> iter = wordList.iterator();
				while (iter.hasNext()) {
					//if the map is empty read the first three words in
					if (map.isEmpty()) {
						for (int i = 0; i < 3; i++) {
							String next = iter.next();
							word[i] = new Word(next);
						}
					//If map has words then rotate through the words and add them to the map
					} else {
						word[0] = new Word(word[1].toString());
						word[1] = new Word(word[2].toString());
						String s = iter.next();
						//some odd characters were coming up in the document and I was trying to get rid of some of them.
						while (s.hashCode() == 0 && iter.hasNext()) {
							s = iter.next();
						}
						word[2] = new Word(s);
					}

					//start by checking if the 1st word of the three word model is in the map
					if (map.containsKey(word[0].toString())) {
						map.get(word[0].toString()).incrementCount();
						// if the first word is found, search the array of second level words
						List<Word> levelTwo = map.get(word[0].toString()).getNextLevel();
						boolean found = false;
						int i;
						// if there is a level 2 search for the second level word in the list
						if (levelTwo != null) {
							// searching for word
							for (i = 0; i < levelTwo.size(); i++) {
								found = (levelTwo.get(i).toString()).equals(word[1].toString());
								if (found)
									break;
							}
							// move up to the third level if the word is in the list
							if (found) {
								// counting the number of occurances
								levelTwo.get(i).incrementCount(); 
								// move up to the third level array and search for the third word
								List<Word> levelThree = levelTwo.get(i).getNextLevel();
								if (levelThree != null) {
									for (i = 0; i < levelThree.size(); i++) {
										found = (levelThree.get(i).toString()).equals(word[2].toString());
										if (found)
											break;
									}
									// if the third level word is found then increment it's count, otherwise add the word to the list
									if (found) {
										levelThree.get(i).incrementCount();
									} else {
										levelThree.add(word[2]);
									}
								//If the second word wasn't in level two add it
								} else {
									levelTwo.get(i).createNextLevel(word[2]);
								}
							// if the second word wasn't in the map then add it and then the third
							} else {
								word[1].createNextLevel(word[2]);
								levelTwo.add(word[1]);
							}
						}
					// If none of the words were found then link the three together and put them in the map
					} else {
						word[1].createNextLevel(word[2]);
						word[0].createNextLevel(word[1]);
						map.put(word[0].toString(), word[0]);
					}
				}
			}
			// Build a string to output the story
			StringBuilder sb = new StringBuilder();
			// Randomly pick the first word
			String randomWord = (String) (map.keySet().toArray())[random(map.size())];
			sb.append(randomWord);
			Word first = map.get(randomWord);
			// get the second random word based on probabilities
			Word second = nextWord(first);
			addWord(sb, second.toString());
			//build a 1000 word story
			for (int i = 0; i < 1000; i++) {
				// get the first word of the trigram
				first = map.get(first.toString());
				// based on the previous two words generate the third
				Word third = getThirdWord(first, second);
				addWord(sb, third.toString());
				// offset the words to generate the next word.
				first = second;
				second = third;
			}
			System.out.print(sb.toString());
			// Output to file
			// BufferedWriter writer = new BufferedWriter(new FileWriter("Output2.txt"));
			// writer.write(sb.toString());
			// writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Add word to the story string builder
	private static void addWord(StringBuilder sb, String s) {
		sb.append(" " + s);
	}

	// randomly generate a number within a range
	private static int random(int size) {
		return (int) (Math.random() * size - 1);
	}

	// find the next word based on the previous one.
	private static Word nextWord(Word word) {
		// randomly generate a number to use with probablity selection
		int n = random(word.getCount());
		int i = 0;
		// selects the random word based on the occurance probability
		for (; n > 0; i++) {
			n = n - word.getNextLevel().get(i).getCount();
		}
		Word temp = null;
		if (i >= word.getNextLevel().size()) {
			temp = nextWord(word);
		} else {
			temp = word.getNextLevel().get(i);
		}
		return temp;
	}

	// Get the third word in the wheel using the two previous words.
	private static Word getThirdWord(Word a, Word b) {
		for (int i = 0; i < a.getNextLevel().size(); i++) {
			//look for the second word in the first words array of secondary words
			if (a.getNextLevel().get(i).toString().equals(b.toString())) {
				return nextWord(a.getNextLevel().get(i));
			}
		}
		return null;
	}
}
