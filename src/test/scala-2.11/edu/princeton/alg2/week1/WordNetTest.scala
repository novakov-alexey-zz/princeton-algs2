package edu.princeton.alg2.week1

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

/**
  * @author Alexey Novakov
  */
class WordNetTest extends FlatSpec with Matchers with WordNetBase {
  behavior of "WordNet"

  it should "true if the word is a WordNet noun" in {
    wordNet.isNoun("Actifed") should be(true)
    wordNet.isNoun(Random.nextString(10)) should be(false)
  }

  it should "return a synset that is the common ancestor of nounA and nounB in a shortest ancestral path " in {
    wordNet.sap("1830s", "1840s") should be ("decade decennary decennium")
  }

  it should "distance between nounA and nounB" in {
    wordNet.distance("1830s", "1840s") should be (2)
  }

  it should "return all WordNet nouns" in {
    import scala.collection.JavaConverters._
    wordNet.nouns.asScala.seq shouldNot be (empty)
  }

  it should "throw IllegalArgumentException if Either nounA or nounB is not a noun)" in {
    intercept[IllegalArgumentException] {
      wordNet.distance(Random.nextString(10), Random.nextString(10))
    }
  }

  it should "throws an IllegalArgumentException when the input is not a rooted DAG" in {
    intercept[IllegalArgumentException] {
      new WordNet("wordnet/synsets3.txt", "wordnet/hypernyms3InvalidTwoRoots.txt")
    }
  }
}
