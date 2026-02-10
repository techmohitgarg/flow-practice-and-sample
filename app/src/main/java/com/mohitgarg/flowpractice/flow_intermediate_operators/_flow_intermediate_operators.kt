package com.mohitgarg.flowpractice.flow_intermediate_operators


import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.runBlocking

/**
 * Entry point for this sample.
 *
 * This uses [runBlocking] to start a coroutine on the current thread and then sequentially runs
 * a set of small examples demonstrating common **Flow intermediate operators**.
 *
 * Notes:
 * - Kotlin [kotlinx.coroutines.flow.Flow] is **cold**: nothing in a flow runs until it is collected.
 * - Each function below creates/collects its own flow to show operator behavior in isolation.
 */
fun main() = runBlocking {
    flowMapOperator(tableOf = 2)
    flowMapNotNullOperator()
    flowFilterOperator()
    flowTakeOperator(takeValue = 5)
    flowDropOperator(dropValue = 5)
    flowTransformOperator()
    flowWithIndexOperator()
    flowDistinctUntilChangedOperator()
}

/**
 * Demonstrates the [map] intermediate operator by producing a multiplication table.
 *
 * [map] transforms each upstream emission into a new value (1-to-1 mapping) without changing the
 * timing of emissions; it is **lazy** and only executes when a terminal operator (like `collect`)
 * is invoked.
 *
 * Implementation detail in this sample:
 * - A new `flow { emit(i) }` is created for each `i` in `1..10` and then collected immediately.
 *   This is intentionally verbose for learning; in real code you’d usually build one flow and
 *   transform it once.
 *
 * @param tableOf The multiplier used to compute the table.
 */
suspend fun flowMapOperator(tableOf: Int) {
    println("-----flowMapOperator-----")
    for (i in 1..10) {
        flow {
            emit(i)
        }.map {
            it * tableOf
        }.collect { collectedValue ->
            println("$tableOf * $i = $collectedValue")
        }
    }
    println("------------------------------")
}

/**
 * Demonstrates [mapNotNull] with a flow that emits nullable values.
 *
 * [mapNotNull] combines two steps:
 * - transforms the upstream item (like [map])
 * - **filters out null results** (like `filterNotNull`)
 *
 * In other words, the downstream collector only receives non-null mapped values.
 *
 * Tip:
 * - `mapNotNull { ... }` is often equivalent to `map { ... }.filterNotNull()`, but avoids creating
 *   an intermediate stream of nulls and reads more clearly when “null means drop it”.
 */
suspend fun flowMapNotNullOperator() {
    println("-----flowMapNotNullOperator-----")
    flow {
        emit(1)
        emit(null)
        emit(2)
        emit(null)
        emit(3)
        emit(null)
    }.mapNotNull { value ->
        value?.times(2)
    }.collect { collectedValue ->
        println("mapNotNull: $collectedValue")
    }
    println("------------------------------")
}

/**
 * Demonstrates Flow filtering operators:
 * - [filter]: keeps values that match a predicate
 * - [filterNot]: keeps values that do **not** match a predicate
 * - [filterNotNull]: drops nulls from a `Flow<T?>` producing `Flow<T>`
 * - [filterIsInstance]: keeps only values of a given runtime type (useful for mixed-type flows)
 *
 * Key idea:
 * - These operators are **intermediate** and are executed only when the flow is collected.
 */
suspend fun flowFilterOperator() {
    println("-----flowFilterOperator-----")
    val flow = flow {
        for (i in 1..20) {
            emit(i)
        }
    }

    // Filter has multiple operators; below are a few common ones.
    // filter: keeps values that match the predicate.
    println("-----filter-----")
    flow.filter { emittedValue ->
        emittedValue % 2 == 0
    }.collect { collectedValue ->
        println("filter: $collectedValue")
    }
    println("----------------")

    // filterNot: keeps values that do NOT match the predicate.
    println("-----filterNot-----")
    flow.filterNot { emittedValue ->
        emittedValue % 2 == 0
    }.collect { collectedValue ->
        println("filterNot: $collectedValue")
    }
    println("----------------")

    // filterNotNull: removes null values from a nullable flow.
    println("-----filterNotNull-----")
    val flow1 = flowOf(1, 2, null, 4, null, 6, 7, null, 8)
    flow1.filterNotNull().collect { collectedValue ->
        println("filterNotNull: $collectedValue")
    }
    println("----------------")

    println("-----filterIsInstance-----")
    /**
     *
     * filterIsInstance Means
     * if we pass
     * filterIsInstance<String>() than output will be
     * filterIsInstance: Emitting 1
     * filterIsInstance: Emitting 2
     * and if we pass
     * filterIsInstance<Int>() than output will be
     * filterIsInstance: 1
     * filterIsInstance: 2
     *
     */
    flow {
        emit(1)
        emit("Emitting 1")
        emit(2)
        emit("Emitting 2")
    }.filterIsInstance<String>().collect { collectedValue ->
        println("filterIsInstance: $collectedValue")
    }
    println("----------------")

    println("------------------------------")
}

