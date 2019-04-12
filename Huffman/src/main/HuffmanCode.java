package main;


// HuffmanCode wrapper Node, simply stores a character and frequency
 
public class HuffmanCode implements Comparable<HuffmanCode> {
	
	private Character c;
	private int frequency;
	
	public HuffmanCode(char c, int frequency) {
		this.c = new Character(c);
		this.frequency = frequency;
	}
	
	public int getFrequency() {
		return this.frequency;
	}
	
	public char getCharacter() {
		return this.c;
	}
	
	public String toString() {
		return String.format("Huffman code c = %s frequency = %s", c, frequency);
	}

	@Override
	public int compareTo(HuffmanCode o) {
		return Integer.compare(frequency, o.frequency);
	}
	
	public int compareTo(int frequency) {
		return Integer.compare(this.frequency, frequency);
	}
}
