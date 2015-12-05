package edu.princeton.alg2.week1;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.*;

/**
 * @author Alexey Novakov
 */
public class WordNet {
    private static final String RELATION_SEPARATOR = ",";
    private static final String SYNONYM_SEPARATOR = " ";

    private Digraph digraph;
    private Map<Integer, String> idToSynsets;
    private Map<String, Bag<Integer>> nounToIds;

    private class Synset {
        private String[] synonyms;
        private String gloss;

        public Synset(String[] synonyms, String gloss) {
            this.synonyms = synonyms;
            this.gloss = gloss;
        }
    }

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        Objects.requireNonNull(synsets, "Synset file is not set");
        Objects.requireNonNull(hypernyms, "Hypernyms file is not set");

        buildSynsetList(synsets);
        buildHypernymsDigraph(hypernyms, idToSynsets.size());
    }

    private void buildSynsetList(String synsetsPath) {
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

    private void buildHypernymsDigraph(String hypernymsPath, int size) {
        In in = new In(hypernymsPath);
        digraph = new Digraph(size);

        while (in.hasNextLine()) {
            String[] items = in.readLine().split(RELATION_SEPARATOR);
            int synsetId = Integer.parseInt(items[0]);

            for (int i = 1; i < items.length; i++) {
                digraph.addEdge(synsetId, Integer.parseInt(items[i]));
            }
        }
        in.close();
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
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Either nounA or nounB is not a noun");
        }


        return 0;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet("wordnet/synsets.txt", "wordnet/hypernyms.txt");
        //wordNet.nouns().forEach(System.out::println);
    }
}
