package com.company;
import java.sql.*;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        final Manager manager = new Manager();
        manager.initializeDB();

        // VARIOUS TESTS:
        //manager.insertStudent("Marty","Tom");
        //manager.insertCourse("Coding 101","9:00","10:00","T,Th");
        //manager.insertEnrollment("2","2");
        //manager.queryStudentsInCourse("1");
        //manager.queryCoursesForStudent("2");
        //manager.queryCoursesOnDay("2","Th");

        Boolean running = true;
        Scanner input = new Scanner(System.in);

        while(running) {
            System.out.println("Would you like to insert (1), query (2), or exit (3)?");
            int first = input.nextInt();
            if (first == 1){
                System.out.println("Would you like to insert a student to the program (1), a new course (2), or a student to a course (3)?");
                int insertChoice = input.nextInt();
                if (insertChoice == 1){
                    System.out.println("What is the first name of the student?");
                    String first_name = input.next();
                    System.out.println("What is the last name of the student?");
                    String last_name = input.next();
                    manager.insertStudent(first_name,last_name);
                }
                else if (insertChoice == 2){
                    System.out.println("What is the name of the course?");
                    String course_name = input.next();
                    System.out.println("What is the start time?");
                    String start_time = input.next();
                    System.out.println("What is the end time?");
                    String end_time = input.next();
                    System.out.println("What is days does the course take place (M,T,W,Th,F,Sa,Su)? Enter days separated by a comma.");
                    String course_days = input.next();
                    manager.insertCourse(course_name,start_time,end_time,course_days);
                }
                else if (insertChoice == 3){
                    System.out.println("What is the student's ID number?");
                    String student_id = input.next();
                    System.out.println("What is the course's ID number?");
                    String course_id = input.next();
                    manager.insertEnrollment(student_id,course_id);
                }
            }
            else if (first == 2){
                System.out.println("Would you like to query the students in a course (1) or the courses for a student (2)?");
                int queryChoice = input.nextInt();
                if (queryChoice == 1){
                    System.out.println("What is the course ID?");
                    String course_id = input.next();
                    manager.queryStudentsInCourse(course_id);
                }
                else if (queryChoice == 2){
                    System.out.println("What is the student's ID number?");
                    String student_id = input.next();
                    System.out.println("Would you like to see all courses (1) or courses on one day (2)?");
                    int dayChoice = input.nextInt();
                    if (dayChoice == 1){
                        manager.queryCoursesForStudent(student_id);
                    }
                    else if (dayChoice == 2){
                        System.out.println("What day of the week (M,T,W,Th,F,Sa,Su)?");
                        String day = input.next();
                        manager.queryCoursesOnDay(student_id,day);
                    }
                }
            }
            else if (first == 3){
                System.out.println("Exiting database...");
                running = false;
            }
        }

        manager.closeConn();
        /*
        Actions:
            - inserting:
                - new student to program (ask for first and last name, then return the student id)
                - new course (ask for name, start time, end time, days, then return course id)
                - student to course (ask for student id, course id, return student and course id)
            - querying:
                - students in 1 course
                - courses for 1 student
                - which courses and what times each course is for 1 student on 1 day
         Action tree:
            Insert, query, or exit?
            -> insert: student to program (1)? new course (2)? new student to course (3)?
            -> query: students in a course (1)? courses for a student (2)?
                -> (1) which course?
                -> (2) which student? -> all courses (1) or for one day (2)? -> which day? (M,T,W,Th,F,Sa,Su)
         */
    }
}
