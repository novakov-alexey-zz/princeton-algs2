package edu.princeton.alg2.week1

import edu.princeton.cs.algs4.{Digraph, In}
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Alexey Novakov
  */
class SAPTest extends FlatSpec with Matchers {
  val digraph = new Digraph(new In("wordnet/digraph1.txt"))

  behavior of "SAP"

  it should "a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path" in {
    //given
    implicit val sap = new SAP(digraph)
    //when-then
    verifyLengthAndAncestor(3, 11, 4, 1)
    verifyLengthAndAncestor(9, 12, 3, 5)
    verifyLengthAndAncestor(7, 2, 4, 0)
    verifyLengthAndAncestor(1, 6, -1, -1)
  }

  it should "throw IndexOutOfBoundsException if vertex v is not between 0 and DAG.V() - 1" in {
    intercept[IndexOutOfBoundsException] {
      new SAP(digraph).length(1, -9)
    }
  }

  def verifyLengthAndAncestor(v: Int, w: Int, length: Int, ancestor: Int)(implicit sap: SAP) = {
    sap.length(v, w) should be(length)
    sap.ancestor(v, w) should be(ancestor)
  }
}
