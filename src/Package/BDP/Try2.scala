package Package.BDP

import Package.BDP.Behaviors.Moveable

/**
 * Created by johnmichaelreed2 on 10/26/15.
 */
object Try2 {

    /**
     * This immutable class represents the kingdom Animalia (Animals). Animals always have
     * one or more cells, the ability to eat food (rather than photosynthesize it), cell nucei,
     * and no cell walls. They can also optionally have feathers or solid bones.
     */
    abstract class Animal(val hasFeathers: Boolean = false,
     val hasSolidBones: Boolean = false) {

        val isMulticellular = true
        val hasCellNuclei = true
        val hasCellWalls = false
        private val moving = new Behaviors.Moving()
        def move = moving
        def eatFood = Behaviors.eating
    }

    class MovingThing(val hasFeathers: Boolean = false,
    val hasSolidBones: Boolean = false) extends Moveable {
        val isMulticellular = true
        val hasCellNuclei = true
        val hasCellWalls = false
    }

    /**
     * Birds are animals with feathers and strutted (not solid) bones.
     * BirdBehavior takes precedence over AnimalBehavior.
     */
    abstract class Bird() extends Animal(true, false) {
        val hasBeak = true
        val laysEggs = true
        def flap = Behaviors.flapping
        def layEggs = Behaviors.eggLaying
    }

    /**
     * Mammals are animals without feathers and with solid bones.
     */
    abstract class Mammal extends Animal(false, true) {
        def growHair = Behaviors.hairGrowing
        private val lactating = new Behaviors.Lactating(milkRemaining = 5)
        def lactate = lactating
    }

    /**
     * I thought that this was a bird because it has a beak and lays eggs.
     */
    class DuckBilledPlatapus() extends Animal(false, true) {
        private val fastMoving = new Behaviors.Moving() {
            override def apply() = {
                setPos(getPos() + getSpeed()*2)
                println("Position: " + getPos() + ", Half-Speed: " + getSpeed())
            }
        }
        override def move = fastMoving
        def layEggs = Behaviors.eggLaying
        def growHair = Behaviors.hairGrowing

        private val lactating = new Behaviors.Lactating(milkRemaining = 5)
        def lactate: Behaviors.Lactating = {
            lactating.milkRemaining -= 1
            return lactating
        }
    }

    def makeMove(a: Animal): Unit = {
        a.move()
    }

    def main(args: Array[String]) {

        val perry = new DuckBilledPlatapus()

        val motion = perry.move // we can capture the concept of motion.
        motion.getPos() // This get's Perry's position.

        // We can also copy it and give the copy to someone else...
        val motion2 = motion.copy(pos = 500)
        val motion3 = motion.copy(speed = 7)

        println("\nPerry's position is: " + perry.move.getPos())
        perry.move()

        println("\nPerry's position is: " + perry.move.getPos())
        perry.move()

        // Get Perry's speed? No...
        perry.move.getSpeed() // Get the speed of Perry's motion

        println("\nAfter speedup, Perry's position is: " + perry.move.getPos())
        perry.move()

        println()
        makeMove(perry)

        println("Perry moved via over-riding\n")

        perry.eatFood()

        println("\nMilk remaining: " + perry.lactate.milkRemaining)

        perry.lactate()
        perry.lactate()
        perry.lactate()
        perry.lactate()
    }
}
