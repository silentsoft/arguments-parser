package org.silentsoft.arguments.parser;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Arguments implements Iterable<Argument> {

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
	 *
	 * @param args
	 * @return
	 * @see #parser(String[], ParsingOptions...)
	 */
	public static ArgumentsParser parser(String[] args) {
		return new ArgumentsParser(args);
	}

	public static ArgumentsParser parser(String[] args, ParsingOptions... parsingOptions) {
		return new ArgumentsParser(args, parsingOptions);
	}

	private TreeSet<Argument> set;

	private ArrayList<ParsingOptions> options;

	Arguments(String[] args, ParsingOptions... parsingOptions) throws InvalidArgumentsException {
		set = new TreeSet<Argument>();
		options = new ArrayList<ParsingOptions>();

		if (args != null) {
			for (String arg : args) {
				if (arg.startsWith("-")) {
					if (arg.matches("(-)+")) {
						throw new InvalidArgumentsException("The argument key is missing.");
					}

					int indexOfEqual = arg.indexOf("=");
					if (indexOfEqual == -1) {
						add(Argument.of(arg));
					} else {
						String[] pair = arg.split("=", 2);
						add(Argument.of(pair[0], pair[1]));
					}
				} else {
					if (isEmpty()) {
						throw new InvalidArgumentsException("The first argument must be starts with '-' or '--'.");
					}

					last().getValues().add(arg);
				}
			}
		}

		if (parsingOptions != null) {
			options = new ArrayList<ParsingOptions>(Arrays.asList(parsingOptions));
		}

		assertOptions(options);

		TreeSet<Argument> _set = new TreeSet<Argument>();
		Function<String, String> transform = (key) -> getOptions().contains(ParsingOptions.REMOVE_DASH_PREFIX) ? key.replaceFirst("(-)+", "") : key;
		set.forEach(argument -> {
			String key;
			if (argument.getKey().startsWith("--") && getOptions().contains(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH)) {
				key = argument.getKey().toLowerCase();
			} else if (!argument.getKey().startsWith("--") && getOptions().contains(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH)) {
				key = argument.getKey().toLowerCase();
			} else if (getOptions().contains(ParsingOptions.CASE_INSENSITIVE)) {
				key = argument.getKey().toLowerCase();
			} else {
				key = argument.getKey();
			}

			if (!_set.stream().anyMatch(arg -> transform.apply(arg.getKey()).equals(transform.apply(key)))) {
				_set.add(Argument.of(transform.apply(key), argument.getValues().toArray(new String[] {})));
			}
		});

		options = getOptions();
		set = _set;
	}
	
	private boolean add(Argument argument) {
		return set.add(argument);
	}

	private Argument last() {
		return set.last();
	}
	
	public int size() {
		return set.size();
	}
	
	public boolean isEmpty() {
		return set.isEmpty();
	}
	
	public Argument get(String key) {
		Optional<Argument> optional = stream(key).findAny();
		return optional.isPresent() ? optional.get() : null;
	}

	public String getValue(String key) {
		return getValue(key, null);
	}

	public String getValue(String key, String defaultValue) {
		Argument argument = get(key);
		return argument != null ? argument.getValue() : defaultValue;
	}

	public List<String> getValues(String key) {
		return getValues(key, null);
	}

	public List<String> getValues(String key, List<String> defaultValues) {
		Argument argument = get(key);
		return argument != null ? argument.getValues() : defaultValues;
	}
	
	public boolean containsKey(String key) {
		return stream(key).findAny().isPresent();
	}

	public boolean requiresHelp() {
		if (containsKey("-help") || containsKey("--help") || containsKey("-?") || containsKey("--?")) {
			return true;
		}

		return false;
	}

	private void assertOptions(ArrayList<ParsingOptions> options) throws InvalidArgumentsException {
		BiFunction<ParsingOptions, ParsingOptions, String> message = (x, y) -> {
			return String.format("%s and %s parsing options cannot be used together.", x.name(), y.name());
		};
		
		if (options.contains(ParsingOptions.LEAVE_DASH_PREFIX) && options.contains(ParsingOptions.REMOVE_DASH_PREFIX)) {
			throw new InvalidArgumentsException(message.apply(ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.REMOVE_DASH_PREFIX));
		}
		
		if (options.contains(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH) && options.contains(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH)) {
			throw new InvalidArgumentsException(message.apply(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH));
		}
		
		if (options.contains(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH) && options.contains(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH)) {
			throw new InvalidArgumentsException(message.apply(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH));
		}
		
		if (options.contains(ParsingOptions.CASE_SENSITIVE) && options.contains(ParsingOptions.CASE_INSENSITIVE)) {
			throw new InvalidArgumentsException(message.apply(ParsingOptions.CASE_SENSITIVE, ParsingOptions.CASE_INSENSITIVE));
		}
		
		if (options.contains(ParsingOptions.LEAVE_DASH_PREFIX)) {
			if (options.contains(ParsingOptions.CASE_SENSITIVE)) {
				throw new InvalidArgumentsException(message.apply(ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE));
			}
			
			if (options.contains(ParsingOptions.CASE_INSENSITIVE)) {
				throw new InvalidArgumentsException(message.apply(ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE));
			}
		}
		
		if (options.contains(ParsingOptions.REMOVE_DASH_PREFIX)) {
			if (options.contains(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH)) {
				throw new InvalidArgumentsException(message.apply(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE_SINGLE_DASH));
			}
			
			if (options.contains(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH)) {
				throw new InvalidArgumentsException(message.apply(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH));
			}
			
			if (options.contains(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH)) {
				throw new InvalidArgumentsException(message.apply(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH));
			}
			
			if (options.contains(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH)) {
				throw new InvalidArgumentsException(message.apply(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH));
			}
		}
	}
	
	private ArrayList<ParsingOptions> getOptions() {
		if (!options.contains(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH) && !options.contains(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH)) {
			options.add(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH);
		}
		
		if (!options.contains(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH) && !options.contains(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH)) {
			options.add(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
		}
		
		if (!options.contains(ParsingOptions.LEAVE_DASH_PREFIX) && !options.contains(ParsingOptions.REMOVE_DASH_PREFIX)) {
			options.add(ParsingOptions.LEAVE_DASH_PREFIX);
		}
		
		if (options.contains(ParsingOptions.REMOVE_DASH_PREFIX)) {
			options.remove(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH);
			options.remove(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH);
			options.remove(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH);
			options.remove(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
			
			if (!options.contains(ParsingOptions.CASE_INSENSITIVE) && !options.contains(ParsingOptions.CASE_SENSITIVE)) {
				options.add(ParsingOptions.CASE_INSENSITIVE);
			}
		}
		
		return options;
	}
	
	private Stream<Argument> stream(String key) {
		Predicate<Argument> predicate = (argument) -> {
			if (argument.getKey().startsWith("-") && !argument.getKey().startsWith("--")) {
				return getOptions().contains(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH) ? argument.getKey().equals(key) : argument.getKey().equalsIgnoreCase(key);
			} else if (argument.getKey().startsWith("--")) {
				return getOptions().contains(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH) ? argument.getKey().equals(key) : argument.getKey().equalsIgnoreCase(key);
			}
			
			return getOptions().contains(ParsingOptions.CASE_SENSITIVE) ? argument.getKey().equals(key) : argument.getKey().equalsIgnoreCase(key);
		};
		
		return set.stream().filter(predicate);
	}
	
	@Override
	public Iterator<Argument> iterator() {
		return set.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Arguments arguments = (Arguments) o;

		if (!Objects.equals(set, arguments.set)) return false;
		return Objects.equals(options, arguments.options);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + set.hashCode();
		result = prime * result + options.hashCode();
		return result;
	}

}
