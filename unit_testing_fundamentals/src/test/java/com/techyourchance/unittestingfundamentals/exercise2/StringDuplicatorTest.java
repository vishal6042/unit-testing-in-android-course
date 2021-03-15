package com.techyourchance.unittestingfundamentals.exercise2;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setUp() {
        SUT = new StringDuplicator();
    }

    @Test
    public void test1() {
        String result = SUT.duplicate("aa");
        Assert.assertThat(result, CoreMatchers.is("aaaa"));
    }

    @Test
    public void test2() {
        String result = SUT.duplicate("");
        Assert.assertThat(result, CoreMatchers.is(""));
    }

}