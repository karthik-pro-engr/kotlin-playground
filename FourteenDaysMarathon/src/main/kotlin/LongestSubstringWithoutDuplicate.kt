class LongestSubstringWithoutDuplicate {
    fun findLongestSubString(input: String): Int {
        val map = HashMap<Char, Int>()
        var start = 0
        var maxLen = 0
        for (i in input.indices) {
            val currentChar = input[i]
            val prev = map[currentChar]
            var len = 0

            if (prev != null && prev >= start) {
                start = prev + 1

            }
            map[currentChar] = i
            len = i - start + 1
            if (len > maxLen) {
                maxLen = len
            }
        }

        return maxLen
    }

    fun reverseVowelsOnly(input: String): CharArray {
        val processingString = input.toCharArray()
        fun isVowel(c: Char): Boolean {
            return when (c) {
                'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' -> true
                else -> false
            }
        }

        var start = 0
        var end = input.length - 1
        while (start < end) {

            if (isVowel(input[start]) && isVowel(input[end])) {
                val temp = input[start]
                processingString[start] = input[end]
                processingString[end] = temp
                start++
                end--
            } else {
                if (!isVowel(input[end])) {
                    end--
                }
                if (!isVowel(input[start])) {
                    start++
                }
            }
        }
        return processingString

    }


}


fun main() {
    /*    println("abcdb ${LongestSubstringWithoutDuplicate().findLongestSubString("abcdb")}")
        println("aaaaa ${LongestSubstringWithoutDuplicate().findLongestSubString("aaaaa")}")
        println("abbbbbaaa ${LongestSubstringWithoutDuplicate().findLongestSubString("abbbbbaaa")}")
        println("  ${LongestSubstringWithoutDuplicate().findLongestSubString(" ")}")*/

    /*  println(
          "input string is hello-> reversed string is ${
              LongestSubstringWithoutDuplicate().reverseVowelsOnly("hello").joinToString()
          }"
      )
      println(
          "input string is Leetcode-> reversed string is ${
              LongestSubstringWithoutDuplicate().reverseVowelsOnly("Leetcode").joinToString()
          }"
      )
      println(
          "input string is race car-> reversed string is ${
              LongestSubstringWithoutDuplicate().reverseVowelsOnly("race car").joinToString()
          }"
      )
      println(
          "input string is aA -> reverse only is ${
              LongestSubstringWithoutDuplicate().reverseVowelsOnly("aA").joinToString()
          }"
      )

    println("Given array-> 1,2,3,4,5, sum is 15 ${countSubArrayWithSumK(intArrayOf(1, 2, 3, 4, 5), 15)}")
    println("Given array-> 1,2,3,4,5, sum is 1 ${countSubArrayWithSumK(intArrayOf(1, 2, 3, 4, 5), 1)}")
    println("Given array-> 1,2,3,4,5, sum is 3 ${countSubArrayWithSumK(intArrayOf(1, 2, 3, 4, 5), 3)}")
    println("Given array-> 1,2,2,2, 4,5, sum is 6 ${countSubArrayWithSumK(intArrayOf(1, 2, 2, 2, 4, 5), 6)}")
    println("Given array-> -1,-1,1, sum is -1 ${countSubArrayWithSumK(intArrayOf(-1, -1, 1), -1)}")
     */

    println("Given array-> 1,2,3,3,4,5 maxLength is ${longestEqualSubArray(intArrayOf(1,2,3,3,4,5),1)}")
}

fun subArrayWithSum(input: IntArray, k: Int): Int {
    val size = input.size
    var sum = 0
    var start = 0
    var maxLen = 0

    for (i in 0..<size) {
        sum += input[i]

        while (sum > k) {
            sum -= input[start]
            start++
        }
        val len = i - start + 1
        if (len > maxLen) {
            maxLen = len
        }

    }
    return maxLen

}


fun reverseVowelsOnly(input: String): String {
    if (input.length <= 1) return input
    val s = input.toCharArray()
    val vowels = setOf('a', 'e', 'i', 'o', 'u')

    var left = 0
    var right = s.lastIndex

    while (left < right) {
        while (left < right && s[left].lowercaseChar() !in vowels) left++
        while (left < right && s[right].lowercaseChar() !in vowels) right--
        if (left < right) {
            val tmp = s[left]
            s[left] = s[right]
            s[right] = tmp
            left++
            right--
        }
    }
    return String(s)
}

fun countSubArrayWithSumK(input: IntArray, k: Int): Int {
    val prefixSumMap = HashMap<Int, Int>()
    prefixSumMap[0] = 1
    var prefixSum = 0
    var count = 0
    for (i in input) {
        prefixSum += i
        count += prefixSumMap.getOrDefault(prefixSum - k, 0)
        prefixSumMap[prefixSum] = prefixSumMap.getOrDefault(prefixSum, 0) + 1
    }
    return count
}


fun longestEqualSubArray(input: IntArray, k: Int): Int {

    val freqMap = HashMap<Int, Int>()
    var start = 0
    var distinctCount = 0
    var maxLength = Int.MIN_VALUE

    for (end in input.indices) {
        val occurrence = freqMap.getOrDefault(input[end], 0) + 1
        if (occurrence == 1) {
            distinctCount++
        }
        while (distinctCount > k) {
            maxLength = maxOf(end - start + 1, maxLength)
            distinctCount--
            start++
        }

    }
    return maxLength

}
