@file:JvmName("Utils")

package com.interop

import JavaCaller
import org.jetbrains.annotations.Nullable
import java.io.IOException

@JvmOverloads
fun greet(name: String = "Guest", excited: Boolean = false): String {
    return if (excited) "Hi $name" else "Hi $name"
}

class PersonWithDefaultParams(name: String, age: Int = 33) {

}

class PersonWithJvmOverloadsConstructor @JvmOverloads constructor(name: String, age: Int = 34) {

    companion object {
        @JvmStatic
        fun staticHello(): String = "hello from @JvmStatic"
        fun nonStaticHello(): String = "hello from Companion"

        val VAL = "v"
        @JvmField val JVM_FIELD_VAL = "v"
        @JvmStatic val StaticVAL = "v"
        val PROP get() = "p"
        var PROP_WITHOUT_GETTER  = "p"


    }


}

class InterOp {

    fun defaultParams(name: String, age: Int, sex: String = "Male") {

    }

    @JvmOverloads
    fun overloadDefaultParams(name: String, age: Int, sex: String = "Male") {

    }

}


fun kotlinNullable(): String? = null


fun kotlinNonNullable(): String = "Karthik"

fun mayNotThrow(bad: Boolean) {
    if (bad) throw IOException("boom")
}

@Throws(IOException::class)
fun mayThrow(bad: Boolean) {
    if (bad) throw IOException("boom")
}

fun main() {
    val javaCaller = JavaCaller()
    javaCaller.caller();

    val maybeNull: String? = javaCaller.maybeNull(true)
    println(maybeNull?.length ?: "0")
    val maybeNotNull: String? = javaCaller.maybeNull(false)
    println(maybeNotNull?.length ?: "0")
    val maybeNullWithoutNullSafe = javaCaller.maybeNull(true)
//    val mayBeValue = javaCaller.mayBeValue
    // println(maybeNullWithoutNullSafe.length) without null safe which cause NPE
}
