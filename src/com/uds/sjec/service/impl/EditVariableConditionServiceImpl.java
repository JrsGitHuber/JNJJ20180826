package com.uds.sjec.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import com.teamcenter.rac.util.MessageBox;
import com.uds.common.exceptions.CalculateException;
import com.uds.common.utils.CheckUtil;
import com.uds.sjec.bean.VariabelConditionTableBean;
import com.uds.sjec.service.IEditVariableConditionService;

public class EditVariableConditionServiceImpl implements IEditVariableConditionService {
	public static double THRESHOLD = 1.E-005D;
	private Map<String, String> paramCodeAndTypeMap;// 存储参数代号和参数类型

	@Override
	public void searchParam(DefaultTableModel paramTableModel, List<VariabelConditionTableBean> paramInfoList, String paramCode,
			String paramName) {
		// TODO Auto-generated method stub
		List<VariabelConditionTableBean> results = new ArrayList<VariabelConditionTableBean>();
		if (!paramCode.equals("") && paramName.equals("")) {// 代号有值，名称为空
			Pattern pattern = Pattern.compile(paramCode, Pattern.CASE_INSENSITIVE);
			for (int i = 0; i < paramInfoList.size(); i++) {
				Matcher matcher = pattern.matcher(paramInfoList.get(i).getParamCode());
				if (matcher.find()) {
					results.add(paramInfoList.get(i));
				}
			}
		} else if (paramCode.equals("") && !paramName.equals("")) {// 代号为空，名称有值
			Pattern pattern = Pattern.compile(paramName, Pattern.CASE_INSENSITIVE);
			for (int i = 0; i < paramInfoList.size(); i++) {
				Matcher matcher = pattern.matcher(paramInfoList.get(i).getParamName());
				if (matcher.find()) {
					results.add(paramInfoList.get(i));
				}
			}
		} else if (!paramCode.equals("") && !paramName.equals("")) {// 代号名称都有值
			Pattern paramCodePattern = Pattern.compile(paramCode, Pattern.CASE_INSENSITIVE);
			Pattern paramNamePattern = Pattern.compile(paramName, Pattern.CASE_INSENSITIVE);
			for (int i = 0; i < paramInfoList.size(); i++) {
				Matcher paramCodeMatcher = paramCodePattern.matcher(paramInfoList.get(i).getParamCode());
				Matcher paramNameMatcher = paramNamePattern.matcher(paramInfoList.get(i).getParamName());
				if (paramCodeMatcher.find() && paramNameMatcher.find()) {
					results.add(paramInfoList.get(i));
				}
			}
		}
		if (results.size() > 0) {
			// 显示结果
			paramTableModel.setRowCount(0);
			for (int i = 0; i < results.size(); i++) {
				paramTableModel.addRow(new String[] { results.get(i).paramCode, results.get(i).paramName, results.get(i).paramType,
						results.get(i).rangeOfParamValue });
			}
		}
	}

	@Override
	public Map<String, String> getParamCodeAndTypeMap(List<VariabelConditionTableBean> paramInfoList) {
		// TODO Auto-generated method stub
		paramCodeAndTypeMap = new HashMap<String, String>();
		for (VariabelConditionTableBean variabelConditionTableBean : paramInfoList) {
			paramCodeAndTypeMap.put(variabelConditionTableBean.getParamCode(), variabelConditionTableBean.getParamType());
		}
		return paramCodeAndTypeMap;
	}

