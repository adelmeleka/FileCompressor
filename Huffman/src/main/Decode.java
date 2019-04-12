package main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Decode {
	
	public static void decodeFolder(String folderName) throws Exception{
		//get current folder path
		String dirPath = new File("").getAbsolutePath()+"\\"+folderName;
		File dir = new File(dirPath);
		
		//create new compressed directory
		folderName = folderName.replace("compressed_", "");
		File theDir = new File(new File("").getAbsolutePath()+"\\recovered_"+folderName);
		theDir.mkdir();
		
		//iterate into each file and compress it 
		File[] directoryListing = dir.listFiles();	
		if (directoryListing != null) {
		  for (File child : directoryListing)
		  {
		      // Do something with each file child
//			  String s = child.getName().replace("compressed_", "");
//			  System.out.println("ssss: "+s);
			  decodeFile("compressed_"+folderName+"\\"+child.getName());
			  
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
		  }
	}
	
	public static void decodeFile(String fileName) throws  FileNotFoundException, IOException {

		long start = System.nanoTime();

		String source = new File("").getAbsolutePath()+"\\"+fileName;
		File input = new File(source);
		
		fileName = fileName.replaceAll("compressed_", "recovered_");
		String target = new File("").getAbsolutePath()+"\\\\"+fileName;
		File output = new File(target);
		

		generateOutputFile(input, output);

		long diff = System.nanoTime() - start;
		System.out.println("Finished in " + TimeUnit.NANOSECONDS.toMillis(diff)
				+ " ms.");
	}
	
	
	 // Decodes input file and generates an output file.
	 //param1: input the input file
	 //param2: output the output file 
	public static void generateOutputFile(File input, File output)throws FileNotFoundException,IOException {

		FileInputStream stream = new FileInputStream(input);
		BufferedInputStream bufferedStream = new BufferedInputStream(stream);
		DataInputStream in = new DataInputStream(bufferedStream);

		FileWriter fw = new FileWriter(output.getAbsoluteFile());
		BufferedWriter out = new BufferedWriter(fw);
		
		// Get header
		int numCharacters = in.readByte();

		// Get Lengths
		Map<Integer, List<Character>> prefixCodes = new HashMap<Integer, List<Character>>();
		for (int i = 0; i < numCharacters; i++) {
			Character c = Character.valueOf((char) in.readByte());
			Integer length = new Integer(in.readByte());

			List<Character> chars = prefixCodes.get(length);

			if (chars != null) {
				chars.add(c);
			} else {
				chars = new ArrayList<Character>();
				chars.add(c);
				prefixCodes.put(length, chars);
			}
		}
		
		for (List<Character> chars : prefixCodes.values()) {
			Collections.sort(chars);
		}

		// Canonize
		Map<Character, String> canonized = Util.canonize(prefixCodes);
		Map<String, Character> codeMapping = Util.swap(canonized);
		
		// Generate output
		String binaryString = "";
		while (in.available() != 0) {
			int b = in.readUnsignedByte();
			String newString = addLeadingZeros(b, in);
			binaryString = binaryString + newString;
			for (int i = 1; i < binaryString.length(); i++) {
				String subString = binaryString.substring(0, i);
				Character c = codeMapping.get(subString);

				// Character found, write to file
				if (c != null) {
					out.write(c);
					binaryString = binaryString.substring(i,
							binaryString.length());
					i = 1;
				}
			}
		}
		stream.close();
		bufferedStream.close();
		in.close();
		out.close();
	}

	
//	  Adds leading zeros when they get dropped while being read. 
//	  param1: r the byte being read
//	  param2: in the data input stream being used to read the byte
//	  return the 8 character longer binary string of the byte
	private static String addLeadingZeros(int r, DataInputStream in)
			throws IOException {

		String newString = Integer.toBinaryString(r);
		// If this is the end of the file, dont add zeros!
		if (in.available() != 0) {
			int len = newString.length();

			if (len < 8) {
				newString = String.format("%0" + (8 - len) + "d", 0)
						+ newString;
			}
			return newString;
		}
		return newString;
	}
}