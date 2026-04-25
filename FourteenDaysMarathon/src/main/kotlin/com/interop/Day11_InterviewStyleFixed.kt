@file:JvmName("LibUtils")

package com.interop



import JavaCaller
import java.io.IOException

// 1) Make default args visible to Java
@JvmOverloads
fun greetInterview1(name: String = "Guest", polite: Boolean = true): String {
    return if (polite) "Hello, $name" else "Hi $name"
}

// 2) Avoid @JvmName mistakes and conflict — keep simple names
fun calcSumFixed(x: Int, y: Int) = x + y
fun calcDoubleFixed(x: Int) = x * 2

// 3) Expose constructor overloads for Java
class User1 @JvmOverloads constructor(val id: String, val age: Int = 30) {

     val easy:String = "Easy"
    companion object {
        // 4) Use const for compile-time constant (becomes public static final field)
        const val VERSION = "1.0"         // Java: User.VERSION

         val assignRole = staticRole()

        // 5) Use @JvmField for a runtime field (if not const)
        @JvmField
        val TAG = "UserTag"               // Java: User.TAG

        // 6) Prefer const or @JvmField for true static field. If getter is OK, use @get:JvmStatic.
        const val API = "v1"              // Java: User.API

        // 7) add a field for role if you want field-like access from Java
        @JvmField
        val ROLE = "USER"                 // Java: User.ROLE

        // 8) static method facade
        @JvmStatic
        fun staticRole() = ROLE

         @JvmField val USER:User = User("30")
    }
}

// 9) Treat legacy return as nullable (platform type) to avoid NPE
fun risky1(): String {
    val maybe: String? = JavaCaller().maybeNull(true) // treat platform type as nullable
    return (maybe ?: "UNKNOWN")
}

// 10) Make checked exception visible to Java
@Throws(IOException::class)
fun readConfig1(path: String) {
    if (path.isEmpty()) throw IOException("empty")
}

// 11) Avoid overload collision: do not annotate with @JvmOverloads if you already have
//     a separate function with same JVM signature. Use different names or reliable overloads.
@JvmOverloads
fun transformSum(x: Int, y: Int = 0) = x + y
fun transformTimes1(x: Int) = x * 2
