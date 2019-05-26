import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import java.io.File
import java.math.BigInteger

data class ReadyMessage(val someNumber: Int)

data class CountMultipliers(val countMultipliers: BigInteger)

class MasterActor : AbstractActor() {
    var numOfWorkers = 0
    // Workers that send ReadyMessage
    var finishedWorkers = 0
    var totalMultipliers = BigInteger.ZERO

    var startTime = Long.MIN_VALUE

    // Create slave actors
    var workers = Array<ActorRef>(numOfWorkers) { context.actorOf(Props.create(CounterActor::class.java)) }

    // When master receives message it decides what to do
    override fun createReceive() = ReceiveBuilder()
        // String indicates initialization of the system
        .match(File::class.java) {file ->
            var numbers = file.readLines().map { BigInteger(it) }
            this.numOfWorkers = numbers.size
            this.workers = Array(numOfWorkers) { context.actorOf(Props.create(CounterActor::class.java)) }
            println("Starting workers in master: ${self.path()}")
            // Send all slave actors a number so one prints it
            this.startTime = System.currentTimeMillis()
            for (i in 0 until numOfWorkers) {
                workers[i].tell(numbers[i], self)
            }
        }

        // ReadyMessage indicates that worker has finished it's job
        .match(CountMultipliers::class.java) {
            totalMultipliers += it.countMultipliers
            // Count workers that are done with their job
            finishedWorkers++
            // When all workers are done we terminate the system
            if (finishedWorkers == numOfWorkers - 1) {
                var endTime = System.currentTimeMillis()
                var totalTime = (endTime - this.startTime) / 1000

                var reportFile = File("akka_mul.txt")
                reportFile.writeText("Time taken: $totalTime\nMultipliers found: $totalMultipliers")
                context.system.terminate()
            }
        }
        .build()!!
}

// Slave actor
class CounterActor : AbstractActor() {
    var counter = SuccesiveSimpleMultipliersCounter()

    override fun createReceive() = ReceiveBuilder()
        .match(BigInteger::class.java) {
            val countMultipliers = counter.countSimpleMultipliers(it)
            // Answer master that this slave done it's job
            sender.tell(CountMultipliers(countMultipliers), self)
        }

        // Some sort of "exception handling
        .matchAny {
            println("Got something unknown at slave actor: ${self.path()}")
        }
        .build()!!
}

fun main() {
    val file = File("numbers.txt")


    // Create actor system
    val actorSystem = ActorSystem.create("test-system")
    // Create master actor
    val actorRef = actorSystem.actorOf(Props.create(MasterActor::class.java))
    // Send him some random message so he starts
    actorRef.tell(file, actorRef)
}
