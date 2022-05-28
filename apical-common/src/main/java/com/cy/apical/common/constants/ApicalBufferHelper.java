package com.cy.apical.common.constants;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午8:38
 * @Describe 网关缓冲区辅助类
 * @Version 1.0
 */
public interface ApicalBufferHelper {
    String DISRUPTOR = "DISRUPTOR";

    String MPMC = "MPMC";

    static boolean isMpmc(String bufferType) {
        return MPMC.equalsIgnoreCase(bufferType);
    }

    static boolean isDisruptor(String bufferType) {
        return DISRUPTOR.equalsIgnoreCase(bufferType);
    }
}
