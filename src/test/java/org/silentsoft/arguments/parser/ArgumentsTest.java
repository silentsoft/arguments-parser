package org.silentsoft.arguments.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class ArgumentsTest {
	
	@Test
	public void parseTest() throws InvalidArgumentsException {
		{
			Arguments arguments = Arguments.parser(null).parse();
			Assert.assertNotNull(arguments);
			Assert.assertEquals(0, arguments.size());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {}).parse();
			Assert.assertNotNull(arguments);
			Assert.assertEquals(0, arguments.size());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "--a", "---a"}).parse();
			Assert.assertEquals(3, arguments.size());
			Assert.assertNotNull(arguments.get("-a"));
			Assert.assertNotNull(arguments.get("--a"));
			Assert.assertNotNull(arguments.get("---a"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "-A", "--a", "--A"}).parse();
			Assert.assertEquals(3, arguments.size());
			Assert.assertNotNull(arguments.get("-a"));
			Assert.assertNotNull(arguments.get("-A"));
			Assert.assertNotNull(arguments.get("--a"));
			Assert.assertNotNull(arguments.get("--A"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-aa", "-aA", "--aa", "--aA"}).parse();
			Assert.assertEquals(3, arguments.size());
			Assert.assertTrue(arguments.containsKey("-aa"));
			Assert.assertTrue(arguments.containsKey("-aA"));
			Assert.assertFalse(arguments.containsKey("-Aa"));
			Assert.assertFalse(arguments.containsKey("-AA"));
			
			Assert.assertTrue(arguments.containsKey("--aa"));
			Assert.assertTrue(arguments.containsKey("--aA"));
			Assert.assertTrue(arguments.containsKey("--Aa"));
			Assert.assertTrue(arguments.containsKey("--AA"));
			
			Assert.assertNull(arguments.get("-aa").getValue());
			Assert.assertNull(arguments.get("-AA"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a=b", "-A=B", "--a=b", "--A=B"}).parse();
			Assert.assertEquals(3, arguments.size());
			Assert.assertTrue(arguments.containsKey("-a"));
			Assert.assertTrue(arguments.containsKey("-A"));
			
			Assert.assertEquals("b", arguments.get("-a").getValue());
			Assert.assertEquals("B", arguments.get("-A").getValue());
			Assert.assertEquals(2, arguments.get("--a").getValues().size());
			Assert.assertEquals(2, arguments.get("--A").getValues().size());
			Assert.assertEquals(arguments.get("--a"), arguments.get("--A"));
			Assert.assertTrue(arguments.get("--a").getValues().contains("b"));
			Assert.assertTrue(arguments.get("--a").getValues().contains("B"));
			Assert.assertTrue(arguments.get("--A").getValues().contains("b"));
			Assert.assertTrue(arguments.get("--A").getValues().contains("B"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--hello-world"}).parse();
			Assert.assertNotNull(arguments.get("--hello-world"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-key=value"}).parse();
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("-key").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--key=value"}).parse();
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("--key").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-key", "value"}).parse();
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("-key").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--key", "value"}).parse();
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("--key").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-key1=value1", "-key2=value2"}).parse();
			Assert.assertEquals("value1", arguments.get("-key1").getValue());
			Assert.assertEquals("value2", arguments.get("-key2").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--key1=value1", "--key2=value2"}).parse();
			Assert.assertEquals("value1", arguments.get("--key1").getValue());
			Assert.assertEquals("value2", arguments.get("--key2").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-key1", "value1", "-key2", "value2"}).parse();
			Assert.assertEquals("value1", arguments.get("-key1").getValue());
			Assert.assertEquals("value2", arguments.get("-key2").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--key1", "value1", "--key2", "value2"}).parse();
			Assert.assertEquals("value1", arguments.get("--key1").getValue());
			Assert.assertEquals("value2", arguments.get("--key2").getValue());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-key1=value1", "value2", "-key2=value3", "value4"}).parse();
			Assert.assertTrue(arguments.containsKey("-key1"));
			Assert.assertEquals(2, arguments.get("-key1").getValues().size());
			Assert.assertTrue(arguments.get("-key1").getValues().contains("value1"));
			Assert.assertTrue(arguments.get("-key1").getValues().contains("value2"));
			Assert.assertFalse(arguments.get("-key1").getValues().contains("value3"));
			
			Assert.assertTrue(arguments.containsKey("-key2"));
			Assert.assertEquals(2, arguments.get("-key2").getValues().size());
			Assert.assertTrue(arguments.get("-key2").getValues().contains("value3"));
			Assert.assertTrue(arguments.get("-key2").getValues().contains("value4"));
			Assert.assertFalse(arguments.get("-key2").getValues().contains("value5"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-key1", "value1", "value2", "-key2", "value3", "value4"}).parse();
			Assert.assertTrue(arguments.containsKey("-key1"));
			Assert.assertEquals(2, arguments.get("-key1").getValues().size());
			Assert.assertTrue(arguments.get("-key1").getValues().contains("value1"));
			Assert.assertTrue(arguments.get("-key1").getValues().contains("value2"));
			Assert.assertFalse(arguments.get("-key1").getValues().contains("value3"));
			
			Assert.assertTrue(arguments.containsKey("-key2"));
			Assert.assertEquals(2, arguments.get("-key2").getValues().size());
			Assert.assertTrue(arguments.get("-key2").getValues().contains("value3"));
			Assert.assertTrue(arguments.get("-key2").getValues().contains("value4"));
			Assert.assertFalse(arguments.get("-key2").getValues().contains("value5"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--key1=value1", "value2", "--key2=value3", "value4"}).parse();
			Assert.assertTrue(arguments.containsKey("--key1"));
			Assert.assertEquals(2, arguments.get("--key1").getValues().size());
			Assert.assertTrue(arguments.get("--key1").getValues().contains("value1"));
			Assert.assertTrue(arguments.get("--key1").getValues().contains("value2"));
			Assert.assertFalse(arguments.get("--key1").getValues().contains("value3"));
			
			Assert.assertTrue(arguments.containsKey("--key2"));
			Assert.assertEquals(2, arguments.get("--key2").getValues().size());
			Assert.assertTrue(arguments.get("--key2").getValues().contains("value3"));
			Assert.assertTrue(arguments.get("--key2").getValues().contains("value4"));
			Assert.assertFalse(arguments.get("--key2").getValues().contains("value5"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--key1", "value1", "value2", "--key2", "value3", "value4"}).parse();
			Assert.assertTrue(arguments.containsKey("--key1"));
			Assert.assertEquals(2, arguments.get("--key1").getValues().size());
			Assert.assertTrue(arguments.get("--key1").getValues().contains("value1"));
			Assert.assertTrue(arguments.get("--key1").getValues().contains("value2"));
			Assert.assertFalse(arguments.get("--key1").getValues().contains("value3"));
			
			Assert.assertTrue(arguments.containsKey("--key2"));
			Assert.assertEquals(2, arguments.get("--key2").getValues().size());
			Assert.assertTrue(arguments.get("--key2").getValues().contains("value3"));
			Assert.assertTrue(arguments.get("--key2").getValues().contains("value4"));
			Assert.assertFalse(arguments.get("--key2").getValues().contains("value5"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--key2", "value3", "value4", "--key1", "value1", "value2"}).parse();
			Assert.assertTrue(arguments.containsKey("--key1"));
			Assert.assertEquals(2, arguments.get("--key1").getValues().size());
			Assert.assertTrue(arguments.get("--key1").getValues().contains("value1"));
			Assert.assertTrue(arguments.get("--key1").getValues().contains("value2"));
			Assert.assertFalse(arguments.get("--key1").getValues().contains("value3"));

			Assert.assertTrue(arguments.containsKey("--key2"));
			Assert.assertEquals(2, arguments.get("--key2").getValues().size());
			Assert.assertTrue(arguments.get("--key2").getValues().contains("value3"));
			Assert.assertTrue(arguments.get("--key2").getValues().contains("value4"));
			Assert.assertFalse(arguments.get("--key2").getValues().contains("value5"));
		}
	}

	@Test
	public void valueTest() throws InvalidArgumentsException {
		Arguments arguments = Arguments.parser(new String[] {"--key1=value1", "--key2=value2", "value3"}).parse();
		Assert.assertEquals(arguments.getValue("--key1"), arguments.get("--key1").getValue());
		Assert.assertEquals(arguments.getValues("--key2"), arguments.get("--key2").getValues());

		Assert.assertNull(arguments.getValue("--not-existing-key"));
		Assert.assertEquals("defaultValue", arguments.getValue("--not-existing-key", "defaultValue"));
		Assert.assertEquals(Arrays.asList("a", "b"), arguments.getValues("--not-existing-key", Arrays.asList("a", "b")));
	}

	@Test
	public void valuesTest() throws InvalidArgumentsException {
		{
			Arguments arguments = Arguments.parser(new String[] {"--d-token=aaaa-bbbb-cccc-dddd", "--d-tags=tag-b", "tag-a"}).parse();
			Assert.assertEquals("aaaa-bbbb-cccc-dddd", arguments.getValue("--d-token"));
			Assert.assertEquals(Arrays.asList("aaaa-bbbb-cccc-dddd"), arguments.getValues("--d-token"));
			Assert.assertEquals(Arrays.asList("tag-b", "tag-a"), arguments.getValues("--d-tags"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--d-token=aaaa-bbbb-cccc-dddd", "--d-tags=tag-b", "--d-tags=tag-a"}).parse();
			Assert.assertEquals("aaaa-bbbb-cccc-dddd", arguments.getValue("--d-token"));
			Assert.assertEquals(Arrays.asList("aaaa-bbbb-cccc-dddd"), arguments.getValues("--d-token"));
			Assert.assertEquals(Arrays.asList("tag-b", "tag-a"), arguments.getValues("--d-tags"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"--d-token=aaaa-bbbb-cccc-dddd", "--d-tags", "tag-b", "tag-a"}).parse();
			Assert.assertEquals("aaaa-bbbb-cccc-dddd", arguments.getValue("--d-token"));
			Assert.assertEquals(Arrays.asList("aaaa-bbbb-cccc-dddd"), arguments.getValues("--d-token"));
			Assert.assertEquals(Arrays.asList("tag-b", "tag-a"), arguments.getValues("--d-tags"));
		}
	}

	@Test
	public void helpTest() throws InvalidArgumentsException {
		AtomicBoolean called = new AtomicBoolean(false);
		Arguments.parser(new String[] {"-help"}).help(arguments -> called.set(true)).parse();
		Assert.assertTrue(called.get());
	}

	@Test
	public void requiresHelpTest() throws InvalidArgumentsException {
		Assert.assertTrue(Arguments.parser(new String[] {"-help"}).parse().requiresHelp());
		Assert.assertTrue(Arguments.parser(new String[] {"--help"}).parse().requiresHelp());
		Assert.assertTrue(Arguments.parser(new String[] {"-?"}).parse().requiresHelp());
		Assert.assertTrue(Arguments.parser(new String[] {"--?"}).parse().requiresHelp());

		Assert.assertFalse(Arguments.parser(new String[] {"--hello-world"}).parse().requiresHelp());
	}
	
	@Test
	public void parsingOptionTest() throws InvalidArgumentsException {
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "--a", "---a"}).parse();
			Assert.assertEquals(3, arguments.size());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "--a", "---a"}, null).parse();
			Assert.assertEquals(3, arguments.size());
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "--a", "---a"}, ParsingOptions.LEAVE_DASH_PREFIX).parse();
			Assert.assertEquals(3, arguments.size());
			Assert.assertNotNull(arguments.get("-a"));
			Assert.assertNotNull(arguments.get("--a"));
			Assert.assertNotNull(arguments.get("---a"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "--a", "---a"}, ParsingOptions.REMOVE_DASH_PREFIX).parse();
			Assert.assertEquals(1, arguments.size());
			Assert.assertNotNull(arguments.get("a"));
			Assert.assertNull(arguments.get("-a"));
			Assert.assertNull(arguments.get("--a"));
			Assert.assertNull(arguments.get("---a"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "--A"}, ParsingOptions.REMOVE_DASH_PREFIX).parse();
			Assert.assertEquals(1, arguments.size());
			Assert.assertNotNull(arguments.get("a"));
			Assert.assertNotNull(arguments.get("A"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-a", "--A"}, ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE).parse();
			Assert.assertEquals(2, arguments.size());
			Assert.assertNotNull(arguments.get("a"));
			Assert.assertNotNull(arguments.get("A"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-aa", "-aA", "--aa", "--aA"}, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH).parse();
			Assert.assertEquals(3, arguments.size());
			Assert.assertTrue(arguments.containsKey("-aa"));
			Assert.assertTrue(arguments.containsKey("-aA"));
			Assert.assertTrue(arguments.containsKey("-Aa"));
			Assert.assertTrue(arguments.containsKey("-AA"));
			
			Assert.assertTrue(arguments.containsKey("--aa"));
			Assert.assertTrue(arguments.containsKey("--aA"));
			Assert.assertFalse(arguments.containsKey("--Aa"));
			Assert.assertFalse(arguments.containsKey("--AA"));
			
			Assert.assertNull(arguments.get("--aa").getValue());
			Assert.assertNull(arguments.get("--AA"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-aa", "-aA", "--aa", "--aA"}, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH).parse();
			Assert.assertEquals(2, arguments.size());
			Assert.assertTrue(arguments.containsKey("-aa"));
			Assert.assertTrue(arguments.containsKey("-aA"));
			Assert.assertTrue(arguments.containsKey("-Aa"));
			Assert.assertTrue(arguments.containsKey("-AA"));
			
			Assert.assertTrue(arguments.containsKey("--aa"));
			Assert.assertTrue(arguments.containsKey("--aA"));
			Assert.assertTrue(arguments.containsKey("--Aa"));
			Assert.assertTrue(arguments.containsKey("--AA"));
			
			Assert.assertNull(arguments.get("--aa").getValue());
			Assert.assertNotNull(arguments.get("--AA"));
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-aa", "-aA", "--aa", "--aA"}, ParsingOptions.CASE_SENSITIVE_SINGLE_DASH, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH).parse();
			Assert.assertEquals(4, arguments.size());
			Assert.assertTrue(arguments.containsKey("-aa"));
			Assert.assertTrue(arguments.containsKey("-aA"));
			Assert.assertFalse(arguments.containsKey("-Aa"));
			Assert.assertFalse(arguments.containsKey("-AA"));
			
			Assert.assertTrue(arguments.containsKey("--aa"));
			Assert.assertTrue(arguments.containsKey("--aA"));
			Assert.assertFalse(arguments.containsKey("--Aa"));
			Assert.assertFalse(arguments.containsKey("--AA"));
			
			Assert.assertNull(arguments.get("--aa").getValue());
			Assert.assertNull(arguments.get("--AA"));
		}
	}
	
	@Test
	public void iterationTest() throws InvalidArgumentsException {
		{
			Arguments arguments = Arguments.parser(new String[] {"-key=value1", "value2"}).parse();
			Assert.assertEquals(1, arguments.size());
			for (Argument argument : arguments) {
				Assert.assertEquals("-key", argument.getKey());
				Assert.assertEquals("value1", argument.getValues().get(0));
				Assert.assertEquals("value2", argument.getValues().get(1));
				Assert.assertEquals("value1 value2", argument.getValue());
			}
		}
		{
			Arguments arguments = Arguments.parser(new String[] {"-key1=value1", "value2", "-key2"}).parse();
			Assert.assertEquals(2, arguments.size());
			AtomicInteger count = new AtomicInteger(0);
			arguments.forEach(argument -> {
				switch (count.getAndIncrement()) {
				case 0:
					Assert.assertEquals("-key1", argument.getKey());
					Assert.assertEquals("value1", argument.getValues().get(0));
					Assert.assertEquals("value2", argument.getValues().get(1));
					Assert.assertEquals("value1 value2", argument.getValue());
					break;
				case 1:
					Assert.assertEquals("-key2", argument.getKey());
					Assert.assertNull(argument.getValue());
					break;
				}
			});
		}
	}
	
	@Test
	public void identityTest() throws InvalidArgumentsException {
		{
			HashMap<Arguments, Integer> map = new HashMap<Arguments, Integer>();
			map.put(Arguments.parser(null).parse(), 1);
			map.put(Arguments.parser(null).parse(), 2);
			Assert.assertEquals(1, map.size());
		}
	}

	@Test
	public void equalityTest() throws InvalidArgumentsException {
		Assert.assertFalse(Arguments.parser(new String[] {"--hello-world"}).parse().equals(null));
		Assert.assertFalse(Arguments.parser(new String[] {"--hello-world"}).parse().equals("--hello-world"));
		{
			Arguments arguments = Arguments.parser(new String[] {"--hello-world"}).parse();
			Assert.assertTrue(arguments.equals(arguments));
		}
		Assert.assertTrue(Arguments.parser(new String[] {"--hello-world"}).parse().equals(Arguments.parser(new String[] {"--hello-world"}).parse()));
		Assert.assertFalse(Arguments.parser(new String[] {"--hello-world"}).parse().equals(Arguments.parser(new String[] {"--hello", "--world"}).parse()));
	}
	
	@Test(expected = InvalidArgumentsException.class)
	public void validatorFalseTest() throws InvalidArgumentsException {
		Arguments.parser(new String[] {}).validate(arguments -> false).parse();
	}
	
	@Test(expected = InvalidArgumentsException.class)
	public void missingKeyTest() throws InvalidArgumentsException {
		Arguments.parser(new String[] {"--"}).parse();
	}
	
	@Test(expected = InvalidArgumentsException.class)
	public void notStartsWithDashTest() throws InvalidArgumentsException {
		Arguments.parser(new String[] {"a"}).parse();
	}
	
	@Test
	public void invalidArgumentsExceptionTest() {
		Predicate<Snippet> exception = (snippet) -> {
			try {
				snippet.run();
			} catch (InvalidArgumentsException e) {
				return true;
			}
			
			return false;
		};
		
		Assert.assertFalse(exception.test(() -> {
			Arguments.parser(new String[] {}).validate(arguments -> true).parse();
		}));
		{
			ArgumentsValidator validator = (arguments) -> {
				if (arguments.containsKey("-a") && arguments.containsKey("-b")) {
					throw new InvalidArgumentsException("'-a' and '-b' cannot be exists together.");
				}
				
				return true;
			};
			
			Assert.assertFalse(exception.test(() -> {
				Arguments.parser(new String[] {"-a"}).validate(null).parse();
			}));
			Assert.assertFalse(exception.test(() -> {
				Arguments.parser(new String[] {"-a"}).validate(validator).parse();
			}));
			Assert.assertTrue(exception.test(() -> {
				Arguments.parser(new String[] {"-a", "-b"}).validate(validator).parse();
			}));
		}
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.REMOVE_DASH_PREFIX).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.CASE_SENSITIVE_SINGLE_DASH, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.CASE_SENSITIVE, ParsingOptions.CASE_INSENSITIVE).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE_SINGLE_DASH).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {}, ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH).parse();
		}));
	}
	
}
