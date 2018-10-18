package com.uds.Jr.utils;

import java.util.regex.Pattern;

public class RegularUtil {
	
	public static boolean JudgeArithmeticalExpression(String dataStr) {
		return Pattern.matches("^((([^=><&|]+((==)|(>=)|(<=)|<|>)[^=><&|]+))(((&&)|(\\|\\|))(([^=><&|]+((==)|(>=)|(<=)|<|>)[^=><&|]+)))*)$", dataStr);
//        System.out.println(matches);
	}
}
