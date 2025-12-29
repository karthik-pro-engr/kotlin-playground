@DslMarker
annotation class HtmlDsl

// simple node types
sealed class Node
data class TextNode(val text: String) : Node()
@HtmlDsl
open class Tag(val name: String) : Node() {
    private val children = mutableListOf<Node>()
    private val attrs = linkedMapOf<String, String>()

    fun id(v: String) {
        println(v)
    }
    // allow "div" { ... } inside a Tag receiver
    operator fun String.invoke(block: Tag.() -> Unit) {
        val child = Tag(this)
        child.block()
        children += child
    }

    // allow +"text" inside current Tag scope
    operator fun String.unaryPlus() {
        children += TextNode(this)
    }

    fun attr(key: String, value: String) { attrs[key] = value }

    // render helper
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("<$name")
        if (attrs.isNotEmpty()) attrs.forEach { (k, v) -> sb.append(" $k=\"${v}\"") }
        if (children.isEmpty()) {
            sb.append("/>")
        } else {
            sb.append(">\n")
            for (c in children) {
                val line = when (c) {
                    is TextNode -> "  ${c.text}\n"
                    is Tag -> c.toString().prependIndent("  ") + "\n"
                    else -> c.toString() + "\n"
                }
                sb.append(line)
            }
            sb.append("</$name>")
        }
        return sb.toString()
    }
}

// entry point: html { ... }
fun html(block: Tag.() -> Unit): Tag {
    val root = Tag("html")
    root.block()
    return root
}

// usage
fun main() {
    val page = html {
        "head" {
            "title" { +"My Page" }
        }
        "body" {
            "h1" { +"Hello" }
        }
    }
//    println(page)

    val html = html {
        "body" {
            "div" {
                // inside div's receiver, `this` is the inner Tag
                id("outer")      // <-- oops: this is calling `id` on the outer receiver,
                // not on the `div` you probably intended
                +"Hello"
            }
        }
    }
    println(html)
}




