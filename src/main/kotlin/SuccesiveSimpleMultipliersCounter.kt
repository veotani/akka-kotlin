import java.io.File
import java.math.BigInteger
import kotlin.system.measureNanoTime

class SuccesiveSimpleMultipliersCounter {

    var numberFileName = "numbers.txt"

    fun countSimpleMultipliers(number: BigInteger): BigInteger {
        var count = BigInteger.ZERO
        var currentNumber = number

        var i = BigInteger.TWO

        var currentNumberSqrt = currentNumber.sqrt()
        while (i <= currentNumberSqrt) {

            if (currentNumber.mod(i) == BigInteger.ZERO) {
                count++
                currentNumber = currentNumber.div(i)
                currentNumberSqrt = currentNumber.sqrt()
            }
            else
                if (i == BigInteger.TWO)
                    i ++
                else
                    i += BigInteger.TWO
        }
        return count
    }

    fun processNumbers(): BigInteger {
        var file = File(numberFileName)
        var lines = file.readLines()
        var countSimpleMultipliers = BigInteger.ZERO
        for (line in lines)
            countSimpleMultipliers += countSimpleMultipliers(BigInteger(line))
        return countSimpleMultipliers
    }

    fun main() {
        var simpleMultipliersCount = BigInteger.ZERO
        var elapsedTime = measureNanoTime {
            simpleMultipliersCount = SuccesiveSimpleMultipliersCounter().processNumbers()
        }

        elapsedTime /= with(1e9) { toInt() }
        var reportFile = File("simple_mul.txt")
        reportFile.writeText("Time taken: $elapsedTime\nMultipliers found: $simpleMultipliersCount")
    }
}
