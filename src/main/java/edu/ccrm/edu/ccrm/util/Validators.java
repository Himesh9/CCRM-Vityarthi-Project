package edu.ccrm.util;

import java.util.regex.Pattern;

public final class Validators {
    private Validators(){}

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern REGNO = Pattern.compile("^[A-Z0-9-]{3,20}$");
    private static final Pattern COURSE = Pattern.compile("^[A-Z]{2,6}[0-9]{2,4}$");

    public static boolean isEmail(String s){ return s!=null && EMAIL.matcher(s).matches(); }
    public static boolean isRegNo(String s){ return s!=null && REGNO.matcher(s).matches(); }
    public static boolean isCourseCode(String s){ return s!=null && COURSE.matcher(s).matches(); }
}
