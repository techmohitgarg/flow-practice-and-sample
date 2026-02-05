package com.mohitgarg.flowpractice.flow_sample_class

import com.mohitgarg.flowpractice.utils.printWithTimePassed
import java.math.BigInteger

fun main() {
    /**
     * here we are getting the result of the function
     *
     * Time passed: 74 ms | Result 1
     * Time passed: 78 ms | Result 2
     * Time passed: 78 ms | Result 6
     * Time passed: 78 ms | Result 24
     * Time passed: 78 ms | Result 120
     * Function has been competed the task
     *
     * As per inout and output
     * ideally the problem is main thread is blocked here because "Function has been competed the task" this is message is printed
     * after the function executed completed and return the value
     */
    val startTime = System.currentTimeMillis()
    calculateFactorialOf(5).forEach {
        printWithTimePassed(startTime = startTime, message = "Result ${it}")
    }
    println("Function has been competed the task")
}

/**
 * this is the function which will return the factorial of a @param num
 * eg 120 -: calculateFactorialOf(num=5)
 */
private fun calculateFactorialOf(num: Int): List<BigInteger> = buildList {
    var factorial = BigInteger.ONE
    for (i in 1..num) {
        Thread.sleep(10)
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        add(factorial)
    }
}