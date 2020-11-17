package com.company;
import java.sql.*;

public class Manager {
    private Connection conn;

    public Manager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=Dskitty12;&serverTimezone=UTC");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void initializeDB(){
        try {
            Statement stmt = conn.createStatement();

            // create database if does not exist already:
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS enrolldb DEFAULT CHARACTER SET utf8");

            // use database:
            stmt.executeUpdate("USE enrolldb");

            // create students table:
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS enrolldb.students (" +
                    "  student_id INT NOT NULL AUTO_INCREMENT," +
                    "  first_name VARCHAR(45) NOT NULL," +
                    "  last_name VARCHAR(45) NOT NULL," +
                    "  PRIMARY KEY (student_id))" +
                    "ENGINE = InnoDB;");

            // create courses table:
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS enrolldb.courses (" +
                    "  course_id INT NOT NULL AUTO_INCREMENT," +
                    "  course_name VARCHAR(45) NOT NULL," +
                    "  start_time TIME NOT NULL," +
                    "  end_time TIME NOT NULL," +
                    "  days SET('M', 'T', 'W', 'Th', 'F', 'Sa', 'Su') NOT NULL," +
                    "  PRIMARY KEY (course_id)," +
                    "  UNIQUE INDEX course_name_UNIQUE (course_name ASC) VISIBLE)" +
                    "ENGINE = InnoDB;");

            // create enrollments table:
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS enrolldb.enrollments (" +
                    "  student_id INT NOT NULL," +
                    "  course_id INT NOT NULL," +
                    "  PRIMARY KEY (student_id, course_id)," +
                    "  INDEX enrollments_fk_students_idx (student_id ASC) VISIBLE," +
                    "  INDEX enrollments_fk_courses_idx (course_id ASC) VISIBLE," +
                    "  CONSTRAINT enrollments_fk_students" +
                    "    FOREIGN KEY (student_id)" +
                    "    REFERENCES enrolldb.students (student_id)" +
                    "    ON DELETE NO ACTION" +
                    "    ON UPDATE NO ACTION," +
                    "  CONSTRAINT enrollments_fk_courses" +
                    "    FOREIGN KEY (course_id)" +
                    "    REFERENCES enrolldb.courses (course_id)" +
                    "    ON DELETE NO ACTION" +
                    "    ON UPDATE NO ACTION)" +
                    "ENGINE = InnoDB;");
            stmt.close();
            System.out.println("Initialized database.");
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void insertStudent(String first, String last){
        try{
            Statement stmt = conn.createStatement();
            int success = stmt.executeUpdate("INSERT INTO students " +
                    "VALUES (DEFAULT, '" + first + "', '" + last + "')");
            if (success != 0) {
                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID() FROM students");
                rs.next();
                System.out.println("Successfully inserted " + first + " " + last + " with student ID #" + rs.getInt(1) + ".");
                rs.close();
            }
            else{
                System.out.println("Did not insert student.");
            }
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void insertCourse(String name, String start, String end, String days){
        try{
            Statement stmt = conn.createStatement();
            int success = stmt.executeUpdate("INSERT INTO courses " +
                    "VALUES (DEFAULT, '" + name + "', '" + start + "', '" + end + "', '" + days + "')");
            if (success != 0) {
                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID() FROM courses");
                rs.next();
                System.out.println("Successfully inserted " + name + " with course ID #" + rs.getInt(1) + ".");
                rs.close();
            }
            else{
                System.out.println("Did not insert course.");
            }
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void insertEnrollment(String student, String course){
        try{
            Statement stmt = conn.createStatement();
            int success = stmt.executeUpdate("INSERT INTO enrollments " +
                    "VALUES (" + student + ", " + course + ")");
            if (success != 0) {
                ResultSet rs = stmt.executeQuery("SELECT CONCAT(first_name, ' ', last_name) AS full_name, course_name " +
                        "FROM courses c JOIN enrollments e " +
                        "ON c.course_id = e.course_id " +
                        "JOIN students s " +
                        "ON e.student_id = s.student_id " +
                        "WHERE s.student_id = " + student + " AND c.course_id = " + course);
                rs.next();
                System.out.println("Successfully inserted " + rs.getString(1) + " into " + rs.getString(2) + ".");
                rs.close();
            }
            else{
                System.out.println("Did not insert enrollment.");
           }
            stmt.close();

        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void queryStudentsInCourse(String course_id){
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT CONCAT(course_name, ' (#', c.course_id, ')') AS course_info, " +
                    "CONCAT(first_name, ' ', last_name, ' (#', s.student_id, ')') AS student_info " +
                    "FROM courses c JOIN enrollments e " +
                    "ON c.course_id = e.course_id " +
                    "JOIN students s " +
                    "ON e.student_id = s.student_id " +
                    "WHERE c.course_id = " + course_id +
                    " ORDER BY s.student_id;");
            // if no results found:
            if (!rs.next()){
                System.out.println("This class does not exist or there are no students in this class.");
            }
            else{
                System.out.println(rs.getString(1) + "'s students are:");
                System.out.println(rs.getString(2));
            }
            while (rs.next()){
                System.out.println(rs.getString(2));
            }
            stmt.close();
            rs.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void queryCoursesForStudent(String student_id){
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT CONCAT(first_name, ' ', last_name, ' (#', s.student_id, ')') AS student_info, " +
                    "CONCAT(course_name, ' (#', c.course_id, ')', ': ', start_time, ' - ', end_time, ' ', days) AS course_info " +
                    "FROM courses c JOIN enrollments e " +
                    "ON c.course_id = e.course_id " +
                    "    JOIN students s " +
                    "ON e.student_id = s.student_id " +
                    "WHERE s.student_id = " + student_id +
                    " ORDER BY c.course_id;");
            // if no results found:
            if (!rs.next()){
                System.out.println("This student does not exist or there are no classes for this student.");
            }
            else{
                System.out.println(rs.getString(1) + "'s classes are:");
                System.out.println(rs.getString(2));
            }
            while (rs.next()){
                System.out.println(rs.getString(2));
            }
            stmt.close();
            rs.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void queryCoursesOnDay(String student_id, String day){
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT CONCAT(first_name, ' ', last_name, ' (#', s.student_id, ')') AS student_info, " +
                    "CONCAT(course_name, ' (#', c.course_id, '): ', start_time, ' - ', end_time) AS course_info " +
                    "FROM courses c JOIN enrollments e " +
                    "ON c.course_id = e.course_id " +
                    "JOIN students s " +
                    "ON e.student_id = s.student_id " +
                    "WHERE s.student_id = " + student_id + " AND FIND_IN_SET('" + day + "',c.days) " +
                    "ORDER BY c.course_id");
            // if no results found:
            if (!rs.next()){
                System.out.println("This student does not exist or there are no classes for the student on this day.");
            }
            else{
                System.out.println(rs.getString(1) + "'s classes on " + day + " are:");
                System.out.println(rs.getString(2));
            }
            while (rs.next()){
                System.out.println(rs.getString(2));
            }
            stmt.close();
            rs.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void closeConn(){
        try{
            conn.close();
        }
        catch(SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}

