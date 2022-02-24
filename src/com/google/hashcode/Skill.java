package com.google.hashcode;

public class Skill {
    String skillName;
    String contributor;
    int level;
    boolean busy = false;

    public Skill(String skillName, String contributor, int level) {
        this.skillName = skillName;
        this.contributor = contributor;
        this.level = level;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "skillName='" + skillName + '\'' +
                ", contributor='" + contributor + '\'' +
                ", level=" + level +
                ", busy=" + busy +
                '}';
    }
}
