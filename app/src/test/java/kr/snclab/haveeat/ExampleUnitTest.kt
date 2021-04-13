package kr.snclab.haveeat

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        print(solution(intArrayOf(-1,-3)))

    }

    fun solution(A: IntArray): Int {
        return A.filter { it>0 }.takeIf { it.isNotEmpty() }?.let {
            for(i in 1..it.last()) {
                if(!it.contains(i)) {
                    return i
                }
            }
            return it.last() + 1
        }?:1
    }

    fun solution2(A: IntArray): Int {
        for(i in 1..A.size) {
            if(!A.contains(i)) {
                return i
            }
        }
        return A.size + 1
    }
}