/**
 * Demonstrates [take] and [takeWhile].
 *
 * - [take] collects only the first [takeValue] elements, then **cancels** the upstream flow.
 * - [takeWhile] collects elements while a predicate remains true; as soon as it becomes false,
 *   it stops and **cancels** upstream.
 *
 * Cancellation detail:
 * - The upstream producer is cooperatively cancelled; in real flows this is important because it
 *   prevents extra work once the downstream no longer needs values.
 *
 * @param takeValue How many items to take (or the threshold used by `takeWhile` in this sample).
 */
suspend fun flowTakeOperator(takeValue: Int) {
    println("-----flowTakeOperator-----")
    println("-----take-----")
    flowOf(1, 2, 3, 4, 5, 6, 7, 8)
        .take(takeValue)
        .collect { collectedValue ->
            println("take $collectedValue")
        }
    println("----------------")

    // takeWhile: keep collecting while predicate is true; stop at the first false.
    println("-----takeWhile-----")
    flowOf(1, 2, 3, 4, 5, 6, 7, 8)
        .takeWhile { it < takeValue }
        .collect { collectedValue ->
            println("takeWhile $collectedValue")
        }
    println("----------------")

    println("------------------------------")
}


/**
 * Demonstrates [drop] and [dropWhile].
 *
 * - [drop] skips the first [dropValue] emissions and then emits the rest.
 * - [dropWhile] skips emissions while a predicate is true, then starts emitting from the first
 *   element that makes the predicate false (and emits all subsequent values).
 *
 * @param dropValue How many items to drop (or the threshold used by `dropWhile` in this sample).
 */
suspend fun flowDropOperator(dropValue: Int) {
    println("-----flowDropOperator-----")
    println("-----drop-----")
    flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .drop(dropValue)
        .collect { collectedValue ->
            println("drop $collectedValue")
        }
    println("----------------")

    println("-----dropWhile-----")
    flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .dropWhile { it < dropValue }
        .collect { collectedValue ->
            println("dropWhile $collectedValue")
        }
    println("----------------")

    println("------------------------------")
}


/**
 * Demonstrates the [transform] operator.
 *
 * [transform] is a “power” operator that can:
 * - emit **zero**, **one**, or **many** values for each upstream value
 * - change the emitted type (e.g., `Int -> String` here)
 * - apply arbitrary logic (it can behave like `map`, `filter`, and small `flatMap`-style expansions)
 *
 * In this example, for every upstream `Int`, we emit two `String` values: the original and a
 * derived “modified” value.
 */
suspend fun flowTransformOperator() {
    println("-----flowTransformOperator-----")
    println("-----transform-----")
    flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .transform {
            emit("Initial Value $it")
            emit("Modified Value ${it * 2}")
        }
        .collect { collectedValue ->
            println("transform $collectedValue")
        }
    println("----------------")

    println("------------------------------")
}

/**
 * Demonstrates [withIndex].
 *
 * [withIndex] pairs each emitted value with its **zero-based index** (0, 1, 2, ...), producing
 * a `Flow<IndexedValue<T>>`.
 *
 * This is useful when you need both the item and its position, for logging, UI lists, etc.
 */
suspend fun flowWithIndexOperator() {
    println("-----flowWithIndexOperator-----")
    println("-----withIndex-----")
    flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .withIndex()
        .collect { collectedValue ->
            print("withIndex ${collectedValue.index} ")
            print("withValue ${collectedValue.value}")
            println()
        }
    println("----------------")

    println("------------------------------")
}

/**
 * Demonstrates [distinctUntilChanged].
 *
 * [distinctUntilChanged] filters out **consecutive duplicates** only.
 * Example: `flowOf(1, 1, 2, 1)` becomes `1, 2, 1` because only the second `1` is a consecutive
 * duplicate of the first; the later `1` is not consecutive to another `1`.
 *
 * This is commonly used to avoid reacting multiple times to the same state update in a row.
 */
suspend fun flowDistinctUntilChangedOperator() {
    println("-----flowDistinctUntilChangedOperator-----")
    println("-----distinctUntilChanged-----")
    flowOf(1, 1, 2, 1, 2, 3, 4, 5, 1)
        .distinctUntilChanged()
        .collect { collectedValue ->
            println("distinctUntilChanged  $collectedValue")
        }
    println("----------------")

    println("------------------------------")
}


