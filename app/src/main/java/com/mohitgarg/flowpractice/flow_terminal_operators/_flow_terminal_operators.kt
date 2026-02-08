package com.mohitgarg.flowpractice.flow_terminal_operators

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext


/**
 * Terminal operators are suspending functions that start the collection of the flow.
 * A flow is "cold" and won't execute until a terminal operator is called.
 */
fun main() {
    flowVsList()
    flowCollectOperator()
    flowFirstOperator()
    flowLastOperator()
    flowSingleOperator()
    flowToListOrToSetOperator()
    flowFoldOperator()
    flowLaunchInOperator()
}

/**
 * Demonstrates the difference between Flow (Lazy/Cold) and List (Eager).
 * 
 * - Lists are eager: the code inside buildList runs immediately upon creation.
 * - Flows are lazy: the code inside the flow builder won't run until a terminal operator (like collect) is called.
 * Note: In this function, the Flow messages won't be printed because no terminal operator is called.
 */
fun flowVsList() {
    println("--- flowVsList ---")
    val flow = flow {
        emit(1)
        println("Emitted 1")
        emit(2)
        println("Emitted 2")
    }

    buildList {
        add(1)
        println("List Added 1")
        add(2)
        println("List Added 2")
    }
}

/**
 * 'collect' is the most basic terminal operator.
 * It processes each value emitted by the flow as it comes.
 * 
 * Output:
 * Collect-Emitting 1
 * Collect-Received 1
 * Collect-Emitting 2
 * Collect-Received 2
 */
fun flowCollectOperator() = runBlocking {
    println("--- flowCollectOperator ---")
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
 * 'first' returns the first element emitted and then cancels the flow.
 * If a predicate is provided, it returns the first element matching that predicate.
 * 
 * Output (first()):
 * First-Emitting 1
 * First-Received 1
 * 
 * Output (first { it > 1 }):
 * First-Emitting 1
 * First-Emitting 2
 * Second-Received 2
 */
fun flowFirstOperator() = runBlocking {
    println("--- flowFirstOperator ---")
    val flow = flow {
        println("First-Emitting 1")
        emit(1)
        println("First-Emitting 2")
        emit(2)
    }
    
    // Returns the very first item and cancels the flow immediately
    val firstValue = flow.first()
    println("First-Received $firstValue")

    // Returns the first item that matches the condition
    val secondValue = flow.first { it > 1 }
    println("Second-Received $secondValue")
}

/**
 * 'last' returns the last element emitted by the flow.
 * It waits for the flow to complete before returning the result.
 */
fun flowLastOperator() = runBlocking {
    println("--- flowLastOperator ---")
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
 * 'single' expects exactly one element to be emitted.
 * 
 * - If the flow emits more than one item: throws IllegalArgumentException.
 * - If the flow is empty: throws NoSuchElementException.
 */
fun flowSingleOperator() = runBlocking {
    println("--- flowSingleOperator ---")
    val flow = flow {
        println("Single-Emitting 1")
        emit(1)
    }
    val singleValue = flow.single()
    println("Single-Received $singleValue")
}

/**
 * 'toList' and 'toSet' collect all emitted items into a collection.
 * These are useful when you want to convert the entire stream into a static collection.
 */
fun flowToListOrToSetOperator() = runBlocking {
    println("--- flowToListOrToSetOperator ---")
    val flow = flow {
        println("List-Set-Emitting 1")
        emit(1)
        println("List-Set-Emitting 2")
        emit(2)
        emit(2) // Emitting duplicate to show Set behavior
    }
    val listValue = flow.toList()
    println("List-Received $listValue")
    
    val setValue = flow.toSet()
    println("Set-Received $setValue")
}

/**
 * 'fold' takes an initial value and an accumulator function.
 * It combines all emitted values into a single result starting with the initial value.
 */
fun flowFoldOperator() = runBlocking {
    println("--- flowFoldOperator ---")
    val flow = flow {
        for (i in 1..5) {
            emit(i)
        }
    }

    // Initial value is 2. Multiplies accumulator by each emitted value.
    // Result: 2 * 1 * 2 * 3 * 4 * 5 = 240
    val result = flow.fold(2) { accumulator, value ->
        accumulator * value
    }
    println("Fold-Received $result")
}

/**
 * 'launchIn' launches the collection of the flow in the provided CoroutineScope.
 * 
 * Difference from 'collect':
 * - 'collect' is a suspending function and waits until the flow is finished.
 * - 'launchIn' is non-blocking; it returns a Job immediately.
 * - It is often used with 'onEach' to handle emitted values.
 */
fun flowLaunchInOperator() = runBlocking {
    println("--- flowLaunchInOperator ---")
    val flow = flow {
        delay(100)
        println("Emitting 1")
        emit(1)
        delay(100)
        println("Emitting 2")
        emit(2)
    }
    
    val scope = CoroutineScope(EmptyCoroutineContext)

    // These two collect concurrently in the background scope
    flow
        .onEach { println("Received $it with launchIn() - A") }
        .launchIn(scope)

    flow
        .onEach { println("Received $it with launchIn() - B") }
        .launchIn(scope)

    // Normal collect blocks the execution of the next line until flow finishes
    scope.launch {
        println("Starting sequential collect in a new coroutine...")
        flow.collect {
            println("Received $it in sequential collect - 1")
        }
        flow.collect {
            println("Received $it in sequential collect - 2")
        }
    }
    
    // Keep the main thread alive long enough to see the output from background scope
    delay(1000)
}
