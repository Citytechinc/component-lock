package com.icfi.aem.componentlock.model;

import java.util.HashMap;
import java.util.Map;

public enum LockPermission {
    ALLOW,
    DENY,
    DEFAULT;
    
    private static final Map<String, LockPermission> BY_NAME = new HashMap<>();
    static {
    	for (LockPermission value: values()) {
    		BY_NAME.put(value.name(), value);
    	}
    }
    
    public static LockPermission fromName(final String name) {
    	return BY_NAME.get(name);
    }
}
