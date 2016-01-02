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
        }

        trivialElimination();
        nontrivialElimination();
    }

    private void nontrivialElimination() {
        int otherTeams = numberOfTeams() - 1;
        int gameCount = (int) ((Math.pow(otherTeams, 2) - (otherTeams)) / 2);
        int vertices = 2 + gameCount + otherTeams;
        int t = vertices - 1;

        for (Team team : teams.values()) {
            if (team.eliminationCertificate != null && team.eliminationCertificate.size() < 2) continue;
            FlowNetwork flow = new FlowNetwork(vertices);
            int game = numberOfTeams();
            boolean[] edgeToSink = new boolean[numberOfTeams()];
            int maxWins = team.w + team.remainingInDivision;

            for (int i = 0; i < numberOfTeams(); i++) {
                for (int j = 0; j < numberOfTeams(); j++) {
                    if (team.id != i && team.id != j && i < j) {
                        int gameV = game++;
                        flow.addEdge(new FlowEdge(0, gameV, teamId[i].g[j]));
                        flow.addEdge(new FlowEdge(gameV, i, Double.POSITIVE_INFINITY));
                        flow.addEdge(new FlowEdge(gameV, j, Double.POSITIVE_INFINITY));

                        if (!edgeToSink[i]) {
                            flow.addEdge(new FlowEdge(i, t, Math.max(0, maxWins - teamId[i].w)));
                            edgeToSink[i] = true;
                        }
                        if (!edgeToSink[j]) {
                            flow.addEdge(new FlowEdge(j, t, Math.max(0, maxWins - teamId[j].w)));
                            edgeToSink[j] = true;
                        }
                    }
                }
            }

            FordFulkerson ff = new FordFulkerson(flow, 0, t);
            Set<String> certificate = new HashSet<>();

            Arrays.stream(teamId)
                    .filter(v -> v.id != team.id)
                    .filter(v -> ff.inCut(v.id))
                    .forEach(v -> certificate.add(v.name));

            int totalWin = certificate.stream().mapToInt(v -> teams.get(v).w).reduce(0, (a, b) -> a + b);

            int totalRemaining = 0;
            for (String certTeam : certificate) {
                for (String anotherCertTeam : certificate) {
                    totalRemaining += teams.get(certTeam).g[teams.get(anotherCertTeam).id];
                }
            }

            double value = ff.value();
            if ( certificate.size() > 1) {
                double a =  (double)(totalWin + totalRemaining / 2) / certificate.size();
                if (Math.abs(a - maxWins) > 1E-6)
                    team.eliminationCertificate = certificate;
            }

//            System.out.print("Team: " + team.name + ", value = " + value);
//            System.out.println(", totalWin = " + totalWin + ", totalRemaining = " + totalRemaining
//                    + ", team.w = " + team.w + ", capacity = " + capacity + ", certificate = " + certificate);
//            System.out.println(Math.abs(value - capacity) < 1E-6);
        }
    }

    private void trivialElimination() {
        for (Team team : teams.values()) {
            Set<String> certificate = new HashSet<>();

            teams.keySet().stream()
                    .filter(aTeam -> !aTeam.equals(team.name))
                    .forEach(anotherTeam -> {
                        if (teams.get(anotherTeam).w > team.w + team.remainingInDivision)
                            certificate.add(anotherTeam);
                    });


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
