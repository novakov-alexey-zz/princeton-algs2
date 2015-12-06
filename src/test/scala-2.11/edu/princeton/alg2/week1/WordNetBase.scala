package edu.princeton.alg2.week1

/**
  * @author Alexey Novakov
  */
trait WordNetBase {
  val wordNet = new WordNet("wordnet/synsets.txt", "wordnet/hypernyms.txt")
}
