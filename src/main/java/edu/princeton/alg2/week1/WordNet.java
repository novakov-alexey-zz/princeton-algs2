package edu.princeton.alg2.week1;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexey Novakov
 */
public class WordNet {
    private static final String RELATION_SEPARATOR = ",";
    private static final String SYNONYM_SEPARATOR = " ";

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

            int id = Integer.parseInt(items[0]);
            String synSet = items[1];
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
            int synsetId = Integer.parseInt(items[0]);

            for (int i = 1; i < items.length; i++) {
                digraph.addEdge(synsetId, Integer.parseInt(items[i]));
            }
        }
        in.close();

        return digraph;
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
