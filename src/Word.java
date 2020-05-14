import java.util.ArrayList;

public class Word {

	//Array of next level words on the trigram
	private ArrayList<Word> nextLevel = null;
	//This word
	private String thisWord = "";
	//Number of times this word occured in the sequence
	private int wordCount = 1;

	Word(String word) {
		this.thisWord = word;
	}

	public void createNextLevel(Word word) {
		nextLevel = new ArrayList<>();
		nextLevel.add(word);
	}

	public ArrayList<Word> getNextLevel() {
		return nextLevel;
	}
	
	public void incrementCount() {
		wordCount++;
	}
	public int getCount() {
		return wordCount;
	}
	public String getString() {
		return thisWord;
	}
	@Override
	public String toString() {
		return thisWord;
	}

	@Override
	public boolean equals(Object o) {
		Word word = (Word)o;
		return thisWord.equals(word.toString());
	}

	@Override
	public int hashCode() {
		return thisWord.hashCode();
	}
}
