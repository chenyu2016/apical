package com.cy.apical.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author ChenYu
 * @Date 2022/6/26 下午6:23
 * @Describe 属性上下文抽象类
 * @Version 1.0
 */
public abstract class AttributeKey<T> {

    private static final Map<String, AttributeKey<?>> namedMap = new ConcurrentHashMap<>();

    public abstract T cast(Object value);

    public static AttributeKey<?> valueOf(String name) {
        return namedMap.get(name);
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> AttributeKey<T> create(final Class<? super T> valueClass) {
        return new SimpleAttributeKey(valueClass);
    }

    public static class SimpleAttributeKey<T> extends AttributeKey<T> {
        private final Class<T> valueClass;

        SimpleAttributeKey(final Class<T> valueClass) {
            this.valueClass = valueClass;
        }

        @Override
        public T cast(Object value) {
            return valueClass.cast(value);
        }

        @Override
        public String toString() {
            if(null != valueClass) {
                StringBuilder sb = new StringBuilder(getClass().getName());
                sb.append("<");
                sb.append(valueClass.getName());
                sb.append(">");
                return sb.toString();
            }
            return super.toString();
        }
    }
}
