package edu.princeton.alg2.week3;

import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexey Novakov
 */
public class BaseballElimination {
    private Map<String, Team> teams;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);

        int n = in.readInt();
        teams = new HashMap<>();

        for (int i = 0; !in.isEmpty() && i < n; i++) {
            Team team = new Team();
            teams.put(in.readString(), team);

            team.id = i;
            team.w = in.readInt();
            team.l = in.readInt();
            team.r = in.readInt();
            team.g = new int[n];
            for (int j = 0; j < n; j++) {
                team.g[j] = in.readInt();
            }
        }

        elimination();
    }

    private void elimination() {
        //Trivial elimination
        for (Team team : teams.values()) {
            teams.keySet().stream()
                    .filter(anotherTeam -> teams.get(anotherTeam).w >= team.w + team.r)
                    .findFirst()
                    .ifPresent(anotherTeam -> {
                        team.eliminated = true;
                        team.eliminationCertificate.add(anotherTeam);
                    });
        }

        //Nontrivial elimination
        //TODO
    }

    // number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        return teams.get(team).w;
    }

    // number of losses for given team
    public int losses(String team) {
        return teams.get(team).l;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return teams.get(team).r;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return teams.get(team1).g[teams.get(team2).id];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return teams.get(team).eliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        return teams.get(team).eliminationCertificate;
    }

    private static class Team {
        private int id;
        private int w;
        private int l;
        private int r;
        private int[] g;
        private boolean eliminated;
        private List<String> eliminationCertificate = new ArrayList<>();
    }
}
