package com.cy.apical.common.constants;

import java.util.regex.Pattern;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午6:25
 * @Describe 基础常量
 * @Version 1.0
 */
public interface BasicConst {
    String DEFAULT_CHARSET = "UTF-8";

    String PATH_SEPARATOR = "/";

    String PATH_PATTERN = "/**";

    String QUESTION_SEPARATOR = "?";

    String ASTERISK_SEPARATOR = "*";

    String AND_SEPARATOR = "&";

    String EQUAL_SEPARATOR = "=";

    String BLANK_SEPARATOR_1 = "";

    String BLANK_SEPARATOR_2 = " ";

    String COMMA_SEPARATOR = ",";

    String SEMICOLON_SEPARATOR = ";";

    String DOLLAR_SEPARATOR = "$";

    String PIPELINE_SEPARATOR = "|";

    String BAR_SEPARATOR = "-";

    String COLON_SEPARATOR = ":";

    String DIT_SEPARATOR = ".";

    String HTTP_PREFIX_SEPARATOR = "http://";

    String HTTPS_PREFIX_SEPARATOR = "https://";

    String HTTP_FORWARD_SEPARATOR = "X-Forwarded-For";

    Pattern PARAM_PATTERN = Pattern.compile("\\{(.*?)\\}");

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String ENABLE = "Y";

    String DISABLE = "N";
}
