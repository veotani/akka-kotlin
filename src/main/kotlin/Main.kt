import java.io.File
import java.math.BigInteger
import kotlin.system.measureNanoTime

fun main(args: Array<String>) {
    if (!File("numbers.txt").exists())
        RandomNumberGenerator().write2000RandomNumbers()
    var simpleMultipliersCount = BigInteger.ZERO
    var elapsedTime = measureNanoTime {
        simpleMultipliersCount = SuccesiveSimpleMultipliersCounter().processNumbers()
    }

    elapsedTime /= with(1e9) { toInt() }
    var reportFile = File("simple_mul.txt")
    reportFile.writeText("Time taken: $elapsedTime\nMultipliers found: $simpleMultipliersCount")



    var concurrentMultipliersCount = BigInteger.ZERO
    var counter = ConcurrencyPrimitivesMultipliersCounter()
    elapsedTime = measureNanoTime {
        concurrentMultipliersCount = counter.processNumbers()
    }

    elapsedTime /= with(1e9) { toInt() }
    reportFile = File("primitives_mul.txt")
    reportFile.writeText("Time taken: $elapsedTime\nMultipliers found: $concurrentMultipliersCount")
}
