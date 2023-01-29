package org.silentsoft.arguments.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.Predicate;

public class ArgumentsParserTest {

	@Test
	public void backwardsCompatibilityTest() throws InvalidArgumentsException {
		Assert.assertEquals(Arguments.parser(null).parse(), ArgumentsParser.parse(null));
		Assert.assertEquals(Arguments.parser(new String[] {}).parse(), ArgumentsParser.parse(new String[] {}));
		Assert.assertEquals(Arguments.parser(new String[] {"-a", "--a", "---a"}).parse(), ArgumentsParser.parse(new String[] {"-a", "--a", "---a"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-a", "-A", "--a", "--A"}).parse(), ArgumentsParser.parse(new String[] {"-a", "-A", "--a", "--A"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-aa", "-aA", "--aa", "--aA"}).parse(), ArgumentsParser.parse(new String[] {"-aa", "-aA", "--aa", "--aA"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-a=b", "-A=B", "--a=b", "--A=B"}).parse(), ArgumentsParser.parse(new String[] {"-a=b", "-A=B", "--a=b", "--A=B"}));
		Assert.assertEquals(Arguments.parser(new String[] {"--hello-world"}).parse(), ArgumentsParser.parse(new String[] {"--hello-world"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-key=value"}).parse(), ArgumentsParser.parse(new String[] {"-key=value"}));
		Assert.assertEquals(Arguments.parser(new String[] {"--key=value"}).parse(), ArgumentsParser.parse(new String[] {"--key=value"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-key", "value"}).parse(), ArgumentsParser.parse(new String[] {"-key", "value"}));
		Assert.assertEquals(Arguments.parser(new String[] {"--key", "value"}).parse(), ArgumentsParser.parse(new String[] {"--key", "value"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-key1=value1", "-key2=value2"}).parse(), ArgumentsParser.parse(new String[] {"-key1=value1", "-key2=value2"}));
		Assert.assertEquals(Arguments.parser(new String[] {"--key1=value1", "--key2=value2"}).parse(), ArgumentsParser.parse(new String[] {"--key1=value1", "--key2=value2"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-key1", "value1", "-key2", "value2"}).parse(), ArgumentsParser.parse(new String[] {"-key1", "value1", "-key2", "value2"}));
		Assert.assertEquals(Arguments.parser(new String[] {"--key1", "value1", "--key2", "value2"}).parse(), ArgumentsParser.parse(new String[] {"--key1", "value1", "--key2", "value2"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-key1=value1", "value2", "-key2=value3", "value4"}).parse(), ArgumentsParser.parse(new String[] {"-key1=value1", "value2", "-key2=value3", "value4"}));
		Assert.assertEquals(Arguments.parser(new String[] {"-key1", "value1", "value2", "-key2", "value3", "value4"}).parse(), ArgumentsParser.parse(new String[] {"-key1", "value1", "value2", "-key2", "value3", "value4"}));
		Assert.assertEquals(Arguments.parser(new String[] {"--key1=value1", "value2", "--key2=value3", "value4"}).parse(), ArgumentsParser.parse(new String[] {"--key1=value1", "value2", "--key2=value3", "value4"}));
		Assert.assertEquals(Arguments.parser(new String[] {"--key1", "value1", "value2", "--key2", "value3", "value4"}).parse(), ArgumentsParser.parse(new String[] {"--key1", "value1", "value2", "--key2", "value3", "value4"}));

		Predicate<Snippet> exception = (snippet) -> {
			try {
				snippet.run();
			} catch (InvalidArgumentsException e) {
				return true;
			}

			return false;
		};
		ArgumentsValidator successValidator = arguments -> true;
		ArgumentsValidator failureValidator = arguments -> false;
		Assert.assertFalse(exception.test(() -> {
			Arguments.parser(new String[] {"--hello-world"}).validate(successValidator).parse();
		}));
		Assert.assertFalse(exception.test(() -> {
			ArgumentsParser.parse(new String[] {"--hello-world"}, successValidator);
		}));
		Assert.assertTrue(exception.test(() -> {
			Arguments.parser(new String[] {"--hello-world"}).validate(failureValidator).parse();
		}));
		Assert.assertTrue(exception.test(() -> {
			ArgumentsParser.parse(new String[] {"--hello-world"}, failureValidator);
		}));
	}

}
