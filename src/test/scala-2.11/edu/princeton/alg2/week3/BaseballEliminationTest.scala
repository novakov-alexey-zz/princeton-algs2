package edu.princeton.alg2.week3

import edu.princeton.cs.algs4.StdOut
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Alexey Novakov
  */
class BaseballEliminationTest extends FlatSpec with Matchers {
  behavior of "BaseballElimination"

  it should "load data from file" in {
    //given
    val fileName = "teams4.txt"
    //when
    val division = new BaseballElimination(s"baseball/$fileName")
    //then
    division.numberOfTeams shouldBe 4
    //then
    import scala.collection.JavaConversions._
    division.teams.toSet shouldBe Set("New_York", "Atlanta", "Philadelphia", "Montreal")
    //then
    division.wins("New_York") shouldBe 78
    //then
    division.losses("Montreal") shouldBe 82
    //then
    division.remaining("Philadelphia") shouldBe 3
    //then
    division.against("Atlanta", "Philadelphia") shouldBe 1
  }

  def printEliminatedTeams(fileName: String) {
    val division: BaseballElimination = new BaseballElimination(fileName)
    import scala.collection.JavaConversions._

    division.teams.foreach {
      case t if division.isEliminated(t) =>
        StdOut.print(s"$t is eliminated by the subset R = { ")
        division.certificateOfElimination(t).foreach(et => StdOut.print(s"$et "))
        StdOut.println("}")
      case t =>  StdOut.println(s"$t is not eliminated")
    }
  }
}
