package com.flipkart.bean;

import java.util.List;

public class GradeCard {
    private List<Grade> grades;
    private double cgpa;

    public GradeCard() {

    }

    public GradeCard(List<Grade> grades, double cgpa) {
        this.grades = grades;
        this.cgpa = cgpa;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }
}
