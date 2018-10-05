package com.uds.Jr.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	
//	public static void main(String[] args) {
//		List<String> list = GetStrListByCharacterSize("ÎØÎØÎØÃ´Ã´Ã´ºÇºÇºÇ¹ş¹ş¹ş123", 10);
//		for (String str : list) {
//			System.out.println(str);
//		}
//	}
	
	public static List<String> GetStrListByCharacterSize(String data, int size) {
		List<String> list = new ArrayList<String>();
		int length = data.length();
		while ((length = SubStringByCharacterSize(data, size)) != data.length()) {
			list.add(data.substring(0, length));
			data = data.substring(length);
		}
		list.add(data.substring(0, length));
		
		return list;
	}
	
	public static int SubStringByCharacterSize(String str, int size) {
        int count = 0;
        char[] chars = str.toCharArray();
        int charsLength = chars.length;
        for (int i = 0; i < charsLength; i++) {
        	if (chars[i] > 256) {
        		count += 2;
        	} else {
        		count += 1;
        	}
        	if (count > size) {
        		return i;
        	}
        }
        if (count <= size) {
        	return charsLength;
        } else {
        	return 0;
        }
    }
}
