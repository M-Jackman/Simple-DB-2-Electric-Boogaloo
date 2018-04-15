import simpledb.remote.SimpleDriver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateStudentDB {
    public static void main(String[] args) {
		Connection conn = null;
		try {
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			Statement stmt = conn.createStatement();

			String s = "create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)";
			stmt.executeUpdate(s);
			System.out.println("Table STUDENT created.");

			s = "insert into STUDENT(SId, SName, MajorId, GradYear) values ";
			String[] studvals = {"(1, 'joe', 10, 2004)",
								 "(2, 'amy', 20, 2004)",
								 "(3, 'max', 10, 2005)",
								 "(4, 'sue', 20, 2005)",
								 "(5, 'bob', 30, 2003)",
								 "(6, 'kim', 20, 2001)",
								 "(7, 'art', 30, 2004)",
								 "(8, 'pat', 20, 2001)",
								 "(9, 'lee', 10, 2004)"};
			for (int i=0; i<studvals.length; i++)
				stmt.executeUpdate(s + studvals[i]);
			System.out.println("STUDENT records inserted.");

			s = "create table DEPT(DId int, DName varchar(8))";
			stmt.executeUpdate(s);
			System.out.println("Table DEPT created.");

			s = "insert into DEPT(DId, DName) values ";
			String[] deptvals = {"(10, 'compsci')",
								 "(20, 'math')",
								 "(30, 'drama')"};
			for (int i=0; i<deptvals.length; i++)
				stmt.executeUpdate(s + deptvals[i]);
			System.out.println("DEPT records inserted.");

			s = "create table COURSE(CId int, Title varchar(20), DeptId int)";
			stmt.executeUpdate(s);
			System.out.println("Table COURSE created.");

			s = "insert into COURSE(CId, Title, DeptId) values ";
			String[] coursevals = {"(12, 'db systems', 10)",
								   "(22, 'compilers', 10)",
								   "(32, 'calculus', 20)",
								   "(42, 'algebra', 20)",
								   "(52, 'acting', 30)",
								   "(62, 'elocution', 30)"};
			for (int i=0; i<coursevals.length; i++)
				stmt.executeUpdate(s + coursevals[i]);
			System.out.println("COURSE records inserted.");

			s = "create table SECTION(SectId int, CourseId int, Prof varchar(8), YearOffered int)";
			stmt.executeUpdate(s);
			System.out.println("Table SECTION created.");

			s = "insert into SECTION(SectId, CourseId, Prof, YearOffered) values ";
			String[] sectvals = {"(13, 12, 'turing', 2004)",
								 "(23, 12, 'turing', 2005)",
								 "(33, 32, 'newton', 2000)",
								 "(43, 32, 'einstein', 2001)",
								 "(53, 62, 'brando', 2001)"};
			for (int i=0; i<sectvals.length; i++)
				stmt.executeUpdate(s + sectvals[i]);
			System.out.println("SECTION records inserted.");

			s = "create table ENROLL(EId int, StudentId int, SectionId int, Grade varchar(2))";
			stmt.executeUpdate(s);
			System.out.println("Table ENROLL created.");

			s = "insert into ENROLL(EId, StudentId, SectionId, Grade) values ";
			String[] enrollvals = {"(14, 1, 13, 'A')",
								   "(24, 1, 43, 'C' )",
								   "(34, 2, 43, 'B+')",
								   "(44, 4, 33, 'B' )",
								   "(54, 4, 53, 'A' )",
								   "(64, 6, 53, 'A' )"};
			for (int i=0; i<enrollvals.length; i++)
				stmt.executeUpdate(s + enrollvals[i]);
			System.out.println("ENROLL records inserted.");

			s = "create table TEACHER(TId int, TName varchar(12), DId int)";
			stmt.executeUpdate(s);
			System.out.println("Table TEACHER created.");

			s = "insert into TEACHER(TId, TName, DId) values ";
			String[] teachvals = {"(1, '', 10)",
					"(2, 'mark', 20)",
					"(3, 'alex', 10)",
					"(4, 'terry', 20)",
					"(5, 'bobbert', 30)",
					"(6, 'kardashian', 20)",
					"(7, 'picasso', 30)",
					"(8, 'patrick', 20)",
					"(9, 'leesin', 10)"};
			for (int i=0; i<teachvals.length; i++)
				stmt.executeUpdate(s + teachvals[i]);
			System.out.println("TEACHER records inserted.");

			s = "create table SCHOOL(SName varchar(25), NStudents int, SchoolMascot varchar(15))";
			stmt.executeUpdate(s);
			System.out.println("Table SCHOOL created.");

			s = "insert into SCHOOL(SName, NStudents, SchoolMascot) values ";
			String[] schoolvals = {"('worcesterState', 6000, 'lions')",
					"('worcesterPolytech', 4000, 'goat')",
					"('fitchburg', 15000, 'eagles')",
					"('assumption', 8000, 'pirates')",
					"('georgeMason', 15000, 'patriots')",
					"('virginia tech', 8000, 'hokies')",
					"('westfield', 4000, 'bulldogs')",
					"('battlefield', 3000, 'bulldogs')",
					"('newEngland', 100, 'patriots')"};
			for (int i=0; i<schoolvals.length; i++)
				stmt.executeUpdate(s + schoolvals[i]);
			System.out.println("SCHOOL records inserted.");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
