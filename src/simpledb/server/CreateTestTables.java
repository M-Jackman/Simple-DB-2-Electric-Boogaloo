package simpledb.server; /******************************************************************/
        import simpledb.remote.SimpleDriver;

        import java.sql.*;
        import java.util.Random;
public class CreateTestTables {
    final static int maxSize=100; //CS4432 this number was changed after feedback from students and the TAs
    // who felt that 1000 instances should be enough to determine if everything is operating as it should
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Connection conn = null;
        Driver d = new SimpleDriver();
        String host = "localhost"; //you may change it if your SimpleDB server is running on a different machine
        String url = "jdbc:simpledb://" + host;
        String qry = "Create table test1" +
                "( a1 int," +
                "  a2 int"+
                ")";
        Random rand = null;
        Statement s = null;
        try {
            conn = d.connect(url, null);
            s=conn.createStatement();
            s.executeUpdate("Create table test1" +
                    "( a1 int," +
                    "  a2 int" + ") ");
            s.executeUpdate("Create table test2" +
                    "( a1 int," +
                    "  a2 int"+
                    ")");
            s.executeUpdate("Create table test3" +
                    "( a1 int," +
                    "  a2 int"+
                    ")");
            s.executeUpdate("Create table test4" +
                    "( a1 int," +
                    "  a2 int"+
                    ")");
            s.executeUpdate("Create table test5" +
                    "( a1 int," +
                    "  a2 int"+
                    ")");

            s.executeUpdate("create sh index idx1 on test1 (a1)");
            System.out.println("First index created");
            s.executeUpdate("create ex index idx2 on test2 (a1)");
            System.out.println("Second Index Created");
            s.executeUpdate("create bt index idx3 on test3 (a1)");
            System.out.println("BTree index created");
            for(int i=1;i<6;i++)
            {
                if(i!=5)
                {
                    rand=new Random(1);// ensure every table gets the same data
                    for(int j=0;j<maxSize;j++)
                    {
                        s.executeUpdate("insert into test"+i+" (a1,a2) values("+rand.nextInt(1000)+","+rand.nextInt(1000)+ ")");
                    }
                }
                else//case where i=5
                {
                    for(int j=0;j<maxSize/2;j++)// insert 500 records into test5
                    {
                        s.executeUpdate("insert into test"+i+" (a1,a2) values("+j+","+j+ ")");
                    }
                }
            }
            ResultSet resultSet = s.executeQuery("select a1,a1 from test3 where a1 = 4");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }

            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally
        {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

