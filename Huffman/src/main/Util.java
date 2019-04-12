package main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

 // Shared static classes

public class Util {
	
	 //Returns canonized mapping of character to their encoding based on character lengths
	 //params charLengths canonized mapping of character to their encoding
	 
	public static Map<Character, String> canonize(Map<Integer, List<Character>> charLengths) {
		Map<Character, String> prefixCodes = new HashMap<Character, String>();
		//convert hash map keys to list
		List<Integer> reverse = new ArrayList<Integer>(charLengths.keySet());
		Collections.reverse(reverse);
		
		int firstLen = reverse.get(0);
		String base = String.format("%0" + firstLen + "d", 0);
		
		for(Integer i : reverse) {
			// Find how many bits to shift
			int diff = base.length() - i.intValue();
			int binaryBase = new BigInteger(base, 2).intValue();
			
			// Right shift
			base = Integer.toBinaryString(binaryBase >> diff);
			base = adjust(base, i.intValue());
			
			List<Character> chars = charLengths.get(i);
			for(int j = 0; j < chars.size(); j++) {
				prefixCodes.put(chars.get(j), base);
				base = incrementBinaryString(base);
			}
		}
		return prefixCodes;
	}
	
//	Ensure that a string has a certain length. Pads if too small, chops off if too big.
//	param1 s the string to adjust
//	param2 length the desired length of the string
//	returns the string with desired length
	public static String adjust(String s, int length) {
		int diff = length - s.length();
		if(diff > 0) {
			return String.format("%0" + diff + "d", 0) + s;
		} else if (diff < 0) {
			diff = Math.abs(diff);
			return s.substring(diff, s.length());
		}
		return s;
	}
	
	
//	 Increments a binary string
//	 param1: base the base binary string
//	 returns the incremented binary string
	 
	public static String incrementBinaryString(String base) {
		
		String binaryString = Integer.toBinaryString(Integer.parseInt(base, 2) + 1);
		int desiredLength = base.length();
		
		binaryString = adjust(binaryString, desiredLength);
		return binaryString;
	}
	
	
//	 Swaps a map's keyset with the values
//	 param 1: map the map to swap
	
	public static <K,V> HashMap<V,K> swap(Map<K,V> map) {
	    HashMap<V,K> rev = new HashMap<V, K>();
	    for(Map.Entry<K,V> entry : map.entrySet())
	        rev.put(entry.getValue(), entry.getKey());
	    return rev;
	}
	
	//read a binary file
	//param1: binary file to read
	//param2: text file write (o/p)
	public static void BinaryToTxtConvert(File input, File output)throws FileNotFoundException,IOException {
		
	    Scanner sc = new Scanner(input);
	    
	    PrintWriter writer = new PrintWriter(output);
	    String lastString = "";

	    while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        lastString = lastString + line;
	    }
	    sc.close();
	        StringBuffer result = new StringBuffer();
	        for (int i = 0; i < lastString.length(); i += 8) {
	            result.append((char) Integer.parseInt(lastString.substring(i, i + 8), 2));
	        }
//	        System.out.println(result);
	        writer.write(result.toString());
	        writer.flush();
	        writer.close();
	        
	    
	}
	
	//read a txt file
	//param1: txt file to read
	//param2: binary file write (o/p)
	public static void TextToBinaryConvert(File input, File output)throws FileNotFoundException,IOException{
		
		Scanner sc = new Scanner(input);
	    
	    PrintWriter writer = new PrintWriter(output);
	    String lastString = "";

	    while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        lastString = lastString + line;
	    }
	    sc.close();
	        StringBuffer result = new StringBuffer();
	        for (int i = 0; i < lastString.length(); i += 1) {
	        	 result.append(lastString.charAt(i));
//	            System.out.println(result);
	        }
	        System.out.println(result);
	        writer.write(result.toString());
	        writer.flush();
	        writer.close();
	}
		
}
