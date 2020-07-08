package org.silentsoft.arguments.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.silentsoft.arguments.parser.ArgumentsParser.ParsingOptions;

public final class Arguments implements Iterable<Argument> {
	
	private TreeSet<Argument> set;
	
	private TreeSet<Argument> source;
	
	private ArrayList<ParsingOptions> options;
	
	protected Arguments() {
		set = new TreeSet<Argument>();
		options = new ArrayList<ParsingOptions>();
	}
	
	protected boolean add(Argument argument) {
		return set.add(argument);
	}
	
	protected Argument last() {
		return set.last();
	}
	
	public int size() {
		return set.size();
	}
	
	public Argument get(String key) {
		Optional<Argument> optional = stream(key).findAny();
		return optional.isPresent() ? optional.get() : null;
	}
	
	public boolean containsKey(String key) {
		return stream(key).findAny().isPresent();
	}
	
	/**
	 * Parsing Options with dash prefix
	 * <ul>
	 * <li>LEAVE_DASH_PREFIX <b>(default)</b></li>
	 * <li>CASE_SENSITIVE_SINGLE_DASH <b>(default)</b></li>
	 * <li>CASE_INSENSITIVE_SINGLE_DASH</li>
	 * <li>CASE_SENSITIVE_DOUBLE_DASH</li>
	 * <li>CASE_INSENSITIVE_DOUBLE_DASH <b>(default)</b></li>
	 * </ul>
	 * 
	 * Parsing Options without dash prefix
	 * <ul>
	 * <li>REMOVE_DASH_PREFIX</li>
	 * <li>CASE_SENSITIVE</li>
	 * <li>CASE_INSENSITIVE</li>
	 * </ul>
	 * 
	 * @param parsingOptions
	 * @return
	 * @throws InvalidArgumentsException
	 */
	public Arguments with(ParsingOptions... parsingOptions) throws InvalidArgumentsException {
		if (parsingOptions != null) {
			options = new ArrayList<ParsingOptions>(Arrays.asList(parsingOptions));
		}
		
		assertOptions(options);
		
		TreeSet<Argument> _set = new TreeSet<Argument>();
		if (source == null) {
			source = set;
		}
		Function<String, String> transform = (key) -> {
			return getOptions().contains(ParsingOptions.REMOVE_DASH_PREFIX) ? removeDashPrefix(key) : key;
		};
		source.forEach(argument -> {
			String key;
			if (argument.getKey().startsWith("--") && getOptions().contains(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH)) {
				key = argument.getKey().toLowerCase();
			} else if (argument.getKey().startsWith("-") && argument.getKey().startsWith("--") == false && getOptions().contains(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH)) {
				key = argument.getKey().toLowerCase();
			} else if (getOptions().contains(ParsingOptions.CASE_INSENSITIVE)) {
				key = argument.getKey().toLowerCase();
			} else {
				key = argument.getKey();
			}
			
			if (_set.stream().filter(arg -> transform.apply(arg.getKey()).equals(transform.apply(key))).findAny().isPresent() == false) {
				_set.add(Argument.of(transform.apply(key), argument.getValues().toArray(new String[] {})));
			}
		});
		
		Arguments arguments = new Arguments();
		arguments.options = getOptions();
		arguments.source = source;
		arguments.set = _set;
		
		return arguments;
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
		if (options.contains(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH) == false && options.contains(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH) == false) {
			options.add(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH);
		}
		
		if (options.contains(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH) == false && options.contains(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH) == false) {
			options.add(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
		}
		
		if (options.contains(ParsingOptions.LEAVE_DASH_PREFIX) == false && options.contains(ParsingOptions.REMOVE_DASH_PREFIX) == false) {
			options.add(ParsingOptions.LEAVE_DASH_PREFIX);
		}
		
		if (options.contains(ParsingOptions.REMOVE_DASH_PREFIX)) {
			options.remove(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH);
			options.remove(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH);
			options.remove(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH);
			options.remove(ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
			
			if (options.contains(ParsingOptions.CASE_INSENSITIVE) == false && options.contains(ParsingOptions.CASE_SENSITIVE) == false) {
				options.add(ParsingOptions.CASE_INSENSITIVE);
			}
		}
		
		return options;
	}
	
	private Stream<Argument> stream(String key) {
		Predicate<Argument> predicate = (argument) -> {
			if (argument.getKey().startsWith("-") && argument.getKey().startsWith("--") == false) {
				return getOptions().contains(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH) ? argument.getKey().equals(key) : argument.getKey().equalsIgnoreCase(key);
			} else if (argument.getKey().startsWith("--")) {
				return getOptions().contains(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH) ? argument.getKey().equals(key) : argument.getKey().equalsIgnoreCase(key);
			}
			
			return getOptions().contains(ParsingOptions.CASE_SENSITIVE) ? argument.getKey().equals(key) : argument.getKey().equalsIgnoreCase(key);
		};
		
		return set.stream().filter(predicate);
	}
	
	private String removeDashPrefix(String value) {
		int index = 0;
		
		char[] charArray = value.toCharArray();
		for (int length=charArray.length; index<length; index++) {
			if (charArray[index] == '-') {
				continue;
			}
			
			break;
		}
		
		return value.substring(index);
	}
	
	@Override
	public Iterator<Argument> iterator() {
		return set.iterator();
	}
	
}
