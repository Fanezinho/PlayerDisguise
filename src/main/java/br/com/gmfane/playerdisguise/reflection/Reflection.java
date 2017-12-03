package br.com.gmfane.playerdisguise.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class Reflection {

	public static Field getField(Class<?> theClass, String fieldName) {
		try {
			Field theField = theClass.getDeclaredField(fieldName);
			theField.setAccessible(true);

			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(theField, theField.getModifiers() & ~Modifier.FINAL);

			return theField;
		} catch (Exception e) {
			return null;
		}
	}

	public static Method getMethod(Class<?> theClass, String methodName, Class<?>... parameters) {
		try {
			Method theMethod = theClass.getDeclaredMethod(methodName, parameters);
			theMethod.setAccessible(true);
			return theMethod;
		} catch (Exception e) {
			return null;
		}
	}
}
