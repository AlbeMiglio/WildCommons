package net.cubespace.yamler.converter;

import java.lang.reflect.ParameterizedType;

import net.cubespace.yamler.InternalConverter;

public class Enum implements Converter {
	
	public Enum(final InternalConverter internalConverter) {
        super();
    }

	@Override
	public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
		return ((java.lang.Enum<?>) obj).name();
	}
	
	@Override
	public Object fromConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
		if (obj == null) {
			return null;
		}
		
		java.lang.Enum<?>[] enumConstants = (java.lang.Enum<?>[]) type.getEnumConstants();
		for (java.lang.Enum<?> enumConstant : enumConstants) {
			if (format(enumConstant.name()).equals(format((String) obj))) {
				return enumConstant;
			}
		}
		
		throw new IllegalArgumentException("Invalid enum " + obj + " in " + type.getName());
	}
	
	private String format(String s) {
		return s.replace(" ", "").replace("_", "").toLowerCase();
	}

	@Override
	public boolean supports(Class<?> type) {
		return java.lang.Enum.class.isAssignableFrom(type);
	}

}
