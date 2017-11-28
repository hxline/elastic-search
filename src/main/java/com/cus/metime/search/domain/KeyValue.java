package com.cus.metime.search.domain;

import java.io.Serializable;

/**
 *
 * @author Handoyo
 */
public class KeyValue implements Serializable {

    private static final long serialVersionUID = 1L;
    private Object key;
    private Object value;

    public KeyValue(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public KeyValue() {
    }
    
    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
//        return "KeyValue{" + "key=" + key + ", value=" + value + '}';
        return "\"" + this.key + "\":\"" + this.value + "\"";
    }
}
