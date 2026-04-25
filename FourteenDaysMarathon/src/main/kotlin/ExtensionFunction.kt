class AExtensionClass{
    fun already() {
        println("Already")
    }
}

fun AExtensionClass?.extensionOfAClass() {
    println("ExtensionFunction")
}
fun main() {
    AExtensionClass().extensionOfAClass()
    val canBeNull:AExtensionClass?=null
    canBeNull.extensionOfAClass()
}