	private Boolean PassCondition(String condition, Boolean notEmpty, List<KeyValueBean> beanList) throws CalculateException {
		condition = condition.replace(" ", "");
		if ((condition.toLowerCase().equals("true")) || (condition.toLowerCase().equals("false"))) {
			return Boolean.valueOf(Boolean.parseBoolean(condition));
		}
		Pattern pattern = Pattern.compile("\\((?<path>[^()]+)\\)");
		Matcher matcher = pattern.matcher(condition);
		if ((matcher.find()) && (!matcher.group("path").isEmpty())) {
			String matchValue = matcher.group("path");
			String newPath = condition.replace("(" + matchValue + ")", PassCondition(matchValue, notEmpty, beanList) + "");
			if (newPath.equals(condition)) {
				throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
			}
			return PassCondition(newPath, notEmpty, beanList);
		}
		pattern = Pattern.compile("(?<path>[^()|&><=]+[><=]{1,2}[^()|&><=]+)");
		matcher = pattern.matcher(condition);
		if ((matcher.find()) && (!matcher.group("path").isEmpty())) {
			String matchValue = matcher.group("path");
			if (matchValue.contains(">=")) {
				beanList.add(new KeyValueBean(matchValue.split(">=")[0], matchValue.split(">=")[1]));
			} else if (matchValue.contains("<=")) {
				beanList.add(new KeyValueBean(matchValue.split("<=")[0], matchValue.split("<=")[1]));
			} else if (matchValue.contains(">")) {
				beanList.add(new KeyValueBean(matchValue.split(">")[0], matchValue.split(">")[1]));
			} else if (matchValue.contains("<")) {
				beanList.add(new KeyValueBean(matchValue.split("<")[0], matchValue.split("<")[1]));
			} else if (matchValue.contains("==")) {
				beanList.add(new KeyValueBean(matchValue.split("==")[0], matchValue.split("==")[1]));
			}
			if (matchValue.contains("<=")) {
				String[] nums = matchValue.split("<=");
				if ((nums.length != 2) || (nums[0].isEmpty()) || (nums[1].isEmpty())) {
					throw new CalculateException(condition, "empty string in the expression");
				}
				if ((CheckUtil.IsDouble(nums[0]).booleanValue()) && (CheckUtil.IsDouble(nums[1]).booleanValue())) {
					String path = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1"
							+ (Double.parseDouble(nums[0]) <= Double.parseDouble(nums[1])) + "$2");
					return PassCondition(path, notEmpty, beanList);
				}
				String newPath = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1"
						+ (nums[0].compareTo(nums[1]) <= 0) + "$2");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
			if (matchValue.contains(">=")) {
				String[] nums = matchValue.split(">=");
				if ((nums.length != 2) || (nums[0].isEmpty()) || (nums[1].isEmpty())) {
					throw new CalculateException(condition, "empty string in the expression");
				}
				if ((CheckUtil.IsDouble(nums[0]).booleanValue()) && (CheckUtil.IsDouble(nums[1]).booleanValue())) {
					String path = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1"
							+ (Double.parseDouble(nums[0]) >= Double.parseDouble(nums[1])) + "$2");
					return PassCondition(path, notEmpty, beanList);
				}
				String newPath = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1"
						+ (nums[0].compareTo(nums[1]) >= 0) + "$2");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
			if (matchValue.contains("==")) {
				String[] nums = matchValue.split("==");
				if ((nums.length != 2) || (nums[0].isEmpty()) || (nums[1].isEmpty())) {
					throw new CalculateException(condition, "empty string in the expression");
				}
				if ((CheckUtil.IsDouble(nums[0]).booleanValue()) && (CheckUtil.IsDouble(nums[1]).booleanValue())) {
					String path = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)",
							"$1" + AreEqual(Double.parseDouble(nums[0]), Double.parseDouble(nums[1])) + "$2");
					return PassCondition(path, notEmpty, beanList);
				}
				String newPath = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1" + nums[0].equals(nums[1]) + "$2");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
			if (matchValue.contains(">")) {
				String[] nums = matchValue.split(">");
				if ((nums.length != 2) || (nums[0].isEmpty()) || (nums[1].isEmpty())) {
					throw new CalculateException(condition, "empty string in the expression");
				}
				if ((CheckUtil.IsDouble(nums[0]).booleanValue()) && (CheckUtil.IsDouble(nums[1]).booleanValue())) {
					String path = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1"
							+ (Double.parseDouble(nums[0]) > Double.parseDouble(nums[1])) + "$2");
					return PassCondition(path, notEmpty, beanList);
				}
				String newPath = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1" + (nums[0].compareTo(nums[1]) > 0)
						+ "$2");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
			if (matchValue.contains("<")) {
				String[] nums = matchValue.split("<");
				if ((nums.length != 2) || (nums[0].isEmpty()) || (nums[1].isEmpty())) {
					throw new CalculateException(condition, "empty string in the expression");
				}
				if ((CheckUtil.IsDouble(nums[0]).booleanValue()) && (CheckUtil.IsDouble(nums[1]).booleanValue())) {
					String path = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1"
							+ (Double.parseDouble(nums[0]) < Double.parseDouble(nums[1])) + "$2");
					return PassCondition(path, notEmpty, beanList);
				}
				String newPath = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1" + (nums[0].compareTo(nums[1]) < 0)
						+ "$2");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
		}
		if (!notEmpty.booleanValue()) {
			pattern = Pattern.compile("(?<path>([><=]{1,2}[^()|&><=]+)|([^()|&><=]+[><=]{1,2}))");
			matcher = pattern.matcher(condition);
			if ((matcher.find()) && (!matcher.group("path").isEmpty())) {
				String matchValue = matcher.group("path");
				String newPath = condition.replaceAll("(.*?)" + Pattern.quote(matchValue) + "(.*)", "$1false$2");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
		}
		pattern = Pattern.compile("!(?<path>true|false|True|False)");
		matcher = pattern.matcher(condition);
		if ((matcher.find()) && (!matcher.group("path").isEmpty())) {
			String matchValue = matcher.group("path");
			String newPath = condition.replace("!" + matchValue, (!PassCondition(matchValue, notEmpty, beanList).booleanValue()) + "");
			if (newPath.equals(condition)) {
				throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
			}
			return PassCondition(newPath, notEmpty, beanList);
		}
		pattern = Pattern.compile("(?<path>(true|false|True|False)(&&|\\|\\|)(true|false|True|False))");
		matcher = pattern.matcher(condition);
		if ((matcher.find()) && (!matcher.group("path").isEmpty())) {
			String matchValue = matcher.group("path");
			if (matchValue.contains("&&")) {
				String[] nums = matchValue.split("&&");
				String newPath = condition.replace(matchValue, ((Boolean.parseBoolean(nums[0])) && (Boolean.parseBoolean(nums[1]))) + "");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
			if (matchValue.contains("||")) {
				String[] nums = matchValue.split("\\|\\|");
				String newPath = condition.replace(matchValue, ((Boolean.parseBoolean(nums[0])) || (Boolean.parseBoolean(nums[1]))) + "");
				if (newPath.equals(condition)) {
					throw new CalculateException(condition, "failed to parse the calculated path. Die recursive.");
				}
				return PassCondition(newPath, notEmpty, beanList);
			}
		}
		throw new CalculateException(condition, "failed to parse the calculated path.");
	}

	public static Boolean AreEqual(double a, double b) {
		return Boolean.valueOf(Math.abs(a - b) < THRESHOLD);
	}

	@Override
	public Boolean checkType(String variableCondition, Map<String, String> paramCodeAndTypeMap) throws CalculateException {
		List<KeyValueBean> beanList = new ArrayList<KeyValueBean>();
		PassCondition(variableCondition, Boolean.valueOf(false), beanList);
		for (KeyValueBean bean : beanList) {
			if (paramCodeAndTypeMap.containsKey(bean.key)) {
				if (paramCodeAndTypeMap.get(bean.key).equals("实数")) {
					try {
						Double.parseDouble(bean.value);
					} catch (Exception e) {
						MessageBox.post(bean.key + "的参数类型为实数。", "编辑变量条件", MessageBox.ERROR);
						return false;
					}
				} else if (paramCodeAndTypeMap.get(bean.key).equals("整数")) {
					try {
						Integer.parseInt(bean.value);
					} catch (Exception e) {
						MessageBox.post(bean.key + "的参数类型为整数。", "编辑变量条件", MessageBox.ERROR);
						return false;
					}
				}
			}
		}
		
		return true;
	}
}

class KeyValueBean {
	String key = "";
	String value = "";
	
	public KeyValueBean(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
