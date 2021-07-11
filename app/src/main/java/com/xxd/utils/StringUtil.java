package com.xxd.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	 /**
	   *  判断一个字符串是否为空.  </p>
	   *  
	   * @param str 被判断的字符
	   * 
	   * @return 如果是空，返回true，否则返回false
	   * 
	   **/
	    public static boolean isEmpty(String str) {  
	        if (str == null || "null".equals(str) || str.length() == 0) {  
	            return true;  
	        } else {  
	            return false;  
	        }  
	    }  
	  

	 /**
     *  判断一个字符串是否为合格的用户名. 只能由字母和数字组成  ，长度在6到15之间 </p>
     *  
     * @param str 被判断的字符
     * 
     * @return 如果是合格的贰货号，返回true，否则返回false
     * 
     */
    public static boolean isUsername(String str) {  
        if (isEmpty(str)) {  
            return false;  
        }  
        String regExp = "^[a-zA-Z0-9]{4,15}$"; 
        Pattern p = Pattern.compile(regExp); 
        Matcher m = p.matcher(str);  
        return m.find();  
    } 
    
    /**
     *  判断一个字符串是否为合格的密码.只能由字母和数字组成  ，长度在6到15之间</p>
     *  
     * @param pswd 被判断的字符
     * 
     * @return 如果是合格的，返回true，否则返回false
     * 
     */
    public static boolean isPassword(String pswd) {  
        if (isEmpty(pswd)) {  
            return false;  
        }  
        String regExp = "^[a-zA-Z0-9]{6,15}$";  
        Pattern p = Pattern.compile(regExp); 
        Matcher m = p.matcher(pswd);  
        return m.find();  
    }  
  

}
