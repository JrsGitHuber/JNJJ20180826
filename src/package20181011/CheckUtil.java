/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package package20181011;

import java.util.regex.Pattern;

public class CheckUtil
{

    public CheckUtil()
    {
    }

    public static boolean IsInt(String num)
    {
        return Pattern.matches("^-?\\d+$", num);
    }

    public static Boolean IsDouble(String num)
    {
        return Boolean.valueOf(IsInt(num) || Pattern.matches("^-?\\d+\\.\\d+$", num));
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSpaces\MyEclipse2015\_JrUtils_Jad\lib\UDS.Common.jar
	Total time: 132 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/