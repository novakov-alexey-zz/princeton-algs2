package edu.princeton.alg2.week1;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Alexey Novakov
 */
public class SAP {
    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph digraph) {
        Objects.requireNonNull(digraph);
        this.digraph = digraph;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(Arrays.asList(v, w));
        return findDistance(new BreadthFirstDirectedPaths(digraph, v), new BreadthFirstDirectedPaths(digraph, w));
    }

    private int findDistance(BreadthFirstDirectedPaths bfsPathsV, BreadthFirstDirectedPaths bfsPathsW) {
        int minDistance = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (bfsPathsV.hasPathTo(i) && bfsPathsW.hasPathTo(i)) {
                int distance = bfsPathsV.distTo(i) + bfsPathsW.distTo(i);

                if (minDistance < 0 || distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(Arrays.asList(v, w));
        return findAncestor(new BreadthFirstDirectedPaths(digraph, v), new BreadthFirstDirectedPaths(digraph, w));
    }

    private int findAncestor(BreadthFirstDirectedPaths bfsPathsV, BreadthFirstDirectedPaths bfsPathsW) {
        int minDistance = -1;
        int ancestor = -1;

        for (int i = 0; i < digraph.V(); i++) {
            if (bfsPathsV.hasPathTo(i) && bfsPathsW.hasPathTo(i)) {
                int distance = bfsPathsV.distTo(i) + bfsPathsW.distTo(i);

                if (minDistance < 0 || distance < minDistance) {
                    minDistance = distance;
                    ancestor = i;
                }
            }
        }

        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        rejectNullArgs(v, w);
        validateVertex(v);
        validateVertex(w);

        return findDistance(new BreadthFirstDirectedPaths(digraph, v), new BreadthFirstDirectedPaths(digraph, w));
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        rejectNullArgs(v, w);
        validateVertex(v);
        validateVertex(w);

        return findAncestor(new BreadthFirstDirectedPaths(digraph, v), new BreadthFirstDirectedPaths(digraph, w));
    }

    private void validateVertex(Iterable<Integer> vertices) {
        for (int v : vertices)
            if (v < 0 || v >= digraph.V()) {
                throw new IndexOutOfBoundsException(String.format("vertex %d is not between 0 and %d", v, digraph.V() - 1));
            }
    }

    private void rejectNullArgs(Iterable<Integer> v, Iterable<Integer> w) {
        Objects.requireNonNull(v);
        Objects.requireNonNull(w);
    }
}
