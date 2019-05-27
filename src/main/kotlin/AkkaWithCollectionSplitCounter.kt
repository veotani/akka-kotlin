import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import java.io.File
import java.math.BigInteger

data class Numbers(val values: List<BigInteger>)
data class CounterResult(val result: BigInteger)

class MasterSeparateActor : AbstractActor() {
    private val NUM_OF_WORKERS = 12
    private var finishedWorkers = 0
    private val workers = Array<ActorRef>(NUM_OF_WORKERS) {
        context.system.actorOf(Props.create(CounterSeparateActor::class.java))
    }
    private val numbers = File("numbers.txt").readLines().map { line -> BigInteger(line) }
    private var totalMultipliers = BigInteger.ZERO
    private var timeStarted = 0L

    override fun createReceive() = ReceiveBuilder()
        .match(String::class.java) {
            var chunkSize = numbers.size / NUM_OF_WORKERS
            timeStarted = System.currentTimeMillis()
            for (i in 0 .. NUM_OF_WORKERS - 2)
                workers[i].tell(
                    Numbers(
                        numbers.slice(chunkSize * i until chunkSize * (i+1))
                    ),
                    self
                )

            // Send the rest
            workers[NUM_OF_WORKERS - 1]
                .tell(
                    Numbers(
                        numbers.slice(chunkSize * (NUM_OF_WORKERS - 1) until numbers.size)),
                    self
                )
        }

        .match(CounterResult::class.java) {cr ->
            totalMultipliers += cr.result
            finishedWorkers++
            if (finishedWorkers == NUM_OF_WORKERS) {
                val totalTime = (System.currentTimeMillis() - timeStarted)/1000
                var reportFile = File("akka_mul.txt")
                reportFile.writeText("Time taken: $totalTime\nMultipliers found: $totalMultipliers")
                context.system.terminate()
            }
        }

        .build()
}

class CounterSeparateActor : AbstractActor() {
    private val counter = SuccesiveSimpleMultipliersCounter()

    override fun createReceive() = ReceiveBuilder()
        .match(Numbers::class.java) {numbers ->
            var multipliersCount = BigInteger.ZERO
            var values = numbers.values
            for (number in values)
                multipliersCount += counter.countSimpleMultipliers(number)
            sender.tell(CounterResult(multipliersCount), self)
        }

        .matchAny { println("Error at ${self.path()}") }

        .build()
}

class AkkaWithCollectionSplitCounter {
    fun main() {
        // Create actor system
        val actorSystem = ActorSystem.create("test-system")
        // Create master actor
        val actorRef = actorSystem.actorOf(Props.create(MasterSeparateActor::class.java))
        // Send him some random message so he starts
        actorRef.tell("", actorRef)
    }
}


