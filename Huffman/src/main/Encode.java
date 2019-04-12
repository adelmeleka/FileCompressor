package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.PriorityQueue;

public class Encode {
	
	// Exclamation mark
	public static final char EOF = (char) 0;
	
	static int origlength = 0;
	static int totalLength = 0;
	
	public static void encodeFolder(String folderName) throws Exception{
		//get current folder path
		String dirPath = new File("").getAbsolutePath()+"\\"+folderName;
		File dir = new File(dirPath);
		
		//create new compressed directory
		File theDir = new File(new File("").getAbsolutePath()+"\\compressed_"+folderName);
		theDir.mkdir();
		
		//iterate into each file and compress it 
		File[] directoryListing = dir.listFiles();	
		if (directoryListing != null) {
		  for (File child : directoryListing)
		  {
		      // Do something with each file child
			  encodeFile(folderName+"\\"+child.getName());
			  
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
		  }
	}
	
	public static void encodeFile(String fileName) throws Exception {
		long start = System.nanoTime();
		
		String source = new File("").getAbsolutePath()+"\\"+fileName;
//		System.out.println("sooo: "+source);
		
		File file = new File(source);
		System.out.println("//////////////////////////Compressing File: "+fileName+"/////////////////////////////////");
		try {
			
			//calculating frequency table
			Map<Character, Integer> frequencies = calculateFrequencies(file);
			
			//Printig frequency table
			System.out.println("-------------------------");
	        System.out.println("Character Frequency Table");
	        System.out.println("-------------------------");
			System.out.println(new PrettyPrintingMap<Character, Integer>(frequencies));
			
			List<HuffmanCode> codes = new ArrayList<HuffmanCode>();

			// Generate wrappers (hc objects)
			for (Entry<Character, Integer> entry : frequencies.entrySet()) {
				codes.add(new HuffmanCode(entry.getKey().charValue(), entry.getValue().intValue()));
			}
			
			//get sorted by frequency in order to build pq 
			Collections.sort(codes);
//			System.out.println("-----");
//			System.out.println("Codes");
//			System.out.println("-----");
//			System.out.println(codes);
			
			// Insert sorted wrappers into priority queue
			PriorityQueue<BinaryTree> pq = new PriorityQueue<BinaryTree>();
			for (HuffmanCode hc : codes) {
				BinaryTree tree = new BinaryTree(hc);
				pq.add(tree);
			}

			// Generate Huffman Tree
			BinaryTree huffmanTree = generateHuffmanTree(pq);
			
			// Generate canonized values
			Map<Integer, List<Character>> charLengths = getCharLengths(huffmanTree, codes);
//			System.out.println("----------");
//			System.out.println("charLength");
//			System.out.println("----------");
//			System.out.println(new PrettyPrintingMap<Integer, List<Character>>(charLengths));
			
			Map<Character, String> prefixCodes = Util.canonize(charLengths);
			
			//Printing Huffman code table
			System.out.println("-----------");
			System.out.println("Prefix Code");
			System.out.println("-----------");
			System.out.println(new PrettyPrintingMap<Character,String>(prefixCodes));
			
			//writring code to oput file
			if(fileName.contains("\\"))
			{
				int t = fileName.lastIndexOf('\\');
				System.out.println("sa7?");
				fileName = fileName.substring(0,t) +"\\\\compressed_"+fileName.substring(t+1,fileName.length());
						

			}
			String target = new File("").getAbsolutePath()+"\\compressed_"+fileName;
			generateOutputFile(file, target, prefixCodes);
			
//			origlength = 
			totalLength = (int) new File(target).length();
			
			long diff = System.nanoTime() - start;
			
			//printing Statistics
			System.out.println("----------------------");
			System.out.println("Compression Statistics");
			System.out.println("----------------------");
			 
	        System.out.println("Original File Size      "+origlength +" bytes");
	        System.out.println("Compressed File Size    "+totalLength +" bytes");
	        System.out.println("Compression Ratio       "+(((float)(origlength - totalLength)/origlength) * 100)+" %");
			System.out.println("Compression Time        "+ TimeUnit.NANOSECONDS.toMillis(diff) + " ms.");

		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
	}

	
//	  Calculates a mapping of character -> frequency given an input file 
//	  param: file the input file to read
//	  return a mapping of character -> frquency
	public static Map<Character, Integer> calculateFrequencies(File file) throws IOException {
		
		Map<Character, Integer> frequencies = new HashMap<Character, Integer>();
		
		
		try (InputStream in = new FileInputStream(file);
				Reader reader = new InputStreamReader(in,Charset.defaultCharset());
				Reader buffer = new BufferedReader(reader)) {
			int r;
			
			// -1 means EOF
			while ((r = reader.read()) != -1) {
				origlength ++;
				Character c = new Character((char) r);
				// Fastest to get and check for null
				Integer i = frequencies.get(c);
				if (i != null) {
					frequencies.put(c, new Integer(i + 1));
				} else {
					frequencies.put(c, new Integer(1));
				}
			}
		}
		// Attach EOF
		frequencies.put(EOF, new Integer(1));
		
//		//print number of bytes in original uncompressed message
//        System.out.println("--------------------");
//        System.out.println("Original data length");
//        System.out.println("--------------------");
//        System.out.println(origlength +" bytes");
//        
		return frequencies;
	}
	
	
//	 Generates the huffman tree
//	 param: pq a min priority queue of all the codes
//	 return: a huffman tree
	 
	public static BinaryTree generateHuffmanTree(PriorityQueue<BinaryTree> pq) {
		while (pq.size() > 1) {

			BinaryTree leftLeaf = pq.poll();
			BinaryTree rightLeaf = pq.poll();

			BinaryTree root = new BinaryTree(leftLeaf, rightLeaf);
			pq.offer(root);
		}

		return pq.poll();
	}
	

//	 Generates a mapping of character to code lengths based on a huffman tree
//	 param1 huffmanTree the tree to traverse to find encoding
//	 param2 codes the huffman codes to search for
	public static Map<Integer, List<Character>> getCharLengths(BinaryTree huffmanTree, List<HuffmanCode> codes) {
		Map<Integer, List<Character>> lengthToCharacters = new HashMap<Integer, List<Character>>();
		
		for (HuffmanCode hc : codes) {
			//get code & its length for all the huffman tree
			String binaryString = huffmanTree.getEncoding("", hc);
			int len = new Integer(binaryString.length());
			
			List<Character> characters = lengthToCharacters.get(len);
			Character c = hc.getCharacter();
			if(characters != null) {
				characters.add(c);
			} else {
				characters = new ArrayList<Character>();
				characters.add(c);
				lengthToCharacters.put(len, characters);
			}
		}
		
		for(List<Character> chars : lengthToCharacters.values()) {
			Collections.sort(chars);
		}
		
		return lengthToCharacters;
	}

	
//	 Generates a huffman encoded file. 
//	 param1: file The input file to encode
//	 param2: target The output file
//	 param3: prefixCodes The encoded values
//	 param4: charLengths the lengths of the encoded values
	public static void generateOutputFile(File file, String target, Map<Character, String> prefixCodes) throws FileNotFoundException, IOException {
		File outputFile = new File(target);
		outputFile.createNewFile();
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		BufferedOutputStream output = new BufferedOutputStream(outputStream);
		

		try (InputStream in = new FileInputStream(file);
				Reader reader = new InputStreamReader(in,Charset.defaultCharset());
				Reader buffer = new BufferedReader(reader)) {
			
			// Write header (number of chars in freq table )
			int numCharacters = prefixCodes.size();
			output.write(numCharacters);
			
			// Write frequencies
			for(Entry<Character, String> entry : prefixCodes.entrySet()) {
				Character c = entry.getKey();
				int length = entry.getValue().length();
				output.write(c.toString().getBytes());
				output.write(length);
			}
			
			// Read file and write output
			int r;
			String binaryString = "";
			// -1 means EOF
			while ((r = reader.read()) != -1) {
				Character c = Character.valueOf((char) r);
				binaryString = binaryString + prefixCodes.get(c);
				binaryString = write(binaryString, output);
			}
			
			// Finished writing file input. Append EOF to end
			binaryString = binaryString + prefixCodes.get(new Character(EOF));
			binaryString = write(binaryString, output);
			
			// Handle overflow
			if(binaryString.length() > 0) {
				output.write(Integer.parseInt(binaryString, 2));
			}
			
//			flush() writes the content of the buffer to the destination and makes the buffer empty for further data to store 
//			but it does not closes the stream permanently.
//			That means you can still write some more data to the stream.
//			But close() closes the stream permanently. If you want to write some data further,
//			then you have to reopen the stream again and append the data with the existing ones.
			output.flush();
			output.close();
		}
	}
	
//	Helper that deals with writing binary strings.
//	param1: binaryString the binary string to write
//	param2: output the file to write to
//	return3: the resulting binaryString that hasn't been written
	private static String write(String binaryString, BufferedOutputStream output) throws NumberFormatException, IOException {
		if(binaryString.length() == 8) {
			output.write(Integer.parseInt(binaryString, 2));
			binaryString = "";
		} else if (binaryString.length() > 8) {
			String toWrite = binaryString.substring(0, 8);
			output.write(Integer.parseInt(toWrite, 2));
			binaryString = binaryString.replace(toWrite, "");
		}
		return binaryString;
	}
}
