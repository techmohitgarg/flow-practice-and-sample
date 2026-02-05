package com.mohitgarg.flowpractice.flow_builders

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

suspend fun main() {

    /**
     * this is the first flow builder called flowOF which will emit single
     */
    val firstFlow = flowOf<Int>(1)
    firstFlow.collect { emittedValue ->
        println("firstFlow: $emittedValue")
    }

    /**
     * this is the first flow builder called flowOF which will emitting more than one value
     */
    val secondFlow = flowOf<Int>(10, 20, 30)
    secondFlow.collect { emittedValue ->
        println("secondFlow: $emittedValue")
    }


    /**
     * this will convert the list to flow and its now ready to collect the data from producer
     */
    listOf(1, 2, 3, 4, 5, 6, 7).asFlow().collect { emittedValue ->
        println("asFlow: $emittedValue")
    }


    /**
     * here we are creating the flow builder and emit the Any data type value like String, Integer etc.
     * Also we can emit the value from another flow as well
     */
    flow {
        delay(1000)
        emit("Start Emitting Second Flow here")
        secondFlow.collect { emittedValue ->
            emit(emittedValue)
        }
        delay(1000)
        emit("Start Emitting First Flow here")
        //
        emitAll(firstFlow)

    }.collect { emittedValue ->
        println("flow{}: $emittedValue")
    }
}