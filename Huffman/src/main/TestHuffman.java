package main;

import java.io.File;

public class TestHuffman {
	public static void main(String[] args) throws Exception{
		
		///////////////////////////////Main Functions////////////////////////
		
		///Compress File
//		Encode.encodeFile("test.txt");
//		Encode.encodeFile("input.txt");
		
		///Decompress File
//		Decode.decodeFile("compressed_test.txt");
//		Decode.decodeFile("compressed_input.txt");

		
		
		///////////////////////////////BONUS 2////////////////////////
		
		///compress folder
//		Encode.encodeFolder("f1");
		
		///decompress folder
//		Decode.decodeFolder("compressed_f1");
		
		
		
		///////////////////////////////BONUS 2//////////////////////// 
		////-> also can be a little modified to compress binary folders
		
		///compress binary file
//		File input = new File( new File("").getAbsolutePath()+"\\binary.bin");
//		//convert binary to txt file
//		File output = new File( new File("").getAbsolutePath()+"\\binary.txt");
//		Util.BinaryToTxtConvert(input, output);
//		//compress txt file -> will produce compressed_binary.txt
//		Encode.encodeFile(output.getName());
		
        ///decompress binary file
		File compressedBinary = new File( new File("").getAbsolutePath()+"\\compressed_binary.txt");
		//decompress txt file -> will produce recovered_binary.txt
		Decode.decodeFile(compressedBinary.getName());
		//convert decompressed binary txt to decompressed binary file
		File inBinary = new File( new File("").getAbsolutePath()+"\\recovered_binary.txt");
		File outBinary = new File( new File("").getAbsolutePath()+"\\recovered_binary.bin");
		Util.TextToBinaryConvert(inBinary, outBinary);
			
		
	    } 
	
}
