package com.ub.conscious;

public class Member {
    private Long id;
    private String name;
    private String surname;
    private String Identity;
    private int birthYear;

    public Member(Long id, String name, String surname, String identity, int birthYear) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        Identity = identity;
        this.birthYear = birthYear;
    }

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIdentity() {
        return Identity;
    }

    public void setIdentity(String identity) {
        Identity = identity;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
}
