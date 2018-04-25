package simpledb.remote;

import java.sql.*;

/**
 * An adapter class that wraps RemoteStatement.
 * Its methods do nothing except transform RemoteExceptions
 * into SQLExceptions.
 * @author Edward Sciore
 */
public class SimpleStatement extends StatementAdapter {
   private RemoteStatement rmtstmt;
   int counter = 0;
   
   public SimpleStatement(RemoteStatement s) {
      rmtstmt = s;
   }
   
   public ResultSet executeQuery(String qry) throws SQLException {
      try {
         RemoteResultSet rrs = rmtstmt.executeQuery(qry);
         return new SimpleResultSet(rrs);
      }
      catch(Exception e) {
         throw new SQLException(e);
      }
   }
   
   public int executeUpdate(String cmd) throws SQLException {
      System.out.println("This is a test"+ counter);
      try {
         counter++;
         return rmtstmt.executeUpdate(cmd);
      }
      catch(Exception e) {
         throw new SQLException(e);
      }
   }
}

