package com.ibm.jason.arnold;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class CompareCompression 
{
	public static void main(String[] args)
	{
		try
		{
			if (args.length != 1)
			{
				System.out.println("Usage: CompareCompression <output file name>");
				System.exit(1);
			}
			PrintWriter out = new PrintWriter(new FileWriter(args[0]));
			HashMap<String, Double> map = new HashMap<String, Double>(); //db2 ratio
			HashMap<String, Double> map2 = new HashMap<String, Double>(); //IDAA ratio
			HashMap<String, Double> map3 = new HashMap<String, Double>(); //DB2 uncompressed
			HashMap<String, Double> map4 = new HashMap<String, Double>(); //DB2 compressed
			HashMap<String, Double> map5 = new HashMap<String, Double>(); //IDAA compressed
		
			Scanner in = new Scanner(System.in);
			System.out.print("Enter hostname for DB2 z/OS subsystem: ");
			String host = in.nextLine();
			System.out.print("Enter port number for DB2 z/OS subsystem: ");
			int port = Integer.parseInt(in.nextLine());
			System.out.print("Enter location name for DB2 z/OS subsystem: ");
			String location = in.nextLine();
			System.out.print("Enter userid: ");
			String user = in.nextLine();
			EraserThread et = new EraserThread(" Enter password (not displayed): ");
			et.start();
			String pwd = in.nextLine();
			et.stopMasking();
			String password = in.nextLine();
			System.out.print("Enter accelerator name: ");
			String accelName = in.nextLine();
		
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			
			Connection con = con = DriverManager.getConnection(
					"jdbc:db2://" + host + ":" + port + "/" + location,
					user,
					password);
			
			System.out.println("Connected!");
		
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println("Getting information about DB2 compression");
			ResultSet rs = stmt.executeQuery("SELECT B.CREATOR, B.NAME, BIGINT(A.AVGROWLEN) * BIGINT(A.CARD) AS COMPRESSED_BYTES, (100.0 / (100.0 - PAGESAVE)) AS COMPRESSION_RATIO FROM SYSIBM.SYSTABLEPART A, SYSIBM.SYSTABLES B, SYSACCEL.SYSACCELERATEDTABLES C WHERE A.COMPRESS='Y' AND C.CREATOR = B.CREATOR AND C.NAME = B.NAME AND A.DBNAME = B.DBNAME AND A.TSNAME = B.TSNAME;                                                            ");
			int i = 0;
			while (rs.next())
			{
				String schema = rs.getString(1);
				String table = rs.getString(2);
				long compressed = rs.getLong(3);
				Double d = map4.get(schema + "." + table);
				if (d == null)
				{
					map4.put(schema + "." + table, new Double(compressed));
				}
				else
				{
					map4.put(schema + "." + table, d + compressed);
				}
				
				double ratio = rs.getDouble(4);
				double uncompressed = compressed * ratio;
				
				d = map3.get(schema + "." + table);
				if (d == null)
				{
					map3.put(schema + "." + table, uncompressed);
				}
				else
				{
					map3.put(schema + "." + table, d + uncompressed);
				}
				
				i++;
				
				if (i % 1000 == 0)
				{
					System.out.println(i + " tables processed");
				}
			}
			
			rs.close();
			
			for (Map.Entry entry : map3.entrySet())
			{
				double uncompressed = (Double)entry.getValue();
				double compressed = map4.get((String)entry.getKey());
				map.put((String)entry.getKey(), uncompressed / compressed);
			}
			
			PreparedStatement stmt2 = con.prepareStatement("CALL SYSPROC.ACCEL_GET_TABLES_INFO('" + accelName + "', ?, ?)");
			stmt2.setNull(1, java.sql.Types.CHAR);
			stmt2.setNull(2, java.sql.Types.CHAR);
			String xml = "";
			System.out.println("Gathering IDAA compression info");
			stmt2.execute();
			rs = stmt2.getResultSet();
			stmt2.getMoreResults();
			rs = stmt2.getResultSet();

			while (rs.next())
			{
				xml += rs.getString(2);
			}
			
			int index = 0;
			i = 0;
			index = xml.indexOf("<table schema=\"", index);
			while (index != -1)
			{
				int end = xml.indexOf("\"", index+15);
				String schema = xml.substring(index+15, end);
				index = xml.indexOf("name=\"", end);
				end = xml.indexOf("\"", index+6);
				String table = xml.substring(index+6, end);
				index = xml.indexOf("<statistics usedDiskSpaceInMB=\"", end);
				end = xml.indexOf("\"", index+31);
				long bytes = Long.parseLong(xml.substring(index+31, end));
				bytes *= (1024 * 1024);
				Double d = map3.get(schema + "." + table);
				if (d != null && bytes > 0)
				{
					map2.put(schema + "." + table, d / bytes);
					map5.put(schema + "." + table, new Double(bytes));
					i++;
					
					if (i % 1000 == 0)
					{
						System.out.println(i + " tables processed");
					}
				}
				
				index = xml.indexOf("<table schema=\"", index);
			}
			
			DecimalFormat df = new DecimalFormat("#.00"); 
			double totalUncompressed = 0;
			double totalCompressed = 0;
			double totalUncompressed2 = 0;
			double totalCompressed2 = 0;
			for (Map.Entry entry : map.entrySet())
			{
				Double d = map2.get(entry.getKey());
				if (d != null)
				{
					out.println(((String)entry.getKey()) + " - DB2 Compression Ratio: " + df.format((Double)entry.getValue()) + "x, IDAA Compression Ratio: " + df.format(d) + "x");
					out.println(((String)entry.getKey()) + " - Uncompressed size: " + printSize(map3.get(entry.getKey())) + " Size in DB2: " + printSize(map4.get(entry.getKey())) + " Size in IDAA: " + printSize(map5.get(entry.getKey())));
					totalUncompressed += map3.get(entry.getKey());
					totalCompressed += map4.get(entry.getKey());
					totalCompressed2 += map5.get(entry.getKey());
					totalUncompressed2 += (map5.get(entry.getKey()) * d);
				}
			}
			
			out.println("");
			out.println("Overall DB2 compression ratio: " + df.format(totalUncompressed / totalCompressed));
			out.println("Overall IDAA compression ratio: " + df.format(totalUncompressed2 / totalCompressed2));
			out.println("Total uncompressed size: " + printSize(totalUncompressed));
			out.println("Total size in DB2: " + printSize(totalCompressed));
			out.println("Total size in IDAA: " + printSize(totalCompressed2));
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static String printSize(double bytes)
	{
		DecimalFormat df = new DecimalFormat("#.00"); 
		if (bytes < 1024.0)
		{
			return ((int)bytes) + "B";
		}
		
		if (bytes < 1024.0 * 1024.0)
		{
			return df.format(bytes / 1024.0) + "KB";
		}
		
		if (bytes < 1024.0 * 1024.0 * 1024.0)
		{
			return df.format(bytes / (1024.0 * 1024.0)) + "MB";
		}
		
		if (bytes < 1024.0 * 1024.0 * 1024.0 * 1024.0)
		{
			return df.format(bytes / (1024.0 * 1024.0 * 1024.0)) + "GB";
		}
		
		return df.format(bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0)) + "TB";
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
