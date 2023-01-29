package org.silentsoft.arguments.parser;

public class ArgumentsParser {
	
	private String[] args;

	private ParsingOptions[] parsingOptions;

	private ArgumentsHelper argumentsHelper;

	private ArgumentsValidator argumentsValidator;

	ArgumentsParser(String[] args) {
		this.args = args;
	}

	ArgumentsParser(String[] args, ParsingOptions... parsingOptions) {
		this.args = args;
		this.parsingOptions = parsingOptions;
	}

	/**
	 * If the <code>args</code> contains one of the following help commands then {@link ArgumentsHelper#help(Arguments)} will be invoked.<br>
	 * <ul>
	 * <li><code>-help</code></li>
	 * <li><code>--help</code></li>
	 * <li><code>-?</code></li>
	 * <li><code>--?</code></li>
	 * </ul>
	 * <p>
	 * Usage:
	 * <pre>
	 * public static void main(String[] args) throws Exception {
	 *     Arguments arguments = parseArguments(args);
	 * }
	 *
	 * private static Arguments parseArguments(String[] args) throws InvalidArgumentsException {
	 *     return Arguments.parser(args).help(arguments -&gt; {
	 *         StringBuilder builder = new StringBuilder();
	 *         builder.append("Usage: java -jar application.jar [arguments]\n");
	 *         builder.append("\n");
	 *         builder.append("Common arguments:\n");
	 *         // ...
	 *         System.out.println(builder.toString());
	 *
	 *         System.exit(0);
	 *     }).parse();
	 * }
	 * </pre>
	 *
	 * @param argumentsHelper
	 * @return
	 * @see org.silentsoft.arguments.parser.Arguments#requiresHelp()
	 */
	public ArgumentsParser help(ArgumentsHelper argumentsHelper) {
		this.argumentsHelper = argumentsHelper;
		return this;
	}

	/**
	 * Usage:
	 * <pre>
	 * public static void main(String[] args) throws Exception {
	 *     Arguments arguments = parseArguments(args);
	 * }
	 *
	 * private static Arguments parseArguments(String[] args) throws InvalidArgumentsException {
	 *     return Arguments.parser(args).validate(arguments -&gt; {
	 *         if (arguments.containsKey("-a") &amp;&amp; arguments.containsKey("-b")) {
	 *             throw new InvalidArgumentsException("'-a' and '-b' cannot be exists together.");
	 *         }
	 *
	 *         return true;
	 *     }).parse();
	 * }
	 * </pre>
	 *
	 * @param argumentsValidator
	 * @return
	 */
	public ArgumentsParser validate(ArgumentsValidator argumentsValidator) {
		this.argumentsValidator = argumentsValidator;
		return this;
	}

	public Arguments parse() throws InvalidArgumentsException {
		Arguments arguments = new Arguments(args, parsingOptions);
		if (arguments.requiresHelp() && argumentsHelper != null) {
			argumentsHelper.help(arguments);
		} else {
			if (argumentsValidator != null) {
				if (!argumentsValidator.isValid(arguments)) {
					throw new InvalidArgumentsException();
				}
			}
		}
		return arguments;
	}

	/**
	 * Use {@link org.silentsoft.arguments.parser.Arguments#parser(String[])} and {@link org.silentsoft.arguments.parser.ArgumentsParser#parse()} instead.<p>
	 * This method is equivalent to <code>Arguments.parser(args).parse()</code>.
	 *
	 * @param args
	 * @return
	 * @throws InvalidArgumentsException
	 * @see org.silentsoft.arguments.parser.Arguments#parser(String[])
	 * @see org.silentsoft.arguments.parser.Arguments#parser(String[], ParsingOptions...)
	 */
	@Deprecated
	public static Arguments parse(String[] args) throws InvalidArgumentsException {
		return Arguments.parser(args).parse();
	}


	/**
	 * Use {@link org.silentsoft.arguments.parser.Arguments#parser(String[])} and {@link org.silentsoft.arguments.parser.ArgumentsParser#parse()} instead.<p>
	 * This method is equivalent to <code>Arguments.parser(args).validate(validator).parse()</code>.
	 *
	 * @param args
	 * @param validator
	 * @return
	 * @throws InvalidArgumentsException
	 * @see org.silentsoft.arguments.parser.Arguments#parser(String[])
	 * @see org.silentsoft.arguments.parser.Arguments#parser(String[], ParsingOptions...)
	 */
	@Deprecated
	public static Arguments parse(String[] args, ArgumentsValidator validator) throws InvalidArgumentsException {
		return Arguments.parser(args).validate(validator).parse();
	}
	
}
