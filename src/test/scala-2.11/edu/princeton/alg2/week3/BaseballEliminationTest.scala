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
      val division = new BaseballElimination(path("teams4.txt"))
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

  it should "eliminate Montreal and Philadelphia in teams4.txt" in {
    //given-when
    val division = fixture.division
    //then
    printEliminatedTeams(division)
    division.isEliminated("Montreal") shouldBe true
    division.certificateOfElimination("Montreal").toSet shouldBe Set("Atlanta")

    division.isEliminated("Philadelphia") shouldBe true
    division.certificateOfElimination("Philadelphia").toSet shouldBe Set("Atlanta", "New_York")
  }

  it should "eliminate Detroit in teams5.txt" in {
    //given-when
    val div = division("teams5.txt")
    printEliminatedTeams(div)
    //then
    div.isEliminated("Detroit") shouldBe true
    //then
    val otherTeams = Set("New_York", "Baltimore", "Boston", "Toronto")
    div.certificateOfElimination("Detroit").toSet shouldBe otherTeams

    otherTeams.foreach(t => div.isEliminated(t) shouldBe false)
    otherTeams.foreach(t => div.certificateOfElimination(t) shouldBe null)
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

  it should "eliminate Ghaddafi and Bin_Ladin in teams4a.txt" in {
    //given-when
    val div = division("teams4a.txt")
    //then
    div.isEliminated("Ghaddafi") shouldBe true
    div.isEliminated("Bin_Ladin") shouldBe true
  }

  it should "eliminate Ireland in teams7.txt" in {
    //given-when
    val div = division("teams7.txt")
    //then
    div.isEliminated("Ireland") shouldBe true
  }

  it should "eliminate Team13 in teams24.txt" in {
    //given-when
    val div = division("teams24.txt")
    //then
    printEliminatedTeams(div)
    div.isEliminated("Team13") shouldBe true
  }

  it should "eliminate Team25, Team29 in teams32.txt" in {
    //given-when
    val div = division("teams32.txt")
    //then
    div.isEliminated("Team25") shouldBe true
    div.isEliminated("Team29") shouldBe true
  }

  it should "eliminate Team21 in teams36.txt" in {
    //given-when
    val div = division("teams36.txt")
    //then
    printEliminatedTeams(div)
    div.isEliminated("Team21") shouldBe true
  }

  it should "eliminate Team6, Team15, Team25 in teams42.txt" in {
    //given-when
    val div = division("teams42.txt")
    //then
    div.isEliminated("Team6") shouldBe true
    div.isEliminated("Team15") shouldBe true
    div.isEliminated("Team25") shouldBe true
  }

  it should "eliminate Team6, Team23, Team47 in teams48.txt" in {
    //given-when
    val div = division("teams48.txt")
    //then
    div.isEliminated("Team6") shouldBe true
    div.isEliminated("Team23") shouldBe true
    div.isEliminated("Team47") shouldBe true
  }

  it should "eliminate Team3, Team29, Team37, Team50 in teams54.txt" in {
    //given-when
    val div = division("teams54.txt")
    //then
    div.isEliminated("Team3") shouldBe true
    div.isEliminated("Team29") shouldBe true
    div.isEliminated("Team37") shouldBe true
    div.isEliminated("Team50") shouldBe true
  }

  it should "not eliminate Indiana in teams10.txt" in {
    //given-when
    val div = division("teams10.txt")
    //then
    printEliminatedTeams(div)
    div.isEliminated("Indiana") shouldBe false
  }

  it should "not eliminate Miami in teams29.txt" in {
    //given-when
    val div = division("teams29.txt")
    //then
    printEliminatedTeams(div)
    div.isEliminated("Miami") shouldBe false
  }

  it should "eliminate Team18 in teams30.txt" in {
    //given-when
    val div = division("teams30.txt")
    //then
    printEliminatedTeams(div)
    div.isEliminated("Team18") shouldBe true
  }

  def printEliminatedTeams(division: BaseballElimination) {
    division.teams.foreach {
      case t if division.isEliminated(t) =>
        print(s"$t is eliminated by the subset R = { ")
        division.certificateOfElimination(t).foreach(et => print(s"$et "))
        println("}")
      case t => //println(s"$t is not eliminated")
    }
  }

  def division(file: String) = new BaseballElimination(path(file))

  def path(file: String) = s"baseball/$file"
}
