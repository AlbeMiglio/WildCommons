package net.cubespace.yamler;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class InvalidConverterException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidConverterException() {}

    public InvalidConverterException(String msg) {
        super(msg);
    }

    public InvalidConverterException(Throwable cause) {
        super(cause);
    }

    public InvalidConverterException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
