package edu.princeton.alg2.week3

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConversions._

/**
  * @author Alexey Novakov
  */
class BaseballEliminationTest extends FlatSpec with Matchers {
  behavior of "BaseballElimination"

  def fixture = {
    new {
      val fileName = "teams4.txt"
      val division = new BaseballElimination(path(fileName))
    }
  }

  it should "load data from file" in {
    //when
    val division = fixture.division
    //then
    division.numberOfTeams shouldBe 4
    //then
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

  it should "return eliminated" in {
    fixture.division.isEliminated("Montreal") shouldBe true
  }

  it should "eliminate Montreal and Philadelphia for teams4.txt" in {
    //given-when
    val division = fixture.division
    //then
    printEliminatedTeams(division)
    division.isEliminated("Montreal") shouldBe true
    division.certificateOfElimination("Montreal").toSet shouldBe Set("Atlanta")

    division.isEliminated("Philadelphia") shouldBe true
    division.certificateOfElimination("Philadelphia").toSet shouldBe Set("Atlanta", "New_York")
  }

  it should "trow exception when team is not from input file" in {
    val division = fixture.division
    intercept[IllegalArgumentException] {
      division.against("unknown team", "Atlanta")
    }
    intercept[IllegalArgumentException] {
      division.losses("unknown team")
    }
    intercept[IllegalArgumentException] {
      division.certificateOfElimination("unknown team")
    }
    intercept[IllegalArgumentException] {
      division.wins("unknown team")
    }
    intercept[IllegalArgumentException] {
      division.isEliminated("unknown team")
    }
    intercept[IllegalArgumentException] {
      division.remaining("unknown team")
    }
  }

  def printEliminatedTeams(division: BaseballElimination) {
    division.teams.foreach {
      case t if division.isEliminated(t) =>
        print(s"$t is eliminated by the subset R = { ")
        division.certificateOfElimination(t).foreach(et => print(s"$et "))
        println("}")
      case t => println(s"$t is not eliminated")
    }
  }

  def path(file: String) = s"baseball/$file"
}
