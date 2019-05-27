import java.io.File
import java.math.BigInteger
import kotlin.system.measureNanoTime

class ConcurrencyPrimitivesMultipliersCounter {

    private val numberFileName = "numbers.txt"
    private var valuesTaken = 0
    private var numbersList = File(numberFileName).readLines().map { it.toBigInteger() }
    private var result = BigInteger.ZERO

    @Synchronized private fun getNumberFromList(): BigInteger {
        this.valuesTaken++
        return this.numbersList[valuesTaken-1]
    }

    @Synchronized private fun increaseResult(value: BigInteger) {
        this.result += value
    }

    fun processNumbers(): BigInteger {
        var succesiveCounter = SuccesiveSimpleMultipliersCounter()
        var threadList = List<Thread>(12) {
            Thread(Runnable {
                println("Starting thread with ID:  ${Thread.currentThread().id}")
                var threadResult = BigInteger.ZERO
                while (this.valuesTaken < this.numbersList.size) {
                    val number = this.getNumberFromList()
                    threadResult += succesiveCounter.countSimpleMultipliers(number)
                }
                this.increaseResult(threadResult)
            })
        }

        for (thread in threadList)
            thread.start()

        for (thread in threadList)
            thread.join()

        return result
    }

    fun main() {
        var concurrentMultipliersCount = BigInteger.ZERO
        var counter = ConcurrencyPrimitivesMultipliersCounter()
        var elapsedTime = measureNanoTime {
            concurrentMultipliersCount = counter.processNumbers()
        }

        elapsedTime /= with(1e9) { toInt() }
        var reportFile = File("primitives_mul.txt")
        reportFile.writeText("Time taken: $elapsedTime\nMultipliers found: $concurrentMultipliersCount")
    }
}
