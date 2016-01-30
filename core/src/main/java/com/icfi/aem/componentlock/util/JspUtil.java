package com.icfi.aem.componentlock.util;

import java.util.Objects;

public final class JspUtil {

	public static String isChecked(final Object a, final Object b) {
		return Objects.equals(a, b) ? "checked=\"checked\"" : "";
	}

	private JspUtil() { }
}
