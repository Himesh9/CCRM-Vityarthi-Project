package edu.ccrm.domain;

import java.time.LocalDate;

public class Enrollment {
    private final String studentId;
    private final String courseCode;
    private final Semester semester;
    private Grade grade; // can be null until graded
    private final LocalDate enrolledAt;

    public Enrollment(String studentId, String courseCode, Semester semester){
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.semester = semester;
        this.enrolledAt = LocalDate.now();
    }

    public String getStudentId(){ return studentId; }
    public String getCourseCode(){ return courseCode; }
    public Semester getSemester(){ return semester; }
    public Grade getGrade(){ return grade; }
    public void setGrade(Grade g){ this.grade = g; }
    public LocalDate getEnrolledAt(){ return enrolledAt; }

    @Override
    public String toString(){
        return String.format("Enrollment[%s -> %s (%s) grade=%s]", studentId, courseCode, semester, grade);
    }
}
