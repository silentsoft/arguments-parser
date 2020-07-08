package org.silentsoft.arguments.parser;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.silentsoft.arguments.parser.ArgumentsParser.ParsingOptions;

public class ArgumentsParserTest {
	
	@Test
	public void parseTest() throws InvalidArgumentsException {
		{
			Arguments arguments = ArgumentsParser.parse(null);
			Assert.assertNotNull(arguments);
			Assert.assertEquals(0, arguments.size());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {});
			Assert.assertNotNull(arguments);
			Assert.assertEquals(0, arguments.size());
		}
		{
			Assert.assertTrue(Argument.of("A").equals(Argument.of("A")));
			Assert.assertFalse(Argument.of("a").equals(Argument.of("A")));
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-a", "--a", "---a"});
			Assert.assertEquals(3, arguments.size());
			Assert.assertNotNull(arguments.get("-a"));
			Assert.assertNotNull(arguments.get("--a"));
			Assert.assertNotNull(arguments.get("---a"));
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-a", "-A", "--a", "--A"});
			Assert.assertEquals(3, arguments.size());
			Assert.assertNotNull(arguments.get("-a"));
			Assert.assertNotNull(arguments.get("-A"));
			Assert.assertNotNull(arguments.get("--a"));
			Assert.assertNotNull(arguments.get("--A"));
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-aa", "-aA", "--aa", "--aA"});
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
			Arguments arguments = ArgumentsParser.parse(new String[] {"-a=b", "-A=B", "--a=b", "--A=B"});
			Assert.assertEquals(3, arguments.size());
			Assert.assertTrue(arguments.containsKey("-a"));
			Assert.assertTrue(arguments.containsKey("-A"));
			
			Assert.assertEquals("b", arguments.get("-a").getValue());
			Assert.assertEquals("B", arguments.get("-A").getValue());
			Assert.assertEquals("B", arguments.get("--a").getValue());
			Assert.assertEquals("B", arguments.get("--A").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key=value"});
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("-key").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"--key=value"});
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("--key").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key", "value"});
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("-key").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"--key", "value"});
			Assert.assertEquals(1, arguments.size());
			Assert.assertEquals("value", arguments.get("--key").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key1=value1", "-key2=value2"});
			Assert.assertEquals("value1", arguments.get("-key1").getValue());
			Assert.assertEquals("value2", arguments.get("-key2").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"--key1=value1", "--key2=value2"});
			Assert.assertEquals("value1", arguments.get("--key1").getValue());
			Assert.assertEquals("value2", arguments.get("--key2").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key1", "value1", "-key2", "value2"});
			Assert.assertEquals("value1", arguments.get("-key1").getValue());
			Assert.assertEquals("value2", arguments.get("-key2").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"--key1", "value1", "--key2", "value2"});
			Assert.assertEquals("value1", arguments.get("--key1").getValue());
			Assert.assertEquals("value2", arguments.get("--key2").getValue());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key1=value1", "value2", "-key2=value3", "value4"});
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
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key1", "value1", "value2", "-key2", "value3", "value4"});
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
			Arguments arguments = ArgumentsParser.parse(new String[] {"--key1=value1", "value2", "--key2=value3", "value4"});
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
			Arguments arguments = ArgumentsParser.parse(new String[] {"--key1", "value1", "value2", "--key2", "value3", "value4"});
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
	public void withTest() throws InvalidArgumentsException {
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-a", "--a", "---a"}).with();
			Assert.assertEquals(3, arguments.size());
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-a", "--a", "---a"}).with(ParsingOptions.LEAVE_DASH_PREFIX);
			Assert.assertEquals(3, arguments.size());
			Assert.assertNotNull(arguments.get("-a"));
			Assert.assertNotNull(arguments.get("--a"));
			Assert.assertNotNull(arguments.get("---a"));
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-a", "--a", "---a"}).with(ParsingOptions.REMOVE_DASH_PREFIX);
			Assert.assertEquals(1, arguments.size());
			Assert.assertNotNull(arguments.get("a"));
			Assert.assertNull(arguments.get("-a"));
			Assert.assertNull(arguments.get("--a"));
			Assert.assertNull(arguments.get("---a"));
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-a", "--A"}).with(ParsingOptions.REMOVE_DASH_PREFIX);
			Assert.assertEquals(1, arguments.size());
			Assert.assertNotNull(arguments.get("a"));
			Assert.assertNotNull(arguments.get("A"));
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-aa", "-aA", "--aa", "--aA"}).with(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH);
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
			Arguments arguments = ArgumentsParser.parse(new String[] {"-aa", "-aA", "--aa", "--aA"}).with(ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
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
			Arguments arguments = ArgumentsParser.parse(new String[] {"-aa", "-aA", "--aa", "--aA"}).with(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH);
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
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key=value1", "value2"});
			Assert.assertEquals(1, arguments.size());
			for (Argument argument : arguments) {
				Assert.assertEquals("-key", argument.getKey());
				Assert.assertEquals("value1", argument.getValues().get(0));
				Assert.assertEquals("value2", argument.getValues().get(1));
				Assert.assertEquals("value1 value2", argument.getValue());
			}
		}
		{
			Arguments arguments = ArgumentsParser.parse(new String[] {"-key1=value1", "value2", "-key2"});
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
	
	@Test(expected = InvalidArgumentsException.class)
	public void validatorFalseTest() throws InvalidArgumentsException {
		ArgumentsParser.parse(new String[] {}, arguments -> false);
	}
	
	@Test(expected = InvalidArgumentsException.class)
	public void notStartsWithDashTest() throws InvalidArgumentsException {
		ArgumentsParser.parse(new String[] {"a"});
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
			ArgumentsParser.parse(new String[] {}, arguments -> true);
		}));
		{
			ArgumentsValidator validator = (arguments) -> {
				if (arguments.containsKey("-a") && arguments.containsKey("-b")) {
					throw new InvalidArgumentsException("'-a' and '-b' cannot be exists together.");
				}
				
				return true;
			};
			
			Assert.assertFalse(exception.test(() -> {
				ArgumentsParser.parse(new String[] {"-a"}, validator);
			}));
			Assert.assertTrue(exception.test(() -> {
				ArgumentsParser.parse(new String[] {"-a", "-b"}, validator);
			}));
		}
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.REMOVE_DASH_PREFIX);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.CASE_SENSITIVE_SINGLE_DASH, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.CASE_SENSITIVE, ParsingOptions.CASE_INSENSITIVE);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.LEAVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE_SINGLE_DASH);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE_SINGLE_DASH);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_SENSITIVE_DOUBLE_DASH);
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {}).with(ParsingOptions.REMOVE_DASH_PREFIX, ParsingOptions.CASE_INSENSITIVE_DOUBLE_DASH);
		}));
	}
	
	interface Snippet {
		abstract void run() throws InvalidArgumentsException;
	}
	
}
