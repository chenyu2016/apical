package com.cy.apical.core;

import com.cy.apical.common.constants.BasicConst;
import com.cy.apical.common.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午8:47
 * @Describe 网关配置信息加载类
 * 优先级：
 * 运行参数(最高) ->  jvm参数  -> 环境变量  -> 配置文件  -> 内部RapidConfig对象的默认属性值(最低);
 * @Version 1.0
 */
@Slf4j
public class ApicalConfigLoader {

    /** 环境变量前缀 */
    private final static String CONFIG_ENV_PREFIX = "APICAL_";
    /** jvm参数前缀*/
    private final static String CONFIG_JVM_PREFIX = "APICAL.";
    /** 运行参数前缀*/
    private final static String CONFIG_ARGS_PREFIX = "--APICAL.";

    private final static String CONFIG_FILE = "apical.properties";

    private final static ApicalConfigLoader INSTANCE = new ApicalConfigLoader();

    private ApicalConfig config;

    private ApicalConfigLoader(){}

    public static ApicalConfigLoader getInstance(){
        return INSTANCE;
    }

    public static ApicalConfig getApicalConfig(){
        return INSTANCE.config;
    }

    public ApicalConfig load(String args[]){
        config = new ApicalConfig();
        // 配置文件
        {
            InputStream is = ApicalConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if(null != is){
                Properties properties = new Properties();
                try {
                    properties.load(is);
                    PropertiesUtils.properties2Object(properties,config);
                } catch (Exception e){
                    e.printStackTrace();
                    log.warn("#ApicalConfigLoader# load CONFIG_FILE is null:{}",e.getMessage());
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // 环境变量
        {
            Map<String,String> env = System.getenv();
            Properties properties = new Properties();
            properties.putAll(env);
            PropertiesUtils.properties2Object(properties,config,CONFIG_ENV_PREFIX);
        }

        // jvm参数
        {
            Properties properties = System.getProperties();
            PropertiesUtils.properties2Object(properties,config,CONFIG_JVM_PREFIX);
        }

        // 运行参数 --xxx==xxx
        {
            if(null != args && args.length>0){
                Properties properties = new Properties();
                for(String e : args){
                    if(e.startsWith("--") && e.contains(BasicConst.EQUAL_SEPARATOR)){
                        properties.put(e.substring(2,e.indexOf(BasicConst.EQUAL_SEPARATOR))
                                , e.substring(e.indexOf(BasicConst.EQUAL_SEPARATOR)+1));
                    }
                }
                PropertiesUtils.properties2Object(properties,config,CONFIG_ARGS_PREFIX);
            }

        }

        return config;
    }
}
