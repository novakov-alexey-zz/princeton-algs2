package edu.princeton.alg2.week3;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexey Novakov
 */
public class BaseballElimination {
    private Map<String, Team> teams;
    private Team[] teamId;
    private int maxDivWins = Integer.MIN_VALUE;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int n = in.readInt();
        teams = new HashMap<>();
        teamId = new Team[n];

        for (int i = 0; !in.isEmpty() && i < n; i++) {
            Team team = new Team();
            String name = in.readString();
            teams.put(name, team);
            teamId[i] = team;

            team.id = i;
            team.name = name;
            team.w = in.readInt();
            team.l = in.readInt();
            team.r = in.readInt();
            team.g = new int[n];
            for (int j = 0; j < n; j++) {
                team.g[j] = in.readInt();
            }
            if (team.w > maxDivWins) {
                maxDivWins = team.w;
            }
        }

        trivialElimination();
        nontrivialElimination();
    }

    private void trivialElimination() {
        for (Team team : teams.values()) {
            Set<String> certificate = new HashSet<>();

            teams.keySet().stream()
                    .filter(aTeam -> !aTeam.equals(team.name))
                    .filter(aTeam -> teams.get(aTeam).w > team.w + team.r)
                    .forEach(certificate::add);

            if (!certificate.isEmpty())
                team.eliminationCertificate = certificate;
        }
    }

    private void nontrivialElimination() {
        for (Team team : teams.values()) {
            if (team.eliminationCertificate != null && team.eliminationCertificate.size() > 0) continue;

            int source = numberOfTeams();
            int sink = numberOfTeams() + 1;
            int gameNode = numberOfTeams() + 2;
            int currentMaxWins = team.w + team.r;
            Set<FlowEdge> edges = new HashSet<>();

            for (int i = 0; i < numberOfTeams(); i++) {
                if (i == team.id || teamId[i].w + teamId[i].r < maxDivWins) continue;

                for (int j = 0; j < i; j++) {
                    if (j == team.id || teamId[i].g[j] == 0 || teamId[j].w + teamId[j].r < maxDivWins) continue;

                    edges.add(new FlowEdge(source, gameNode, teamId[i].g[j]));
                    edges.add(new FlowEdge(gameNode, i, Double.POSITIVE_INFINITY));
                    edges.add(new FlowEdge(gameNode, j, Double.POSITIVE_INFINITY));
                    gameNode++;
                }
                edges.add(new FlowEdge(i, sink, currentMaxWins - teamId[i].w));
            }

            FlowNetwork network = new FlowNetwork(gameNode);
            edges.forEach(network::addEdge);
            FordFulkerson ff = new FordFulkerson(network, source, sink);

            Set<String> certificate = new HashSet<>();
            for (FlowEdge edge : network.adj(numberOfTeams())) {
                if (edge.flow() < edge.capacity()) {
                    Arrays.stream(teamId)
                            .filter(v -> ff.inCut(v.id))
                            .forEach(v -> certificate.add(v.name));
                }
            }

            if (certificate.size() > 0) {
                team.eliminationCertificate = certificate;
            }
        }
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
        validateTeam(team);
        return teams.get(team).w;
    }

    private void validateTeam(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException("Team does not exist in the input file");
    }

    // number of losses for given team
    public int losses(String team) {
        validateTeam(team);
        return teams.get(team).l;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validateTeam(team);
        return teams.get(team).r;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return teams.get(team1).g[teams.get(team2).id];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateTeam(team);
        return teams.get(team).eliminationCertificate != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        return teams.get(team).eliminationCertificate;
    }

    private static class Team {
        private String name;
        private int id;
        private int w;
        private int l;
        private int r;
        private int[] g;
        private Set<String> eliminationCertificate;
    }
}