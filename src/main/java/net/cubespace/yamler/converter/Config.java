package net.cubespace.yamler.converter;

import net.cubespace.yamler.ConfigSection;
import net.cubespace.yamler.InternalConverter;
import net.cubespace.yamler.YamlerConfig;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Config implements Converter {

    public Config(InternalConverter internalConverter) {
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {

        return (obj instanceof Map) ? obj : ((YamlerConfig) obj).saveToMap();
    }

    @Override
    public Object fromConfig(Class<?> type, Object section, ParameterizedType genericType) throws Exception {
    	if (section == null) {
    		return null;
    	}
        YamlerConfig obj = (YamlerConfig) newInstance(type);
        obj.loadFromMap((section instanceof Map) ? (Map<?, ?>) section : ((ConfigSection) section).getRawMap());
        return obj;
    }
    
    // recursively handles enclosed classes
    public Object newInstance(Class<?> type) throws Exception {
//        Class<?> enclosingClass = type.getEnclosingClass();
//        if (enclosingClass != null) {
//            Object instanceOfEnclosingClass = newInstance(enclosingClass);
//            return type.getConstructor(enclosingClass).newInstance(instanceOfEnclosingClass);
//        } else {
            return type.newInstance();
//        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return YamlerConfig.class.isAssignableFrom(type);
    }
}
