package org.silentsoft.arguments.parser;

public interface ArgumentsValidator {
	
	boolean isValid(Arguments arguments) throws InvalidArgumentsException;

}
