package com.mohitgarg.flowpractice.flow_sample_class

import com.mohitgarg.flowpractice.utils.printWithTimePassed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigInteger

fun main() = runBlocking {

    /**
     * here we are getting the result of the function
     *
     */
    launch {
        val startTime = System.currentTimeMillis()

        createFlow().collect {
            printWithTimePassed(startTime = startTime, message = "createFlow Result ${it}")
        }

        calculateFactorialOf(5).collect {
            printWithTimePassed(startTime = startTime, message = "Result ${it}")
        }
    }
    println("Function has been competed the task")
}


fun createFlow() = flow {
    emit(1)
    delay(100)
    emit(2)
    delay(100)
    emit(3)
}

/**
 * this is the function which will return the factorial of a @param num
 * eg 120 -: calculateFactorialOf(num=5)
 */
private fun calculateFactorialOf(num: Int): Flow<BigInteger> = flow {
    var factorial = BigInteger.ONE
    for (i in 1..num) {
        delay(100)
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        emit(factorial)
    }
}.flowOn(Dispatchers.Default)