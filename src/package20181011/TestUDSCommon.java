package package20181011;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uds.common.exceptions.CalculateException;
import com.uds.common.utils.MathUtil;

public class TestUDSCommon {
	
	public static double THRESHOLD = 1.0000000000000001E-005D;

	public static void main(String[] args) {
		String variableCondition = "para>m1_2>=0&&param1_2<=5";
		
		try {
			System.out.println(!PassConditionNotEmpty(variableCondition));
			
			if (!MathUtil.PassConditionNotEmpty(variableCondition) && !MathUtil.PassCondition(variableCondition)) {
				System.out.println("Come In !");
			} else {
				System.out.println("Not Come In...");
			}
		} catch (CalculateException e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean PassConditionNotEmpty(String condition)
	        throws CalculateException
	    {
	        return PassCondition(condition, Boolean.valueOf(true));
	    }
	
	public static Boolean PassCondition(String condition, Boolean notEmpty)
	        throws CalculateException
	    {
	        condition = condition.replace(" ", "");
	        if(condition.toLowerCase().equals("true") || condition.toLowerCase().equals("false"))
	            return Boolean.valueOf(Boolean.parseBoolean(condition));
	        Pattern pattern = Pattern.compile("\\((?<path>[^()]+)\\)");
	        Matcher matcher = pattern.matcher(condition);
	        if(matcher.find() && !matcher.group("path").isEmpty())
	        {
	            String matchValue = matcher.group("path");
	            String newPath = condition.replace((new StringBuilder()).append("(").append(matchValue).append(")").toString(), (new StringBuilder()).append(PassCondition(matchValue, notEmpty)).append("").toString());
	            if(newPath.equals(condition))
	                throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	            else
	                return PassCondition(newPath, notEmpty);
	        }
	        pattern = Pattern.compile("(?<path>[^()|&><=]+[><=]{1,2}[^()|&><=]+)");
	        matcher = pattern.matcher(condition);
	        if(matcher.find() && !matcher.group("path").isEmpty())
	        {
	            String matchValue = matcher.group("path");
	            if(matchValue.contains("<="))
	            {
	                String nums[] = matchValue.split("<=");
	                if(nums.length != 2 || nums[0].isEmpty() || nums[1].isEmpty())
	                    throw new CalculateException(condition, "empty string in the expression");
	                if(CheckUtil.IsDouble(nums[0]).booleanValue() && CheckUtil.IsDouble(nums[1]).booleanValue())
	                {
	                    String path = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(Double.parseDouble(nums[0]) <= Double.parseDouble(nums[1])).append("$2").toString());
	                    return PassCondition(path, notEmpty);
	                }
	                String newPath = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(nums[0].compareTo(nums[1]) <= 0).append("$2").toString());
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	            if(matchValue.contains(">="))
	            {
	                String nums[] = matchValue.split(">=");
	                if(nums.length != 2 || nums[0].isEmpty() || nums[1].isEmpty())
	                    throw new CalculateException(condition, "empty string in the expression");
	                if(CheckUtil.IsDouble(nums[0]).booleanValue() && CheckUtil.IsDouble(nums[1]).booleanValue())
	                {
	                    String path = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(Double.parseDouble(nums[0]) >= Double.parseDouble(nums[1])).append("$2").toString());
	                    return PassCondition(path, notEmpty);
	                }
	                String newPath = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(nums[0].compareTo(nums[1]) >= 0).append("$2").toString());
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	            if(matchValue.contains("=="))
	            {
	                String nums[] = matchValue.split("==");
	                if(nums.length != 2 || nums[0].isEmpty() || nums[1].isEmpty())
	                    throw new CalculateException(condition, "empty string in the expression");
	                if(CheckUtil.IsDouble(nums[0]).booleanValue() && CheckUtil.IsDouble(nums[1]).booleanValue())
	                {
	                    String path = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(AreEqual(Double.parseDouble(nums[0]), Double.parseDouble(nums[1]))).append("$2").toString());
	                    return PassCondition(path, notEmpty);
	                }
	                String newPath = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(nums[0].equals(nums[1])).append("$2").toString());
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	            if(matchValue.contains(">"))
	            {
	                String nums[] = matchValue.split(">");
	                if(nums.length != 2 || nums[0].isEmpty() || nums[1].isEmpty())
	                    throw new CalculateException(condition, "empty string in the expression");
	                if(CheckUtil.IsDouble(nums[0]).booleanValue() && CheckUtil.IsDouble(nums[1]).booleanValue())
	                {
	                    String path = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(Double.parseDouble(nums[0]) > Double.parseDouble(nums[1])).append("$2").toString());
	                    return PassCondition(path, notEmpty);
	                }
	                String tempStr = (new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString();
	                String tempStr1 = (new StringBuilder()).append("$1").append(nums[0].compareTo(nums[1]) > 0).append("$2").toString();
	                String newPath = condition.replaceAll(tempStr, tempStr1);
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	            if(matchValue.contains("<"))
	            {
	                String nums[] = matchValue.split("<");
	                if(nums.length != 2 || nums[0].isEmpty() || nums[1].isEmpty())
	                    throw new CalculateException(condition, "empty string in the expression");
	                if(CheckUtil.IsDouble(nums[0]).booleanValue() && CheckUtil.IsDouble(nums[1]).booleanValue())
	                {
	                    String path = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), (new StringBuilder()).append("$1").append(Double.parseDouble(nums[0]) < Double.parseDouble(nums[1])).append("$2").toString());
	                    return PassCondition(path, notEmpty);
	                }
	                String tempStr = (new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString();
	                String tempStr1 = (new StringBuilder()).append("$1").append(nums[0].compareTo(nums[1]) < 0).append("$2").toString();
	                String newPath = condition.replaceAll(tempStr, tempStr1);
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	        }
	        if(!notEmpty.booleanValue())
	        {
	            pattern = Pattern.compile("(?<path>([><=]{1,2}[^()|&><=]+)|([^()|&><=]+[><=]{1,2}))");
	            matcher = pattern.matcher(condition);
	            if(matcher.find() && !matcher.group("path").isEmpty())
	            {
	                String matchValue = matcher.group("path");
	                String newPath = condition.replaceAll((new StringBuilder()).append("(.*?)").append(Pattern.quote(matchValue)).append("(.*)").toString(), "$1false$2");
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	        }
	        pattern = Pattern.compile("!(?<path>true|false|True|False)");
	        matcher = pattern.matcher(condition);
	        if(matcher.find() && !matcher.group("path").isEmpty())
	        {
	            String matchValue = matcher.group("path");
	            String newPath = condition.replace((new StringBuilder()).append("!").append(matchValue).toString(), (new StringBuilder()).append(!PassCondition(matchValue, notEmpty).booleanValue()).append("").toString());
	            if(newPath.equals(condition))
	                throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	            else
	                return PassCondition(newPath, notEmpty);
	        }
	        pattern = Pattern.compile("(?<path>(true|false|True|False)(&&|\\|\\|)(true|false|True|False))");
	        matcher = pattern.matcher(condition);
	        if(matcher.find() && !matcher.group("path").isEmpty())
	        {
	            String matchValue = matcher.group("path");
	            if(matchValue.contains("&&"))
	            {
	                String nums[] = matchValue.split("&&");
	                String newPath = condition.replace(matchValue, (new StringBuilder()).append(Boolean.parseBoolean(nums[0]) && Boolean.parseBoolean(nums[1])).append("").toString());
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	            if(matchValue.contains("||"))
	            {
	                String nums[] = matchValue.split("\\|\\|");
	                String newPath = condition.replace(matchValue, (new StringBuilder()).append(Boolean.parseBoolean(nums[0]) || Boolean.parseBoolean(nums[1])).append("").toString());
	                if(newPath.equals(condition))
	                    throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
	                else
	                    return PassCondition(newPath, notEmpty);
	            }
	        }
	        throw new CalculateException(condition, "failed to parse the calculated path.");
	    }
	
	public static Boolean AreEqual(double a, double b)
    {
        return Boolean.valueOf(Math.abs(a - b) < THRESHOLD);
    }

}
