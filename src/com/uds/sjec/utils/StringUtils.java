package com.uds.sjec.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
	
	public static String GetNameByString(String str) throws Exception {
		String returnStr = str.replaceAll("[/\\\\:*?<>|]", "");
		if (returnStr.equals("")) {
			returnStr = GetNewNameByTime();
		}
		return returnStr;
	}
	
	private static String GetNewNameByTime() {
		return "Î´ÃüÃû" + new SimpleDateFormat("_yyyyMMdd_HHmmsss").format(new Date());
	}
}
