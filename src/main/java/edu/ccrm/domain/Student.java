package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.*;

public class Student extends Person {
    private final String regNo;
    private boolean active = true;
    private final LocalDate createdAt;
    private final List<String> enrolledCourseCodes = new ArrayList<>();

    public Student(String id, String regNo, String fullName, String email) {
        super(id, fullName, email);
        this.regNo = regNo;
        this.createdAt = LocalDate.now();
    }

    public String getRegNo() { return regNo; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    public LocalDate getCreatedAt() { return createdAt; }

    public List<String> getEnrolledCourseCodes() {
        return Collections.unmodifiableList(enrolledCourseCodes);
    }

    public void enroll(String courseCode) {
        if(!enrolledCourseCodes.contains(courseCode)) enrolledCourseCodes.add(courseCode);
    }

    public void unenroll(String courseCode) {
        enrolledCourseCodes.remove(courseCode);
    }

    @Override
    public String toString() {
        return String.format("Student[%s, %s, %s]", id, regNo, fullName);
    }
}
