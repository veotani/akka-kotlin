import java.io.File
import java.math.BigInteger
import kotlin.system.measureNanoTime

fun main(args: Array<String>) {
    // Нужно сгенерировать файл, содержащий 2000 128-битных случайных целых чисел, каждое число на отдельной строке.
    // прим.: генерировал 100 64-битных чисел
    if (!File("numbers.txt").exists())
        println("Generating numbers")
        RandomNumberGenerator().writeAllRandomNumbers()

    // простым последовательным алгоритмом
    println("Succesive approach")
    SuccesiveSimpleMultipliersCounter().main()

    // многопоточно, с использованием примитивов синхронизации
    println("Concurrency primitives approach")
    ConcurrencyPrimitivesMultipliersCounter().main()

    // с помощью Akka
    println("Akka approach")
    AkkaWithCollectionSplitCounter().main()

    // c помощью RxJava (или аналога)
    println("RxJava approach")
    RxJavaCounter().main()

}
