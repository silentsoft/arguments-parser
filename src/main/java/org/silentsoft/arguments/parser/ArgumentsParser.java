package org.silentsoft.arguments.parser;

public class ArgumentsParser {
	
	public enum ParsingOptions {
		LEAVE_DASH_PREFIX,
		CASE_SENSITIVE_SINGLE_DASH,
		CASE_INSENSITIVE_SINGLE_DASH,
		CASE_SENSITIVE_DOUBLE_DASH,
		CASE_INSENSITIVE_DOUBLE_DASH,
		
		REMOVE_DASH_PREFIX,
		CASE_SENSITIVE,
		CASE_INSENSITIVE,
	}
	
	private ArgumentsParser() { }
	
	/**
	 * Supported command line arguments formats are:<br>
	 * <ul>
	 * <li><code>-a -b</code></li>
	 * <li><code>-key1=value1 -key2=value2</code></li>
	 * <li><code>-key1 value1 -key2 value2</code></li>
	 * <li><code>-key1=value1 value2 -key2=value3 value4</code></li>
	 * <li><code>-key1 value1 value2 -key2 value3 value4</code></li>
	 * <li><code>--a --b</code></li>
	 * <li><code>--key1=value1 --key2=value2</code></li>
	 * <li><code>--key1 value1 --key2 value2</code></li>
	 * <li><code>--key1=value1 value2 --key2=value3 value4</code></li>
	 * <li><code>--key1 value1 value2 --key2 value3 value4</code></li>
	 * </ul>
	 * <p>
	 * 
	 * @param args
	 * @return
	 * @throws InvalidArgumentsException 
	 */
	public static Arguments parse(String[] args) throws InvalidArgumentsException {
		return parse(args, validator -> true);
	}
	
	/**
	 * Usage:
	 * <pre>
	 * public static void main(String[] args) throws Exception {
	 *     ArgumentsValidator validator = (arguments) -&gt; {
	 *         if (arguments.containsKey("-a") &amp;&amp; arguments.containsKey("-b")) {
	 *             throw new InvalidArgumentsException("'-a' and '-b' cannot be exists together.");
	 *         }
	 *         
	 *         return true;
	 *     };
	 *     
	 *     Arguments arguments = ArgumentsParser.parse(args, validator);
	 * }
	 * </pre>
	 * 
	 * @param args
	 * @param validator
	 * @return
	 * @throws InvalidArgumentsException
	 */
	public static Arguments parse(String[] args, ArgumentsValidator validator) throws InvalidArgumentsException {
		Arguments arguments = new Arguments();
		
		if (args != null) {
			for (String arg : args) {
				if (arg.startsWith("-")) {
					if (arg.matches("(-)+")) {
						throw new InvalidArgumentsException("The argument key is missing.");
					}
					
					int indexOfEqual = arg.indexOf("=");
					if (indexOfEqual == -1) {
						arguments.add(Argument.of(arg));
					} else {
						String[] pair = arg.split("=", 2);
						arguments.add(Argument.of(pair[0], pair[1]));
					}
				} else {
					if (arguments.isEmpty()) {
						throw new InvalidArgumentsException("The first argument must be starts with '-' or '--'.");
					}
					
					arguments.last().getValues().add(arg);
				}
			}
		}
		
		if (validator != null) {
			if (validator.isValid(arguments) == false) {
				throw new InvalidArgumentsException();
			}
		}
		
		return arguments.with(ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE_SINGLE_DASH, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
	}
	
}
