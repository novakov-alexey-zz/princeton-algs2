package edu.princeton.alg2.week1;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author Alexey Novakov
 */
public class WordNet {
    private static final String RELATION_SEPARATOR = ",";
    private static final String SYNONYM_SEPARATOR = " ";
    private static final int SYNSET_ID_POS = 0;
    private static final int SYNSET_POS = 1;

    private SAP sap;
    private Map<Integer, String> idToSynsets;
    private Map<String, Bag<Integer>> nounToIds;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        Objects.requireNonNull(synsets, "Synset file is not set");
        Objects.requireNonNull(hypernyms, "Hypernyms file is not set");

        buildSynset(synsets);
        sap = new SAP(buildHypernymsDigraph(hypernyms, idToSynsets.size()));
    }

    private void buildSynset(String synsetsPath) {
        In in = new In(synsetsPath);
        idToSynsets = new HashMap<>();
        nounToIds = new HashMap<>();

        while (in.hasNextLine()) {
            String[] items = in.readLine().split(RELATION_SEPARATOR);

            int id = Integer.parseInt(items[SYNSET_ID_POS]);
            String synSet = items[SYNSET_POS];
            idToSynsets.put(id, synSet);

            for (String noun : synSet.split(SYNONYM_SEPARATOR)) {
                if (nounToIds.containsKey(noun)) {
                    nounToIds.get(noun).add(id);
                } else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(id);
                    nounToIds.put(noun, bag);
                }
            }
        }
        in.close();
    }

    private Digraph buildHypernymsDigraph(String hypernymsPath, int size) {
        In in = new In(hypernymsPath);
        Digraph digraph = new Digraph(size);

        while (in.hasNextLine()) {
            String[] items = in.readLine().split(RELATION_SEPARATOR);
            int synsetId = Integer.parseInt(items[SYNSET_ID_POS]);

            for (int i = 1; i < items.length; i++) {
                digraph.addEdge(synsetId, Integer.parseInt(items[i]));
            }
        }
        in.close();

        validateRootedDag(digraph);
        return digraph;
    }

    //check that the input is a rooted DAG
    private void validateRootedDag(Digraph digraph) {
        int roots = IntStream.range(0, digraph.V()).filter(i -> !digraph.adj(i).iterator().hasNext()).reduce(0, (a, b) -> a + 1);

        if (roots != 1) {
            throw new IllegalArgumentException();
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToIds.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        Objects.requireNonNull(word, "Word should be defined");
        return nounToIds.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        validateNouns(nounA, nounB);
        return sap.length(nounToIds.get(nounA), nounToIds.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        validateNouns(nounA, nounB);
        return idToSynsets.get(sap.ancestor(nounToIds.get(nounA), nounToIds.get(nounB)));
    }

    private void validateNouns(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Either nounA or nounB is not a noun");
        }
    }
}
