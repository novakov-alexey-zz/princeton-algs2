package edu.princeton.alg2.week1;

/**
 * @author Alexey Novakov
 */
public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String outcast = "";
        int maxDistance = 0;
        for (String noun : nouns) {
            int distance = 0;
            for (String a : nouns) {
                distance += wordnet.distance(noun, a);
            }

            if (distance > maxDistance) {
                maxDistance = distance;
                outcast = noun;
            }
        }
        return outcast;
    }
}
