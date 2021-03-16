package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IntervalsAdjacencyDetectorTest {
    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() {
        SUT = new IntervalsAdjacencyDetector();
    }

    @Test
    public void test1() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(8, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(false));
    }
    @Test
    public void test2() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(5, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(true));
    }
    @Test
    public void test3() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(4, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(false));
    }
    @Test
    public void test4() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-1, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(false));
    }
    @Test
    public void test5() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(0, 2);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(false));
    }
    @Test
    public void test6() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-8, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(false));
    }
    @Test
    public void test7() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-8, -5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(false));
    }
    @Test
    public void test8() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-8, -1);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(true));
    }
    @Test
    public void test9() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-8, 2);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, CoreMatchers.is(false));
    }

}