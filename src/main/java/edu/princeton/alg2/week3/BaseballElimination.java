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
    private int maxWins = Integer.MIN_VALUE;

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
                team.remainingInDivision += team.g[j];
            }
            if (team.w > maxWins) {
                maxWins = team.w;
            }
        }

        trivialElimination();
        nontrivialElimination();
    }

    private void nontrivialElimination() {
        for (Team team : teams.values()) {
            if (team.eliminationCertificate != null && team.eliminationCertificate.size() > 0) continue;

            int n = numberOfTeams();
            int source = n;
            int sink = n + 1;
            int gameNode = n + 2;
            int currentMaxWins = team.w + team.remainingInDivision;
            Set<FlowEdge> edges = new HashSet<>();
            for (int i = 0; i < n; i++) {
                if (i == team.id || teamId[i].w + teamId[i].remainingInDivision < maxWins) {
                    continue;
                }

                for (int j = 0; j < i; j++) {
                    if (j == team.id || teamId[i].g[j] == 0 || teamId[j].w + teamId[j].remainingInDivision < maxWins) {
                        continue;
                    }

                    edges.add(new FlowEdge(source, gameNode, teamId[i].g[j]));
                    edges.add(new FlowEdge(gameNode, i, Double.POSITIVE_INFINITY));
                    edges.add(new FlowEdge(gameNode, j, Double.POSITIVE_INFINITY));
                    gameNode++;
                }
                edges.add(new FlowEdge(i, sink, currentMaxWins - teamId[i].w));
            }

            FlowNetwork network = new FlowNetwork(gameNode);
            for (FlowEdge edge : edges) {
                network.addEdge(edge);
            }
            FordFulkerson ff = new FordFulkerson(network, source, sink);

            Set<String> certificate = new HashSet<>();
            for (FlowEdge edge : network.adj(0)) {
                if (edge.flow() < edge.capacity()) {
                    Arrays.stream(teamId)
                            .filter(v -> v.id != team.id)
                            .filter(v -> ff.inCut(v.id))
                            .forEach(v -> certificate.add(v.name));
                }
            }

            if (certificate.size() > 1) {
                int totalWin = certificate.stream().mapToInt(v -> teams.get(v).w).reduce(0, (a, b) -> a + b);

                int totalRemaining = 0;
                for (String certTeam : certificate) {
                    for (String anotherCertTeam : certificate) {
                        totalRemaining += teams.get(certTeam).g[teams.get(anotherCertTeam).id];
                    }
                }

                double a = (double) (totalWin + totalRemaining / 2) / certificate.size();
                if (a >= team.w + team.remainingInDivision)
                    team.eliminationCertificate = certificate;

                System.out.printf(
                        "Team: %s, totalWin = %d, totalRemaining = %d, team.w = %d, certificate = %s, a = %.2f, maxWins = %d%n",
                        team.name, totalWin, totalRemaining, team.w, certificate, a, team.w + team.remainingInDivision);
            }
        }
    }

    private void trivialElimination() {
        for (Team team : teams.values()) {
            Set<String> certificate = new HashSet<>();

            teams.keySet().stream()
                    .filter(aTeam -> !aTeam.equals(team.name))
                    .filter(aTeam -> teams.get(aTeam).w > team.w + team.remainingInDivision)
                    .forEach(certificate::add);

            if (!certificate.isEmpty())
                team.eliminationCertificate = certificate;
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
        private int remainingInDivision;
        private int[] g;
        private Set<String> eliminationCertificate;
    }
}