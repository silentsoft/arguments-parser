package org.silentsoft.arguments.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ArgumentTest {

    @Test
    public void identityTest() {
        {
            HashMap<Argument, Integer> map = new HashMap<Argument, Integer>();
            map.put(Argument.of("a"), 1);
            map.put(Argument.of("a"), 2);
            Assert.assertEquals(1, map.size());
            Assert.assertTrue(map.get(Argument.of("a")) == 2);
        }
        {
            HashMap<Argument, Integer> map = new HashMap<Argument, Integer>();
            map.put(Argument.of("a", "b"), 1);
            map.put(Argument.of("a", "b"), 2);
            Assert.assertEquals(1, map.size());
            Assert.assertTrue(map.get(Argument.of("a", "b")) == 2);
        }
        {
            HashMap<Argument, Integer> map = new HashMap<Argument, Integer>();
            map.put(Argument.of("a", "b", "c"), 1);
            map.put(Argument.of("a", "b", "c"), 2);
            Assert.assertEquals(1, map.size());
            Assert.assertTrue(map.get(Argument.of("a", "b", "c")) == 2);
        }
        {
            HashMap<Argument, Integer> map = new HashMap<Argument, Integer>();
            map.put(Argument.of("a", new String[] {"b"}), 1);
            map.put(Argument.of("a", new String[] {"b"}), 2);
            Assert.assertEquals(1, map.size());
            Assert.assertTrue(map.get(Argument.of("a", new String[] {"b"})) == 2);
        }
        {
            HashMap<Argument, Integer> map = new HashMap<Argument, Integer>();
            map.put(Argument.of("a", new String[] {"b", "c"}), 1);
            map.put(Argument.of("a", new String[] {"b", "c"}), 2);
            Assert.assertEquals(1, map.size());
            Assert.assertTrue(map.get(Argument.of("a", new String[] {"b", "c"})) == 2);
        }
    }

    @Test
    public void equalityTest() {
        Assert.assertTrue(Argument.of("a").equals(Argument.of("a")));
        Assert.assertFalse(Argument.of("a").equals(Argument.of("A")));
        Assert.assertFalse(Argument.of("a").equals(null));
        Assert.assertTrue(Argument.of("a", "b").equals(Argument.of("a", "b")));
        Assert.assertTrue(Argument.of("a", "b", "c").equals(Argument.of("a", "b", "c")));
        Assert.assertTrue(Argument.of("a", new String[] {"b"}).equals(Argument.of("a", new String[] {"b"})));
        Assert.assertTrue(Argument.of("a", new String[] {"b", "c"}).equals(Argument.of("a", new String[] {"b", "c"})));
    }

}
