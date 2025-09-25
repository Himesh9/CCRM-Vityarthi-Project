package edu.ccrm;

import edu.ccrm.domain.*;
import edu.ccrm.service.*;
import edu.ccrm.exception.*;
import edu.ccrm.util.Validators;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();
    private static final CourseService courseService = new CourseService();
    private static final EnrollmentService enrollmentService = new EnrollmentService();
    private static final TranscriptService transcriptService = new TranscriptService();
    private static final DataStore ds = DataStore.get();

    public static void main(String[] args){
        System.out.println("Welcome to CCRM (starter v0.2)");
        loop:
        while(true){
            printMenu();
            String choice = sc.nextLine().trim();
            switch(choice){
                case "1": createStudent(); break;
                case "2": listStudents(); break;
                case "3": createCourse(); break;
                case "4": listCourses(); break;
                case "5": enrollStudent(); break;
                case "6": listEnrollments(); break;
                case "7": assignGrade(); break;
                case "8": printTranscript(); break;
                case "9": exportJson(); break;
                case "10": importJson(); break;
                case "11": backup(); break;
                case "0": break loop;
                default: System.out.println("Unknown choice");
            }
        }
        System.out.println("Bye");
    }

    private static void printMenu(){
        System.out.println("\nMenu:\n1) Add Student\n2) List Students\n3) Add Course\n4) List Courses\n5) Enroll Student\n6) List Enrollments\n7) Assign Grade\n8) Print Transcript\n9) Export JSON\n10) Import JSON\n11) Backup\n0) Exit\nEnter choice:");
    }

    private static void createStudent(){
        System.out.print("RegNo: "); String reg = sc.nextLine().trim();
        if(!Validators.isRegNo(reg)){ System.out.println("Invalid reg no. Example: ABC123"); return; }
        System.out.print("Full name: "); String name = sc.nextLine().trim();
        System.out.print("Email: "); String email = sc.nextLine().trim();
        if(!Validators.isEmail(email)){ System.out.println("Invalid email"); return; }
        var s = studentService.createStudent(reg, name, email);
        System.out.println("Created: " + s);
    }

    private static void listStudents(){
        var all = studentService.listAll();
        if(all.isEmpty()) System.out.println("No students");
        else all.forEach(System.out::println);
    }

    private static void createCourse(){
        System.out.print("Course code: "); String code = sc.nextLine().trim().toUpperCase();
        if(!Validators.isCourseCode(code)){ System.out.println("Invalid course code. Example: CS101"); return; }
        System.out.print("Title: "); String title = sc.nextLine().trim();
        System.out.print("Credits (int): "); int credits = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Semester (SPRING,SUMMER,FALL): "); Semester sem = Semester.valueOf(sc.nextLine().trim().toUpperCase());
        var c = courseService.createCourse(code, title, credits, sem);
        System.out.println("Created: " + c);
    }

    private static void listCourses(){
        var all = courseService.listAll();
        if(all.isEmpty()) System.out.println("No courses");
        else all.forEach(System.out::println);
    }

    private static void enrollStudent(){
        System.out.print("StudentId: "); String sid = sc.nextLine().trim();
        if(!studentService.findById(sid).isPresent()){ System.out.println("Student not found"); return; }
        System.out.print("CourseCode: "); String cc = sc.nextLine().trim().toUpperCase();
        if(!courseService.findByCode(cc).isPresent()){ System.out.println("Course not found"); return; }
        System.out.print("Semester (SPRING,SUMMER,FALL): "); Semester sem = Semester.valueOf(sc.nextLine().trim().toUpperCase());
        try {
            var e = enrollmentService.enroll(sid, cc, sem);
            System.out.println("Enrolled: " + e);
        } catch(DuplicateEnrollmentException ex){
            System.err.println("Failed: " + ex.getMessage());
        } catch(Exception ex){
            System.err.println("Failed to enroll: " + ex.getMessage());
        }
    }

    private static void listEnrollments(){
        var all = ds.listAllEnrollments();
        if(all.isEmpty()) System.out.println("No enrollments");
        else all.forEach(System.out::println);
    }

    private static void assignGrade(){
        System.out.print("StudentId: "); String sid = sc.nextLine().trim();
        System.out.print("CourseCode: "); String cc = sc.nextLine().trim().toUpperCase();
        System.out.print("Grade (S,A,B,C,D,E,F): "); Grade g = Grade.valueOf(sc.nextLine().trim().toUpperCase());
        try{
            ds.assignGrade(sid, cc, g);
            System.out.println("Grade assigned");
        } catch(Exception ex){
            System.err.println("Failed: " + ex.getMessage());
        }
    }

    private static void printTranscript(){
        System.out.print("StudentId: "); String sid = sc.nextLine().trim();
        String t = transcriptService.generateTranscript(sid);
        System.out.println(t);
    }

    private static void exportJson(){
        try{
            Path out = Paths.get(System.getProperty("user.home"), "ccrm_export.json");
            ds.exportAllAsJson(out);
            System.out.println("Exported to: " + out.toString());
        } catch(Exception ex){
            System.err.println("Export failed: " + ex.getMessage());
        }
    }

    private static void importJson(){
        try{
            System.out.print("Path to json file: ");
            String p = sc.nextLine().trim();
            ds.importFromJson(Paths.get(p));
            System.out.println("Import done");
        } catch(Exception ex){
            System.err.println("Import failed: " + ex.getMessage());
        }
    }

    private static void backup(){
        try{
            var tgt = ds.backupToTimestampedFolder();
            long size = ds.folderSize(tgt);
            System.out.println("Backup created at " + tgt + " (size bytes: " + size + ")");
        } catch(Exception ex){
            System.err.println("Backup failed: " + ex.getMessage());
        }
    }
}
