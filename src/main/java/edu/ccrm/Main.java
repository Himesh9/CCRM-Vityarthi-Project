package edu.ccrm;

import edu.ccrm.domain.*;
import edu.ccrm.service.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();
    private static final CourseService courseService = new CourseService();
    private static final EnrollmentService enrollmentService = new EnrollmentService();

    public static void main(String[] args){
        System.out.println("Welcome to CCRM (starter)");
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
                case "0": break loop;
                default: System.out.println("Unknown choice");
            }
        }
        System.out.println("Bye");
    }

    private static void printMenu(){
        System.out.println("\nMenu:\n1) Add Student\n2) List Students\n3) Add Course\n4) List Courses\n5) Enroll Student\n6) List Enrollments\n0) Exit\nEnter choice:");
    }

    private static void createStudent(){
        System.out.print("RegNo: "); String reg = sc.nextLine().trim();
        System.out.print("Full name: "); String name = sc.nextLine().trim();
        System.out.print("Email: "); String email = sc.nextLine().trim();
        var s = studentService.createStudent(reg, name, email);
        System.out.println("Created: " + s);
    }

    private static void listStudents(){
        var all = studentService.listAll();
        if(all.isEmpty()) System.out.println("No students");
        else all.forEach(System.out::println);
    }

    private static void createCourse(){
        System.out.print("Course code: "); String code = sc.nextLine().trim();
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
        System.out.print("CourseCode: "); String cc = sc.nextLine().trim();
        System.out.print("Semester (SPRING,SUMMER,FALL): "); Semester sem = Semester.valueOf(sc.nextLine().trim().toUpperCase());
        try {
            var e = enrollmentService.enroll(sid, cc, sem);
            System.out.println("Enrolled: " + e);
        } catch(Exception ex){
            System.err.println("Failed to enroll: " + ex.getMessage());
        }
    }

    private static void listEnrollments(){
        var all = DataStore.get().listAllEnrollments();
        if(all.isEmpty()) System.out.println("No enrollments");
        else all.forEach(System.out::println);
    }
}
