package com.cy.apical.core;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午9:27
 * @Describe 生命周期管理接口
 * @Version 1.0
 */
public interface LifeCycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();

}
