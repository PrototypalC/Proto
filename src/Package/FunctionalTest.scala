package Package
// ExecutionContext is like a thread pool/executor. Promise is for callback on future value.

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ ExecutionContext, Promise }
import scala.concurrent._
// Success/Failure for futures
import scala.util.{Try, Success, Failure}


/**
 * Created by john on 10/24/15.
 */
object FunctionalTest {
    // 1. not Currying - it just returns a anonymous function with predefined body/algorithm
    def add1(a:Int) = { b:Int => a + b } // use anonymous function, that expect b:Int as param and use "a" from parent-function
    // 1.1


    // 2. Currying - it does not return a function - it returns an Int result.
    // It expects 2nd parameter to be passed,
    // but if that is not provided - it returns a function that expects this parameter
    def add2(a:Int)(b:Int) = {a + b}

    // Composition means making 1 function out of 2 functions.
    def compose(func1: (String) => String, func2: (String) => String)(str: String): String = {
        val str1 = func1(str)
        val str2 = func2(str1)
        return str2
    }

    /**
     * "Option" should be used instead of null check.
     * If you leave out a "None" case, the method does nothing rather than exploding.
     */
    def printSomething(something: Option[String]) = {
        something match {
            case Some(maybe) => println(something.get)
            case None => println("Fuck it.")
        }
    }
    // Same thing as above, but more consice.
    def printSomething2(something: Option[String]) = {
        println(something.getOrElse("Fuck it."))
    }

    /**
     * I don't know how to use futures.
     */
    /*
    def doSomethingInFuture(): Future[String] = {
        return new Future[String] {
            override def isCompleted: Boolean = ???

            override def onComplete[U](f: (Try[String]) => U)(implicit executor: ExecutionContext): Unit = ???

            override def value: Option[Try[String]] = ???

            @throws[Exception](classOf[Exception])
            override def result(atMost: Duration)(implicit permit: CanAwait): String = ???

            @throws[InterruptedException](classOf[InterruptedException])
            @throws[TimeoutException](classOf[TimeoutException])
            override def ready(atMost: Duration)(implicit permit: CanAwait): this.type = ???
        }
    }

     // Apparently you can do something when the future returns a value (callback)
    def getAndUseFuture() = {
        val myFuture = doSomethingInFuture()
        myFuture onComplete {
            case Success(posts) => for (post <- posts) println(post)
            case Failure(t) => println("An error has occured: " + t.getMessage)
        }
    }
    */

    // Trait is the same as a Java 8 interface.
    sealed trait Z { def minus: String }
    // case class is 100% immutable and can only be modified via copy.
    sealed abstract class A(val number: Int) extends Z { def minus = "a" }
    // sealed is a bad keyword meaning "compiler, assume all super/subclasses are in same file."
    sealed case class B(val number: Double) extends Z { def minus = "b" }
    // sealed allows compiler to make assumptions/inferences about possible subclasses.
    sealed case class C(override val number: Int = 6) extends A(number) {
        override def minus = "override-C"
    }

    class D(val otherNumber: Int = 7) extends C() {
        override def minus = "override-D"
    }

    case class Parent(c:C = new C()) {
        def coolio() = println("Monkey Patching number: " + c.number)
    }

    /**
     * This monkey patches C into being type Parent.
     * Instances of C now have method "coolio"
     */
    implicit def CtoParent(c: C) = new Parent(c)

    implicit def ParentToC(parent: Parent) = parent.c

    /* // case to case inheritance is prohibited.
    case class DD() extends D() {
        override def minus = "override-DD"
    }*/

    trait Monoid[T] {
        def baseCase: T
        def append(a: T, b: T): T
    }
    // ^ This is a monoid representing things that are addable & associative w/ base case.^^

    // Subclass the monoid ("implicit" means using the adapter pattern.)
    implicit object IntMonoid extends Monoid[Int] {
        def baseCase = 0
        def append(a: Int, b: Int) = a + b
    }
    /* // This produces ambiguity because it has the same type as IntMonoid
    implicit object MultiplyMonoid extends Monoid[Int] {
        def baseCase = 1
        def append(a: Int, b: Int) = a * b
    }*/

    // String are also addable...
    implicit object StringMonoid extends Monoid[String] {
        def baseCase = ""
        def append(a: String, b: String) = a + b
    }

    // Everything that goes in this function is adapted to interface "Addable"
    // Each element in the list is adapted to the Addable[T] interface.
    def sum[T](listOfAdaptables: List[T])(implicit addable: Monoid[T]) =
        listOfAdaptables.foldLeft(addable.baseCase)(addable.append)

    def main(args: Array[String]) {

        val list123 = List(1,2,3) // Note for performance reasons this should be mutable ListBuffer.
        val sum123 = sum(list123)
        println("Sum of elements 1,2,3 is: " + sum123) // "6"
        val listXYZ = List("X", "Y", "Z")
        val sumXYZ = sum(listXYZ)
        println("Sum of elements X,Y,Z is: " + sumXYZ) // "XYZ"

        val pp = new Parent()
        println("Parent gets from delegate C: " + pp.number)

        //val immutableA = new A(5) // Case classes have no inheritance.
        val immutableB = new B(2.5)
       // println("A minus: " + immutableA.minus)
        println("B minus: " + immutableB.minus)
        println("B val: " + immutableB.number)
        val immutableC = new C(7)
        println("C minus: " + immutableC.minus)
        immutableC.coolio() // This method was monkey patched.
        println("C val: " + immutableC.number)
        val bCopy = immutableB.copy(number = 77.6)
        println("B copy: " + bCopy.number + "\n")
        val dd = new D()
        println("D number/type: " + dd.number + " " + dd.getClass.getCanonicalName)
        val cc = dd.copy(number = 55)
        println("D copied to C number/type: " + cc.number + " " + cc.getClass.getCanonicalName)

        def printTerm(term: Z) {
            term match {
                case B(number: Double) =>
                    println("Hello B: " + number)
                case C(number: Int) =>
                    println("Hello C: " + number)
            }
        }
        printTerm(immutableC) // 6
        printTerm(bCopy) // 77.6

        println("Added 5 and 6 to get: " + add1(5)(6))
        println("Added 5 and 6 to get: " + add2(5)(6))
        val halfAddition = add1(5)
        val result = halfAddition(6)
        println("Added 5 and 6 to get: " + result)

        val toUpperCase = (str: String) => {str.toUpperCase}
        val toReverse = (str: String) => {str.reverse}
        val toUpperCaseAndReverse = compose(toUpperCase, toReverse)("hello")
        println("\"hello\" to upper case and reverse is: " + toUpperCaseAndReverse)

        printSomething(Option("hi"))
        printSomething(None)
        printSomething2(Option("lalala"))
        printSomething2(None)
    }
}
