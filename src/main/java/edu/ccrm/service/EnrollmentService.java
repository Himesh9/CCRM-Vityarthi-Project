package edu.ccrm.service;

import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Semester;
import java.util.List;

public class EnrollmentService {
    private final DataStore ds = DataStore.get();

    public Enrollment enroll(String studentId, String courseCode, Semester sem) throws Exception {
        return ds.enroll(studentId, courseCode, sem);
    }

    public List<Enrollment> listForStudent(String studentId){
        return ds.listEnrollmentsForStudent(studentId);
    }
}
