package com.google.hashcode;

import java.util.LinkedList;
import java.util.List;

class ProjectRole {
    String name;
    int level;
    Skill contributor;

    public ProjectRole(String name, int level) {
        this.name = name;
        this.level = level;
    }
}

public class Project {
    String name;
    int duration;
    int score;
    int bestBefore;
    int startDate;
    List<ProjectRole> roles = new LinkedList<>();

    public Project(String name, int duration, int score, int bestBefore) {
        this.name = name;
        this.duration = duration;
        this.score = score;
        this.bestBefore = bestBefore;
    }

    public void addRole(String name, int level) {
        this.roles.add(new ProjectRole(name, level));
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", score=" + score +
                ", bestBefore=" + bestBefore +
                ", roles=" + roles +
                '}';
    }
}
