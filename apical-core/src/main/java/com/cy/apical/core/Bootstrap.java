package com.cy.apical.core;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午7:42
 * @Describe 启动入口
 * @Version 1.0
 */
public class Bootstrap {

    public static void main(String[] args) {
        //1 加载网管配置信息
        ApicalConfig config = ApicalConfigLoader.getInstance().load(args);

        //2 插件初始化
        //3 初始化服务注册管理器 监听动态配置的新增 修改 删除
        //4 启动容器

        ApicalContainer apicalContainer = new ApicalContainer(config);
        apicalContainer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
             //   apicalContainer.shutdown();
            }
        }));
        //5
        //6
    }
}
