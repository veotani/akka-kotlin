import io.reactivex.Observable
import java.io.File
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import io.reactivex.schedulers.Schedulers
import java.util.*
import io.reactivex.Flowable
import io.reactivex.functions.Consumer

class RxJavaCounter() {
    fun main() {
        val numbers = File("numbers.txt").readLines().map { BigInteger(it) }
        val counter = SuccesiveSimpleMultipliersCounter()
        var total = BigInteger.ZERO

        var startTime = System.currentTimeMillis()

        Flowable.range(0, numbers.size)
            .parallel()
            .runOn(Schedulers.computation())
            .map { i ->
                SuccesiveSimpleMultipliersCounter().countSimpleMultipliers(numbers[i])
            }
            .sequential()
            .blockingSubscribe {
                total += it
            }

        var endTime = System.currentTimeMillis()

        var reportFile = File("rx_mul.txt")
        reportFile.writeText("Time taken: ${(endTime - startTime) / 1000}\nMultipliers found: $total")
    }
}
