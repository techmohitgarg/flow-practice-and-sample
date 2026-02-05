package com.mohitgarg.flowpractice.utils

fun printWithTimePassed(
    startTime: Long,
    message: String
) {
    val timePassed = System.currentTimeMillis() - startTime
    println("Time passed: ${timePassed} ms | $message")
}