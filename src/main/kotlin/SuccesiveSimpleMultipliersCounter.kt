import java.io.File
import java.math.BigInteger

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
}
