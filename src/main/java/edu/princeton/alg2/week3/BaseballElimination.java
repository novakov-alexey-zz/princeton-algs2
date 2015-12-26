package edu.princeton.alg2.week3;

import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Novakov
 */
public class BaseballElimination {
    private int[] w;
    private int[] l;
    private int[] r;
    private int[][] g;
    private Map<String, Integer> teamId;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);

        int n = in.readInt();
        createStorage(n);

        for (int i = 0; !in.isEmpty() && i < n; i++) {
            teamId.put(in.readString(), i);
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();

            for (int j = 0; j < n; j++) {
                g[i][j] = in.readInt();
            }
        }
    }

    private void createStorage(int n) {
        w = new int[n];
        l = new int[n];
        r = new int[n];
        g = new int[n][n];
        teamId = new HashMap<>();
    }

    // number of teams
    public int numberOfTeams() {
        return teamId.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teamId.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        return w[teamId.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return l[teamId.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return r[teamId.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return g[teamId.get(team1)][teamId.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        return teamId.keySet();
    }
}
