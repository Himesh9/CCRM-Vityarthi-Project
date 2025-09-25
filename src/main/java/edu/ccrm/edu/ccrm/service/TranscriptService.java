package edu.ccrm.service;

import edu.ccrm.domain.*;
import java.util.*;
import java.util.stream.Collectors;

public class TranscriptService {
    private final DataStore ds = DataStore.get();

    public String generateTranscript(String studentId){
        var student = ds.findStudentById(studentId).orElse(null);
        if(student==null) return "Student not found";
        StringBuilder sb = new StringBuilder();
        sb.append("Transcript for: ").append(student.getFullName()).append(" ("+student.getRegNo()+")\n");
        sb.append("-------------------------------------------------\n");
        var enrollments = ds.listEnrollmentsForStudent(studentId);
        if(enrollments.isEmpty()) { sb.append("No enrollments\n"); return sb.toString(); }
        sb.append(String.format("%-10s %-30s %-6s %-5s\n","Code","Title","Credits","Grade"));
        int totalCredits = 0;
        for(var e : enrollments){
            var course = ds.findCourseByCode(e.getCourseCode()).orElse(null);
            String title = course==null? "<unknown>" : course.getTitle();
            int credits = course==null? 0 : course.getCredits();
            totalCredits += credits;
            String grade = e.getGrade()==null? "-" : e.getGrade().name();
            sb.append(String.format("%-10s %-30s %-6d %-5s\n", e.getCourseCode(), title, credits, grade));
        }
        double gpa = ds.computeGPA(studentId);
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("Total credits: %d   GPA: %.2f\n", totalCredits, gpa));
        return sb.toString();
    }
}
