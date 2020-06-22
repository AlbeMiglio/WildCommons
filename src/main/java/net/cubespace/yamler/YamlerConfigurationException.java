package net.cubespace.yamler;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class YamlerConfigurationException extends Exception {

	private static final long serialVersionUID = 1L;

	public YamlerConfigurationException() {}

    public YamlerConfigurationException(String msg) {
        super(msg);
    }

    public YamlerConfigurationException(Throwable cause) {
        super(cause);
    }

    public YamlerConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
