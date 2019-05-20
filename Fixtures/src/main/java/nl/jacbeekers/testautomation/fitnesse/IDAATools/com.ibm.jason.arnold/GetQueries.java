package nl.jacbeekers.testautomation.fitnesse.IDAATools.com.ibm.jason.arnold;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class GetQueries
{
	public static void main(String[] args)
	{
		try
		{
			Scanner in = new Scanner(System.in);
                    /*
			System.out.print(" Enter hostname for DB2 z/OS subsystem: ");
			String host = in.nextLine();
			System.out.print(" Enter port number for DB2 z/OS subsystem: ");
			int port = Integer.parseInt(in.nextLine());
			System.out.print(" Enter location name for DB2 z/OS subsystem (case sensitive): ");
			String location = in.nextLine();
			System.out.print(" Enter userid: ");
			String user = in.nextLine();
		*/
                    String host="someserver.somewhere.nl";
                    int port=1234;
                    String location="dbname";
                    String user ="me";
                    String password="verysecret";
                    String accelName="IDAA";
                    String fn="list.txt";

                    /*
                        EraserThread et = new EraserThread(" Enter password (not displayed): ");
			et.start();
			String password = in.nextLine();
			et.stopMasking();
			System.out.print("Enter accelerator name (case sensitive): ");
			String accelName = in.nextLine();
			System.out.print(" Enter output filename: ");
			String fn = in.nextLine();
*/
                    PrintWriter out = new PrintWriter(new FileWriter(fn));

//			Class.forName("com.ibm.db2.jcc.DB2Driver");

			Connection con = null;
			try
			{
				con= con = DriverManager.getConnection(
					"jdbc:db2://" + host + ":" + port + "/" + location,
					user,
					password);
			}
			catch(Exception e)
			{
				System.out.println("The connection failed.  Check your connection parameters! host=>" +host + "< port=>" +port
                                                   +"< database =>" + location +"< user=>" +user +"<" );
                                System.out.println(e.toString());
				System.exit(1);
			}

			System.out.println(" Connected!");
			System.out.println(" Getting query list");
			HashSet<Pair> pairs = getQueryHistory(con, accelName);
			System.out.println(" Getting query details");
			int size = pairs.size();
			int i = 0;
			int target = 10;
			for (Pair pair : pairs)
			{
				out.println(getSQL(pair, con, accelName));
				i++;
				if (i * 100 / size >= target)
				{
					System.out.println(" " + getLargestMultipleOf10(i, size) + "% complete");
					target = getNextTarget(i, size);
				}
			}
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static String getSQL(Pair pair, Connection con, String accelName) throws Exception
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <dwa:querySelection xmlns:dwa=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> <filter scope=\"completed\" /> </dwa:querySelection>";
		CallableStatement stmt = con.prepareCall("CALL SYSPROC.ACCEL_GET_QUERY_DETAILS('" + accelName + "', " + pair.id + ", ?)");
		stmt.setNull(1, java.sql.Types.CHAR);
		//ResultSet rs = stmt.executeQuery();
		stmt.execute();
		ResultSet rs = stmt.getResultSet();
		StringBuilder buffer = new StringBuilder();
		while (rs.next())
		{
			buffer.append(rs.getString(2));
		}

		rs.close();
		stmt.close();
		try
		{
			String sql = buffer.toString();
			int end = sql.indexOf("<<<<<<<<<<< NPS VERSION >>>>>>>>>>");
			if (end != -1)
			{
				sql = sql.substring(0, end);
				sql = sql.replaceAll("\\s+", " ").trim();
				if (sql.startsWith("DECLARE"))
				{
					int x = sql.indexOf("CURSOR FOR ");
					sql = sql.substring(x+11);
					x = sql.lastIndexOf(' ');
					String temp = sql.substring(0, x);
					if (temp.endsWith("QUERYNO"))
					{
						sql = sql.substring(0, x-8);
					}
				}
			}
			else
			{
				sql = sql.replaceAll("\\s+", " ").trim();
				if (sql.startsWith("DECLARE"))
				{
					int x = sql.indexOf("CURSOR FOR ");
					sql = sql.substring(x+11);
					x = sql.lastIndexOf(' ');
					String temp = sql.substring(0, x);
					if (temp.endsWith("QUERYNO"))
					{
						sql = sql.substring(0, x-8);
					}
				}
			}

			return sql.toUpperCase() + "\n";
		}
		catch(Exception e)
		{
			System.out.println(" Unexpected response received from ACCEL_GET_QUERY_DETAILS");
			String fn = System.currentTimeMillis() + ".txt";
			System.out.println(" The reponse in error will be written to " + fn);
			PrintWriter out = new PrintWriter(new FileWriter(fn));
			out.println(buffer.toString());
			out.println("");
			e.printStackTrace(out);
			out.close();
			System.exit(1);
			return null;
		}
	}

	private static int getNextTarget(int i, int size)
	{
		int p = i * 100 / size;
		int retval = 10;
		if (p >= 10)
		{
			retval = 20;
		}

		if (p >= 20)
		{
			retval = 30;
		}

		if (p >= 30)
		{
			retval = 40;
		}

		if (p >= 40)
		{
			retval = 50;
		}

		if (p >= 50)
		{
			retval = 60;
		}

		if (p >= 60)
		{
			retval = 70;
		}

		if (p >= 70)
		{
			retval = 80;
		}

		if (p >= 80)
		{
			retval = 90;
		}

		if (p >= 90)
		{
			retval = 100;
		}

		if (p >= 100)
		{
			retval = 110;
		}

		return retval;
	}

	private static int getLargestMultipleOf10(int i, int size)
	{
		int p = i * 100 / size;
		int retval = 0;
		if (p >= 10)
		{
			retval = 10;
		}

		if (p >= 20)
		{
			retval = 20;
		}

		if (p >= 30)
		{
			retval = 30;
		}

		if (p >= 40)
		{
			retval = 40;
		}

		if (p >= 50)
		{
			retval = 50;
		}

		if (p >= 60)
		{
			retval = 60;
		}

		if (p >= 70)
		{
			retval = 70;
		}

		if (p >= 80)
		{
			retval = 80;
		}

		if (p >= 90)
		{
			retval = 90;
		}

		if (p >= 100)
		{
			retval = 100;
		}

		return retval;
	}


	private static HashSet<Pair> getQueryHistory(Connection con, String accelName) throws Exception
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <dwa:querySelection xmlns:dwa=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\">"
//            + "<filter scope=\"completed\" /> </dwa:querySelection>";
	    + "<filter scope=\"active\" /> </dwa:querySelection>";
		CallableStatement stmt = con.prepareCall("CALL SYSPROC.ACCEL_GET_QUERIES('" + accelName + "', ?, ?, ?)");
		stmt.setString(1, xml);
		stmt.registerOutParameter(2, java.sql.Types.CHAR);
		stmt.setNull(3, java.sql.Types.CHAR);
		stmt.execute();
		String out = null;
		try
		{
			out = stmt.getString(2);
		}
		catch(Exception e)
		{
			System.out.println("An error occurred while trying to get the query history.  Maybe the accelerator name was wrong?");
			System.exit(1);
		}
		stmt.close();
		//System.out.println(out);
		HashSet<Pair> retval = new HashSet<Pair>();
		int i = out.indexOf("planID=\"");
		while (i != -1)
		{
			int e = out.indexOf('"', i+8);
			int id = Integer.parseInt(out.substring(i+8, e));
			i = out.indexOf("<sql><![CDATA[", e);
			e = out.indexOf("]]></sql>", i);
			String sql = out.substring(i+14, e);
			sql = sql.replaceAll("\\s+", " ").trim();
			if (sql.startsWith("DECLARE"))
			{
				int x = sql.indexOf("CURSOR FOR ");
				sql = sql.substring(x+11);
				x = sql.lastIndexOf(' ');
				String temp = sql.substring(0, x);
				if (temp.endsWith("QUERYNO"))
				{
					sql = sql.substring(0, x-8);
				}
			}

			retval.add(new Pair(id, sql));
			i = out.indexOf("planID=\"", e);
		}

		System.out.println(" Found " + retval.size() + " distinct queries");
		return retval;
	}

	private static class Pair
	{
		private int id;
		private String sql;

		public Pair(int id, String sql)
		{
			this.id = id;
			this.sql = sql;
		}

		public int hashCode()
		{
			return sql.hashCode();
		}

		public boolean equals(Object r)
		{
			Pair rhs = (Pair)r;
			return sql.equals(rhs.sql);
		}
	}

	private static class EraserThread extends Thread {
		private volatile boolean stop;
		   private char echochar = '*';

		  /**
		   *@param prompt The prompt displayed to the user
		   */
		   public EraserThread(String prompt) {
		      System.out.print(prompt);
		   }

		  /**
		   * Begin masking until asked to stop.
		   */
		   public void run() {

		      int priority = Thread.currentThread().getPriority();
		      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		      try {
		         stop = true;
		         while(stop) {
		        	 System.out.print("\010" + " ");
		         }
		      } finally { // restore the original priority
		         Thread.currentThread().setPriority(priority);
		      }
		   }

		  /**
		   * Instruct the thread to stop masking.
		   */
		   public void stopMasking() {
		      this.stop = false;
		   }
	}
}
