package com.uds.Jr.utils;

import java.io.File;
import java.io.FileWriter;

public class TxtUtil {
	public static String GetNewFile(String fileName, String content) throws Exception {
		String tempPath = "C:\\Temp\\" + fileName;
		
		File file = new File(tempPath);    
		file.createNewFile();
		
		FileWriter fileWriter = new FileWriter(tempPath);
		fileWriter.write(content);
		fileWriter.close();
		
		return tempPath;
	}
}
