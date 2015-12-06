package edu.princeton.alg2.week1

import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Alexey Novakov
  */
class OutcastTest extends FlatSpec with Matchers with WordNetBase {
  behavior of "Outcast"

  it should "return an outcast based on given array of WordNet nouns" in {
    //given
    val outcast = new Outcast(wordNet)
    //when - then
    outcast.outcast(Array("horse", "zebra", "cat", "bear", "table")) should be("table")
    //when - then
    outcast.outcast(Array("water", "soda", "bed", "orange_juice", "milk", "apple_juice", "tea", "coffee")) should be("bed")
    //when - then
    outcast.outcast(Array("apple", "pear", "peach", "banana", "lime", "lemon", "blueberry", "strawberry",
      "mango", "watermelon", "potato")) should be("potato")
  }
}
