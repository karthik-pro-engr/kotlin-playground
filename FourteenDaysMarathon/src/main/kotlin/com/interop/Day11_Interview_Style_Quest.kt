
@file:JvmName("LibKt")


package com.interop

import JavaCaller
import java.io.IOException

// top-level utils (no @JvmOverloads)

@JvmOverloads
fun greetInterview(name: String = "Guest", polite: Boolean = true): String {
    return if (polite) "Hello, $name" else "Hi $name"
}

// a class with default constructor parameter and companion
class User @JvmOverloads constructor(val id: String, val age: Int = 30) {

    companion object {
        val defaultRole = "USER"            // simple property
        @JvmField
        val VERSION = "1.0"       // field
        @JvmStatic
        val API = "v1"           // annotated like a property (note)
        fun role(): String = defaultRole
        @JvmStatic
        fun staticRole() = defaultRole
    }

}

// calling a legacy Java API that might return null (no nullability annotations)
fun risky(): String? {
    return JavaCaller().maybeNull(true)
}

// a function that occasionally throws an IOException (no @Throws)
fun readConfig(path: String) {
    if (path.isEmpty()) throw IOException("empty")
}

// overloaded top-level functions in same file (potential JVM clash if @JvmOverloads used)
@JvmOverloads
fun transformSums(x: Int, y: Int = 0) = x + y
fun transformTimes(x: Int) = x * 2

@JvmOverloads
fun transform(x: Int, y: Int = 0) = x + y

//fun transform(x: Int) = x * 2



// top-level utility with default args (NO @JvmOverloads)
fun welcome(name: String = "Guest", shout: Boolean = false): String {
    return if (shout) "HELLO, $name" else "Hello, $name"
}

// top-level function accidentally given same JVM name as another via @JvmName
@JvmName("calc")
fun calcSum(x: Int, y: Int) = x + y

// another top-level overload that WILL conflict if @JvmOverloads used
fun calcDouble(x: Int) = x * 2

// A class with default param but no @JvmOverloads on constructor
class PersonInterOp(val name: String, val age: Int = 30) {
    companion object {
        // mixing annotations:
        const val BUILD = "1.0"          // compile-time constant -> static final field on Person
        @JvmField val TAG = "PersonTag"  // public static field on Person
        @JvmStatic val API_VERSION = "v1" // note: creates static getter, not field
        val role get() = "USER"          // property with getter on Companion
        @JvmStatic fun staticRole() = "ADMIN"
    }
}

// A property that looks non-null but comes from Java platform type call.
fun callerOfLegacy(): String {
    // com.legacy.Legacy.getName() // pretend calls Java legacy method with no null annotations
    @Suppress("UNUSED_VARIABLE")
    //val maybe: String = com.legacy.Legacy.getName() // platform type -> assigned to non-null
    return ""//maybe.toUpperCase()
}

// Function that throws a checked exception but NO @Throws
fun loadConfig(path: String) {
    if (path.isEmpty()) throw IOException("empty")
}

// A function that we intended to hide from Java clients
@JvmSynthetic
fun secretForKotlinOnly() = "secret"

