package edu.ccrm.domain;

public abstract class Person {
    protected final String id;
    protected String fullName;
    protected String email;

    protected Person(String id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return String.format("%s(%s) <%s>", fullName, id, email);
    }
}
