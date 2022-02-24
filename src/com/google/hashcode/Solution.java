package com.google.hashcode;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws IOException {
        File dir = new File("./files/in");
        ProcessBuilder builder = new ProcessBuilder("zip", "-r", "files/source", "src");
        builder.directory(new File("."));
        builder.start();
        List<File> files = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
//                .filter(file -> file.getName().startsWith("b_"))
                .filter(file -> file.getName().endsWith(".txt"))
                .sorted(Comparator.comparingInt(f -> f.getName().charAt(0)))
                .collect(Collectors.toList());
        for (File file : files) {
            BufferedReader in = new BufferedReader(new FileReader(file));
            PrintWriter out = new PrintWriter(new FileWriter("./files/out/" + file.getName().replace(".txt", ".ans")));
            Solution sol = new Solution();
            sol.run(in, out);
            System.out.printf("Input '%s' Done!%n", file.getName());
            in.close();
            out.close();
        }
    }

    private void run(BufferedReader in, PrintWriter out) throws IOException {
        String[] tokens = in.readLine().split(" ");
        int c = Integer.parseInt(tokens[0]);
        int p = Integer.parseInt(tokens[1]);

        Map<String, List<Skill>> skills = new HashMap<>();

        List<String> contributors = new ArrayList<>();
        for (int i = 0; i < c; i++) {
            tokens = in.readLine().split(" +");
            String contributorName = tokens[0];
            int numberOfSkills = Integer.parseInt(tokens[1]);
            for (int j = 0; j < numberOfSkills; j++) {
                tokens = in.readLine().split(" ");
                skills.putIfAbsent(tokens[0], new ArrayList<>());
                skills.get(tokens[0]).add(new Skill(tokens[0], contributorName, Integer.parseInt(tokens[1])));
            }
            contributors.add(tokens[0]);
        }

        for (List<Skill> skillList : skills.values()) {
            skillList.sort(Comparator.comparingInt(skill -> skill.level));
        }

        List<Project> projectsList = new ArrayList<>();
        for (int i = 0; i < p; i++) {
            tokens = in.readLine().split(" ");
            Project project = new Project(
                    tokens[0],
                    Integer.parseInt(tokens[1]),
                    Integer.parseInt(tokens[2]),
                    Integer.parseInt(tokens[3])
            );
            int numberOfRoles = Integer.parseInt(tokens[4]);
            for (int j = 0; j < numberOfRoles; j++) {
                tokens = in.readLine().split(" ");
                project.addRole(tokens[0], Integer.parseInt(tokens[1]));
            }
            projectsList.add(project);
        }

        projectsList.sort((p1, p2) -> {
            if (p1.bestBefore == p2.bestBefore) {
                if (p1.score == p2.score) {
                    return p1.roles.stream().map(role -> role.level).reduce(0, Integer::sum) - p2.roles.stream().map(role -> role.level).reduce(0, Integer::sum);
                }

                return p2.score - p1.score;
            }

            return p1.bestBefore - p2.bestBefore;
        });
        Map<String, Project> projects = projectsList.stream().collect(Collectors.toMap(project -> project.name, project -> project));
        Set<String> todoProjects = new HashSet<>(projects.keySet());
        Set<String> inProgressProjects = new HashSet<>();
        List<String> doneProjects = new LinkedList<>();

        int day = 0;

        while (!todoProjects.isEmpty() || !inProgressProjects.isEmpty()) {
            int finalDay = day;
            todoProjects.removeIf(name -> finalDay > projects.get(name).bestBefore);
            Set<String> toBeRemoved = new HashSet<>();
            for (String name : todoProjects) {
                Project project = projects.get(name);
                Map<String, Skill> toBeReservedContributors = new HashMap<>();
                boolean canBeDone = true;

                for (ProjectRole role : project.roles) {
                    List<Skill> skillContributors = skills.get(role.name);
                    if (skillContributors != null) {
                        Optional<Skill> availableContributor = skillContributors.stream()
                                .filter(skill ->
                                        !skill.busy &&
                                                skill.level >= role.level &&
                                                !toBeReservedContributors.containsKey(skill.contributor))
                                .findFirst();
                        if (availableContributor.isPresent()) {
                            toBeReservedContributors.put(availableContributor.get().contributor, availableContributor.get());
                            role.contributor = availableContributor.get();
                        } else {
                            canBeDone = false;
                            break;
                        }
                    } else {
                        toBeRemoved.add(name);
                        canBeDone = false;
                        break;
                    }
                }

                if (canBeDone) {
                    toBeReservedContributors.values().forEach(con -> con.busy = true);
                    project.startDate = day;
                    toBeRemoved.add(name);
                    inProgressProjects.add(name);
                }
            }
            todoProjects.removeAll(toBeRemoved);
            toBeRemoved.clear();

            for (String name : inProgressProjects) {
                Project project = projects.get(name);
                if (day > (project.startDate + project.duration)) {
                    toBeRemoved.add(name);
                    StringBuilder sb = new StringBuilder();
                    sb.append(name);
                    sb.append('\n');
                    sb.append(
                            project.roles
                                    .stream()
                                    .map(role -> {
                                        Skill foundSkill = role.contributor;
                                        if (foundSkill != null) {
                                            if (foundSkill.level == role.level) {
                                                foundSkill.level++;
                                            }
                                            foundSkill.busy = false;
                                            return foundSkill.contributor;
                                        }

                                        return null;
                                    })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.joining(" "))
                    );
                    sb.append('\n');
                    doneProjects.add(sb.toString().trim());
                }
            }
            inProgressProjects.removeAll(toBeRemoved);
            day++;
        }

        out.println(doneProjects.size());
        for (String done : doneProjects) {
            out.println(done);
        }

    }
}
