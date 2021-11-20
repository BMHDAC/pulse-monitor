package pl.pw.mierzopuls.alg

import java.lang.IllegalStateException

enum class PixelColor(val idx: Int) {
    RED(0),
    GREEN(1),
    BLUE(2);

    companion object {
        fun getPixelColorFromIndex(idx: Int): PixelColor {
            return when(idx) {
                0 -> RED
                1 -> GREEN
                2 -> BLUE
                else -> throw IllegalStateException("Index $idx is out of bound")
            }
        }
    }
}