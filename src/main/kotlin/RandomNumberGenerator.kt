import java.io.File
import java.math.BigInteger

class RandomNumberGenerator {

    fun generateRandomNumber(): BigInteger {
        return BigInteger(64, java.util.Random())
    }

    fun write2000RandomNumbers() {
        var file = File("numbers.txt")
        file.printWriter().use {
            for (_i in 0 until 100)
                it.println(generateRandomNumber())
        }
    }
}
