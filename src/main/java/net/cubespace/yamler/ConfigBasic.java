package net.cubespace.yamler;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public abstract class ConfigBasic {
	
    protected transient File configFile = null;
    protected transient String[] configHeader = null;
    protected transient boolean skipFailedObjects = false;
    protected transient boolean preserveStatic;

    protected transient InternalConverter converter = new InternalConverter();
    
    public ConfigBasic() {
    	preserveStatic = getClass().isAnnotationPresent(PreserveStatic.class);
    }

    /**
     * This function gets called after the File has been loaded and before the Converter gets it.
     * This is used to manually edit the configSection when you updated the config or something
     * @param configSection The root ConfigSection with all Subnodes loaded into
     */
    public void update(ConfigSection configSection) {

    }

    /**
     * Add a Custom Converter. A Converter can take Objects and return a pretty Object which gets saved/loaded from
     * the Converter. How a Converter must be build can be looked up in the Converter Interface.
     *
     * @param addConverter Converter to be added
     * @throws InvalidConverterException If the Converter has any errors this Exception tells you what
     */
    public void addConverter(Class<?> addConverter) throws InvalidConverterException {
        converter.addConverter(addConverter);
    }

    /*
	 * Dalla documentazione di Field#set(...):
	 * "If the underlying field is static, the obj argument is ignored; it may be null."
	 * 
	 * Quindi non è importante se YamlerConfig passa un oggetto o no, il campo statico viene serializzato e deserializzato comunque.
	 */
    protected boolean doSkip(Field field) {
    	return Modifier.isTransient(field.getModifiers()) || (!preserveStatic && Modifier.isStatic(field.getModifiers())) || Modifier.isFinal(field.getModifiers());
    }
}
