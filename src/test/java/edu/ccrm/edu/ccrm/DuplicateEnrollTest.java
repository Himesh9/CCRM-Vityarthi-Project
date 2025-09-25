package edu.ccrm;

import edu.ccrm.domain.Semester;
import edu.ccrm.service.DataStore;
import edu.ccrm.exception.DuplicateEnrollmentException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DuplicateEnrollTest {
    @Test
    public void testDuplicateEnrollment() throws Exception {
        DataStore ds = DataStore.get();
        var s = ds.addStudent("TST-2","Dup Test","dup@example.com");
        ds.addCourse("DUP100","Dup Course",3, Semester.FALL);
        ds.enroll(s.getId(), "DUP100", Semester.FALL);
        assertThrows(DuplicateEnrollmentException.class, () -> {
            ds.enroll(s.getId(), "DUP100", Semester.FALL);
        });
    }
}
