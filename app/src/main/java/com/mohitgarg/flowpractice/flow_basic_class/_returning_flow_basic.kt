package com.mohitgarg.flowpractice.flow_basic_class

import com.mohitgarg.flowpractice.utils.printWithTimePassed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.math.BigInteger

/**
 * Returning values: **single item** vs **List** vs **Sequence** vs **Flow**
 *
 * This file is a single place to compare four common ways to "return results" from a function,
 * using factorial as an example.
 *
 * ## Problem we’re modeling
 * Factorial for a number \(n\) is:
 *
 * \[
 * n! = 1 \times 2 \times 3 \times ... \times n
 * \]
 *
 * If we compute it step-by-step, we naturally get intermediate results:
 * for 5 → `1, 2, 6, 24, 120`
 *
 * ## Comparison (high level)
 * - **Single item (`BigInteger`)**
 *   - You only get the final value.
 *   - If you do `Thread.sleep(...)`, it blocks the current thread.
 *
 * - **List (`List<BigInteger>`)**
 *   - You get all intermediate values, but only *after* the function finishes.
 *   - Still blocking if you use `Thread.sleep(...)`.
 *
 * - **Sequence (`Sequence<BigInteger>`)**
 *   - Lazy: values are produced one-by-one while you iterate.
 *   - But if production uses `Thread.sleep(...)`, it still blocks the thread that is iterating.
 *
 * - **Flow (`Flow<BigInteger>`)**
 *   - Asynchronous, cancellable stream of values (a *cold* stream: nothing runs until collected).
 *   - Uses `delay(...)` (non-blocking) instead of `Thread.sleep(...)` (blocking).
 *   - Can change execution context using `flowOn(...)` to move upstream work off the caller thread.
 *
 * ## How to run
 * This file has a `main()` so you can run it like a regular Kotlin program (prints to console).
 * In Android Studio, you can run the `main` if your run configuration supports it.
 */
fun main() {
    println("=== Returning Values Demo (single vs list vs sequence vs flow) ===")

    runSingleItemExample()
    println()

    runListExample()
    println()

    runSequenceExample()
    println()

    runFlowExample()
    println()

    println("=== Done ===")
}

/**
 * ### 1) Returning a single value
 *
 * - You wait for the whole computation, then you get one value.
 * - This version uses `Thread.sleep`, so it **blocks** the thread while “waiting”.
 */
private fun runSingleItemExample() {
    println("--- Single item (blocking) ---")
    val startTime = System.currentTimeMillis()

    val result = calculateFactorialBlockingSingle(5)
    printWithTimePassed(startTime, "Result $result")
    println("Single-item example finished (work completed before returning).")
}

private fun calculateFactorialBlockingSingle(num: Int): BigInteger {
    var factorial = BigInteger.ONE
    for (i in 1..num) {
        Thread.sleep(10) // blocks the current thread
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
    }
    return factorial
}

/**
 * ### 2) Returning a List
 *
 * - You get all intermediate results, but only **after** the function finishes building the list.
 * - This still blocks if you use `Thread.sleep`.
 */
private fun runListExample() {
    println("--- List (blocking; results available only at the end) ---")
    val startTime = System.currentTimeMillis()

    calculateFactorialBlockingList(5).forEach { value ->
        printWithTimePassed(startTime, "Result $value")
    }
    println("List example finished (list was fully computed before iteration started).")
}

private fun calculateFactorialBlockingList(num: Int): List<BigInteger> = buildList {
    var factorial = BigInteger.ONE
    for (i in 1..num) {
        Thread.sleep(10) // blocks the current thread
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        add(factorial)
    }
}

/**
 * ### 3) Returning a Sequence
 *
 * - A `Sequence` is **lazy**: it computes values when you iterate.
 * - But if you block inside the producer (e.g., `Thread.sleep`), you block the thread consuming it.
 */
private fun runSequenceExample() {
    println("--- Sequence (lazy, but still blocking during iteration) ---")
    val startTime = System.currentTimeMillis()

    calculateFactorialBlockingSequence(5).forEach { value ->
        printWithTimePassed(startTime, "Result $value")
    }
    println("Sequence example finished (values produced during iteration).")
}

private fun calculateFactorialBlockingSequence(num: Int): Sequence<BigInteger> = sequence {
    var factorial = BigInteger.ONE
    for (i in 1..num) {
        Thread.sleep(1000) // blocks the current thread
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        yield(factorial)
    }
}

/**
 * ### 4) Returning a Flow
 *
 * Key differences compared to `Sequence`:
 * - `delay(...)` is **non-blocking** (suspends the coroutine without blocking the thread).
 * - `Flow` is **cold**: nothing happens until you collect.
 * - With `flowOn(Dispatchers.Default)`, upstream work runs on a background dispatcher.
 *
 * This demo shows two variants:
 * - **Collect directly**: you wait until collection finishes.
 * - **Collect in a launched coroutine**: code after `launch { collect { ... } }` continues immediately,
 *   unless you `join()` the launched job.
 */
private fun runFlowExample() = runBlocking {
    println("--- Flow (non-blocking & cancellable) ---")

    val startTime = System.currentTimeMillis()

    println("Collect directly (this suspends until the flow completes):")
    calculateFactorialFlow(5).collect { value ->
        printWithTimePassed(startTime, "Result $value")
    }
    printWithTimePassed(startTime, "Direct collect finished")
}


/**
 * Emits intermediate factorial values.
 *
 * - Uses `delay` (non-blocking)
 * - Runs upstream on `Dispatchers.Default` due to `flowOn`
 */
private fun calculateFactorialFlow(num: Int): Flow<BigInteger> = flow {
    var factorial = BigInteger.ONE
    for (i in 1..num) {
        delay(100)
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        emit(factorial)
    }
}.flowOn(Dispatchers.Default)

