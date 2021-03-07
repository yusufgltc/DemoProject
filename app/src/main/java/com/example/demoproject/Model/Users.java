package com.example.demoproject.Model;

public class Users {
    String name;
    String surname;
    String username;
    String email;
    String university;
    String department;
    String bio;

    public Users(){
    }
    public Users(String name, String surname, String username, String email, String university, String department, String bio) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.university = university;
        this.department = department;
        this.bio = bio;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
