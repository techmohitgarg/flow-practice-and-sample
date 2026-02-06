package com.mohitgarg.flowpractice.flow_terminal_operators

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

fun main() {
    flowCollectOperator()
    flowFirstOperator()
    flowLastOperator()
    flowSingleOperator()
    flowToListOrToSetOperator()
}

fun flowVsList() {
    val flow = flow {
        emit(1)
        println("Emitted 1")
        emit(2)
        println("Emitted 2")
    }

    val list = buildList {
        add(1)
        println("Added 1")
        add(2)
        println("Emitted 2")
    }
}

/**
 * Output
 * Collect-Emitting 1
 * Collect-Received 1
 * Collect-Emitting 2
 * Collect-Received 2
 */
fun flowCollectOperator() = runBlocking {
    println("-----------------")
    val flow = flow {
        println("Collect-Emitting 1")
        emit(1)
        println("Collect-Emitting 2")
        emit(2)
    }
    flow.collect { emittedValue ->
        println("Collect-Received $emittedValue")
    }
}

/**
 * Output
 * First-Emitting 1
 * First-Received 1
 *
 * When we are checking if (it>1)
 * than the output is
 * First-Emitting 1
 * First-Emitting 2
 * Second-Received 2
 */
fun flowFirstOperator() = runBlocking {
    println("-----------------")
    val flow = flow {
        println("First-Emitting 1")
        emit(1)
        println("First-Emitting 2")
        emit(2)
    }
    val firstValue = flow.first()
    println("First-Received $firstValue")

    val secondValue = flow.first { it > 1 }
    println("Second-Received $secondValue")
}

fun flowLastOperator() = runBlocking {
    println("-----------------")
    val flow = flow {
        println("Last-Emitting 1")
        emit(1)
        println("Last-Emitting 2")
        emit(2)
    }
    val lastValue = flow.last()
    println("Last-Received $lastValue")
}


/**
 * Single is only used for single emitted item
 * if more than one item
 * Error : Exception in thread "main" java.lang.IllegalArgumentException: Flow has more than one element
 */
fun flowSingleOperator() = runBlocking {
    println("-----------------")
    val flow = flow {
        println("Single-Emitting 1")
        emit(1)
    }
    val lastValue = flow.single()
    println("Single-Received $lastValue")
}

fun flowToListOrToSetOperator() = runBlocking {
    println("-----------------")
    val flow = flow {
        println("List-Set-Emitting 1")
        emit(1)
        println("List-Set-Emitting 2")
        emit(2)
    }
    val listValue = flow.toList()
    println("List-Received $listValue")
    println()
    val setValue = flow.toList()
    println("Set-Received $setValue")
}