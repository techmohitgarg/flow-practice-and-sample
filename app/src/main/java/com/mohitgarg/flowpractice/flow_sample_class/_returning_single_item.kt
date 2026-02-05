package com.mohitgarg.flowpractice.flow_sample_class

import java.math.BigInteger

fun main() {
    /**
     * here we are getting the result of the function
     * But
     * ideally the problem is we are adding the delay here and this delay is blocking the main the thread ideally this is not correct
     */
    val result = calculateFactorialOf(5)
    print("Result ${result}")
}

/**
 * this is the function which will return the factorial of a @param num
 * eg 120 -: calculateFactorialOf(num=5)
 */
private fun calculateFactorialOf(num: Int): BigInteger {
    var factorial = BigInteger.ONE
    for (i in 1..num) {
        Thread.sleep(10)
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
    }
    return factorial
}