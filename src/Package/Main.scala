package Package

import JavaPackage.Print

/**
 * Created by john on 10/24/15.
 */
object Main {
  /**
   * IntelliJ configuration - must go to File > Project Structures >
   * Set Project Compiler Output to: /home/john/IdeaProjects/Proto/out
   * Set Modules > Sources to set only the src directory as a source directory.
   * Right click > Run - should work after applying the above two changes.
   */
  def main(args: Array[String]) {
    print("Hello World")

    // Set the current thread name to "Main_Thread" for printout.
    Thread.currentThread().setName("Main_Thread")

    // Print even the most insignificant error messages.
    Print.myOutputSignificanceLevelSetter(Print.Significance.INSIGNIFICANT)

    // Print everything to only the error stream
    Print.myDefaultPrintStreamSetter(Print.DefaultPrintStream.ONLY_STANDARD_ERROR)

    Print.good("Printing successful.")
    Print.slightlyGood("This message is of slight significance. Only one \"+\" sign.")
    Print.veryBad("Program terminating.")
  }
}
