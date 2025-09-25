package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.exception.*;
import edu.ccrm.AppConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataStore {
    private static final DataStore INSTANCE = new DataStore();

    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    private final List<Enrollment> enrollments = Collections.synchronizedList(new ArrayList<>());

    private DataStore(){}

    public static DataStore get(){ return INSTANCE; }

    // Student operations
    public Student addStudent(String regNo, String name, String email){
        String id = UUID.randomUUID().toString();
        Student s = new Student(id, regNo, name, email);
        students.put(id, s);
        return s;
    }
    public Optional<Student> findStudentById(String id){ return Optional.ofNullable(students.get(id)); }
    public Optional<Student> findStudentByRegNo(String regNo){
        return students.values().stream().filter(s -> s.getRegNo().equalsIgnoreCase(regNo)).findFirst();
    }
    public List<Student> listStudents(){ return new ArrayList<>(students.values()); }

    // Course
    public Course addCourse(String code, String title, int credits, Semester sem){
        Course c = new Course.Builder().code(code).title(title).credits(credits).semester(sem).build();
        courses.put(code, c);
        return c;
    }
    public Optional<Course> findCourseByCode(String code){ return Optional.ofNullable(courses.get(code)); }
    public List<Course> listCourses(){ return new ArrayList<>(courses.values()); }

    // Enrollment
    public Enrollment enroll(String studentId, String courseCode, Semester sem) throws DuplicateEnrollmentException, EntityNotFoundException {
        Objects.requireNonNull(studentId);
        Objects.requireNonNull(courseCode);
        synchronized(enrollments){
            boolean exists = enrollments.stream().anyMatch(e -> e.getStudentId().equals(studentId) && e.getCourseCode().equalsIgnoreCase(courseCode));
            if(exists) throw new DuplicateEnrollmentException("Student already enrolled in course");
            if(!students.containsKey(studentId)) throw new EntityNotFoundException("Student not found: " + studentId);
            if(!courses.containsKey(courseCode)) throw new EntityNotFoundException("Course not found: " + courseCode);
            Enrollment e = new Enrollment(studentId, courseCode, sem);
            enrollments.add(e);
            Student s = students.get(studentId);
            if(s!=null) s.enroll(courseCode);
            return e;
        }
    }

    public void assignGrade(String studentId, String courseCode, Grade g) throws EntityNotFoundException {
        synchronized(enrollments){
            Optional<Enrollment> oe = enrollments.stream()
                    .filter(e -> e.getStudentId().equals(studentId) && e.getCourseCode().equalsIgnoreCase(courseCode))
                    .findFirst();
            if(oe.isEmpty()) throw new EntityNotFoundException("Enrollment not found");
            oe.get().setGrade(g);
        }
    }

    public List<Enrollment> listEnrollmentsForStudent(String studentId){
        synchronized(enrollments){
            return enrollments.stream().filter(e -> e.getStudentId().equals(studentId)).collect(Collectors.toList());
        }
    }

    public List<Enrollment> listAllEnrollments(){
        synchronized(enrollments){
            return new ArrayList<>(enrollments);
        }
    }

    // GPA calculation
    public double computeGPA(String studentId){
        var list = listEnrollmentsForStudent(studentId);
        double numerator = 0.0;
        int totalCredits = 0;
        for(Enrollment e : list){
            Grade g = e.getGrade();
            Integer credits = Optional.ofNullable(courses.get(e.getCourseCode())).map(Course::getCredits).orElse(0);
            if(g!=null && credits>0){
                numerator += g.getPoints() * credits;
                totalCredits += credits;
            }
        }
        if(totalCredits==0) return 0.0;
        return numerator / totalCredits;
    }

    // Import/export (CSV and JSON)
    public Path exportAllAsJson(Path outFile) throws IOException {
        JSONObject root = new JSONObject();
        JSONArray studs = new JSONArray();
        for(Student s : students.values()){
            JSONObject so = new JSONObject();
            so.put("id", s.getId());
            so.put("regNo", s.getRegNo());
            so.put("fullName", s.getFullName());
            so.put("email", s.getEmail());
            studs.put(so);
        }
        root.put("students", studs);

        JSONArray cors = new JSONArray();
        for(Course c : courses.values()){
            JSONObject co = new JSONObject();
            co.put("code", c.getCode());
            co.put("title", c.getTitle());
            co.put("credits", c.getCredits());
            co.put("semester", c.getSemester().name());
            cors.put(co);
        }
        root.put("courses", cors);

        JSONArray ens = new JSONArray();
        for(Enrollment e : enrollments){
            JSONObject eo = new JSONObject();
            eo.put("studentId", e.getStudentId());
            eo.put("courseCode", e.getCourseCode());
            eo.put("semester", e.getSemester().name());
            eo.put("grade", e.getGrade()==null? JSONObject.NULL : e.getGrade().name());
            ens.put(eo);
        }
        root.put("enrollments", ens);

        Files.createDirectories(outFile.getParent());
        Files.write(outFile, root.toString(2).getBytes(StandardCharsets.UTF_8));
        return outFile;
    }

    public Path exportStudentsCsv(Path outFile) throws IOException {
        Files.createDirectories(outFile.getParent());
        try(BufferedWriter w = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8)){
            w.write("id,regNo,fullName,email\n");
            for(Student s : students.values()){
                w.write(String.format("%s,%s,%s,%s\n", s.getId(), s.getRegNo(), s.getFullName(), s.getEmail()));
            }
        }
        return outFile;
    }

    // Simple JSON import that adds students and courses if absent (id collisions preserved)
    public void importFromJson(Path file) throws IOException {
        String txt = Files.readString(file, StandardCharsets.UTF_8);
        JSONObject root = new JSONObject(txt);
        JSONArray studs = root.optJSONArray("students");
        if(studs!=null){
            for(int i=0;i<studs.length();i++){
                JSONObject so = studs.getJSONObject(i);
                String id = so.getString("id");
                String regNo = so.optString("regNo", "");
                String fullName = so.optString("fullName", "");
                String email = so.optString("email", "");
                // if id exists skip; otherwise add with same id
                if(!students.containsKey(id)){
                    Student s = new Student(id, regNo, fullName, email);
                    students.put(id, s);
                }
            }
        }
        JSONArray cors = root.optJSONArray("courses");
        if(cors!=null){
            for(int i=0;i<cors.length();i++){
                JSONObject co = cors.getJSONObject(i);
                String code = co.getString("code");
                if(!courses.containsKey(code)){
                    Course c = new Course.Builder()
                            .code(code)
                            .title(co.optString("title", code))
                            .credits(co.optInt("credits", 3))
                            .semester(Semester.valueOf(co.optString("semester", "FALL")))
                            .build();
                    courses.put(code, c);
                }
            }
        }
        // enrollments imported but not validated in depth
        JSONArray ens = root.optJSONArray("enrollments");
        if(ens!=null){
            for(int i=0;i<ens.length();i++){
                JSONObject eo = ens.getJSONObject(i);
                String sid = eo.optString("studentId");
                String cc = eo.optString("courseCode");
                Semester sem = Semester.valueOf(eo.optString("semester", "FALL"));
                String gname = eo.optString("grade", null);
                Enrollment e = new Enrollment(sid, cc, sem);
                if(!JSONObject.NULL.equals(gname) && gname!=null && !gname.isBlank()){
                    try{ e.setGrade(Grade.valueOf(gname)); } catch(Exception ex){}
                }
                enrollments.add(e);
            }
        }
    }

    // Backup (simple timestamped folder copy of exported JSON)
    public Path backupToTimestampedFolder() throws IOException {
        Path base = AppConfig.get().getDataDir();
        String stamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path tgt = base.resolve("backup_" + stamp);
        Files.createDirectories(tgt);
        Path out = tgt.resolve("export.json");
        exportAllAsJson(out);
        return tgt;
    }

    // Utility: folder size
    public long folderSize(Path dir) throws IOException {
        try (var stream = Files.walk(dir)) {
            return stream.filter(Files::isRegularFile)
                         .mapToLong(p -> { try { return Files.size(p);} catch(IOException e){ return 0L; }})
                         .sum();
        }
    }
}
