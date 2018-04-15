import simpledb.remote.SimpleDriver;
import java.sql.*;

/**
 * Created by Matthew on 4/4/2018.
 */
public class FindMascot {
    public static void main(String[] args) {
        //CS 4432
        // This file will test our additional tables that we added
        // into CreateStudentDB.java. Our configuration when running
        // used 'bulldogs' as the id to be searched for from the schoolmascot field
        String mascot = args[0];
        System.out.println("Here are the schools with " + mascot + " as a mascot");
        System.out.println("SchoolName\tMascot");

        Connection conn = null;
        try {
            // Step 1: connect to database server
            Driver d = new SimpleDriver();
            conn = d.connect("jdbc:simpledb://localhost", null);

            // Step 2: execute the query
            Statement stmt = conn.createStatement();
            String qry = "select sname, schoolmascot "
                    + "from school "
                    + "where schoolmascot = '" + mascot + "'";
            ResultSet rs = stmt.executeQuery(qry);

            // Step 3: loop through the result set
            while (rs.next()) {
                String sname = rs.getString("sname");
                String schoolmascot = rs.getString("schoolmascot");
                System.out.println(sname + "\t" + schoolmascot);
            }
            rs.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            // Step 4: close the connection
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
