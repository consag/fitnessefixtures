package com.ibm.jason.arnold;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class EstimateHPSSSavings 
{
	public static void main(String[] args)
	{
		try
		{
			HashMap<String, Long> map = new HashMap<String, Long>(); //amount that can be saved on tables
			HashMap<String, Long> map2 = new HashMap<String, Long>(); //amount that can be saved on indexes
			HashMap<String, ArrayList<String>> table2Index = new HashMap<String, ArrayList<String>>();
		
			Scanner in = new Scanner(System.in);
			System.out.print(" Enter the filename where the output report should be written: ");
			String fn = in.nextLine();
			PrintWriter out = new PrintWriter(new FileWriter(fn));
			System.out.print(" Enter hostname for DB2 z/OS subsystem: ");
			String host = in.nextLine();
			System.out.print(" Enter port number for DB2 z/OS subsystem: ");
			int port = Integer.parseInt(in.nextLine());
			System.out.print(" Enter location name for DB2 z/OS subsystem: ");
			String location = in.nextLine();
			System.out.print(" Enter userid: ");
			String user = in.nextLine();
			EraserThread et = new EraserThread(" Enter password (not displayed): ");
			et.start();
			String pwd = in.nextLine();
			et.stopMasking();
			System.out.print("Include data that has not changed in how many day? ");
			int days = Integer.parseInt(in.nextLine());
			System.out.print(" Cost per GB of DASD (no dollar sign, in dollars and cents): ");
			double cost = Double.parseDouble(in.nextLine());
			System.out.print(" V11 (Y/N)? ");
			String v11s = in.nextLine();
			boolean v11 = v11s.equalsIgnoreCase("Y");
			System.out.print(" Include tables where existing RI would have to be dropped to enable archiving (Y/N)? ");
			String iris = in.nextLine();
			boolean iri = iris.equalsIgnoreCase("Y");
		
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			
			Connection con = con = DriverManager.getConnection(
					"jdbc:db2://" + host + ":" + port + "/" + location,
					user,
					pwd);
			
			System.out.println("Connected!");
		
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println("Searching for partitions that are eligible for archiving...");
			ResultSet rs = null;
			if (v11)
			{
				String sql = "SELECT SUM(CAST(B.SPACEF AS BIGINT)) AS KBYTES, C.CREATOR, C.NAME FROM SYSIBM.SYSTABLESPACESTATS A,  SYSIBM.SYSTABLEPART B, SYSIBM.SYSTABLES C, SYSIBM.SYSTABLESPACE D WHERE ";
				if (!iri)
				{
					sql += "C.CHILDREN = 0 AND ";
				}
				
				sql += "A.DBNAME = C.DBNAME AND A.NAME = C.TSNAME AND B.SPACEF > 0 AND ((A.LASTDATACHANGE IS NOT NULL AND LASTDATACHANGE < CURRENT TIMESTAMP - " + days + " DAYS) OR UPDATESTATSTIME < CURRENT TIMESTAMP - " + days + " DAYS) AND A.DBNAME = B.DBNAME AND A.NAME = B.TSNAME AND A.PARTITION = B.PARTITION AND A.DBNAME = D.DBNAME AND A.NAME = D.NAME AND D.PARTITIONS > 0 AND NOT D.TYPE = 'G' AND NOT D.TYPE = 'O' AND NOT D.TYPE = 'P' AND C.TYPE = 'T' GROUP BY C.CREATOR, C.NAME";
				rs = stmt.executeQuery(sql);
			}
			else
			{
				String sql = "SELECT SUM(CAST(B.SPACEF AS BIGINT)) AS KBYTES, C.CREATOR, C.NAME FROM SYSIBM.SYSTABLESPACESTATS A,  SYSIBM.SYSTABLEPART B, SYSIBM.SYSTABLES C, SYSIBM.SYSTABLESPACE D WHERE ";
				if (!iri)
				{
					sql += "C.CHILDREN = 0 AND ";
				}
				
				sql += "A.DBNAME = C.DBNAME AND A.NAME = C.TSNAME AND B.SPACEF > 0 AND UPDATESTATSTIME < CURRENT TIMESTAMP - " + days + " DAYS AND A.DBNAME = B.DBNAME AND A.NAME = B.TSNAME AND A.PARTITION = B.PARTITION AND A.DBNAME = D.DBNAME AND A.NAME = D.NAME AND D.PARTITIONS > 0 AND NOT D.TYPE = 'G' AND NOT D.TYPE = 'O' AND NOT D.TYPE = 'P' AND C.TYPE = 'T' GROUP BY C.CREATOR, C.NAME";
				rs = stmt.executeQuery(sql);
			}
			int i = 0;
			while (rs.next())
			{
				long kBytes = rs.getLong(1);
				String schema = rs.getString(2);
				String table = rs.getString(3);
				map.put(schema + "." + table, kBytes);
			}
			
			rs.close();

			String sql = "SELECT SUM(CAST(B.SPACEF AS BIGINT)) AS KBYTES, C.CREATOR, C.NAME FROM SYSIBM.SYSTABLESPACESTATS A, SYSIBM.SYSTABLEPART B, SYSIBM.SYSTABLES C, SYSIBM.SYSTABLESPACE D, (SELECT F.DBNAME, F.TSNAME, F.DSNUM AS PARTNUM FROM (SELECT DBNAME, TSNAME, DSNUM, MAX(\"TIMESTAMP\") AS LASTCOPYTIME FROM SYSIBM.SYSCOPY WHERE DSNUM > 0 GROUP BY DBNAME, TSNAME, DSNUM) E, SYSIBM.SYSCOPY F WHERE E.DBNAME = F.DBNAME AND E.TSNAME = F.TSNAME AND E.DSNUM = F.DSNUM AND E.LASTCOPYTIME = F.\"TIMESTAMP\" AND F.ICTYPE = 'I' AND E.LASTCOPYTIME < CURRENT TIMESTAMP - " + days + " DAYS) G WHERE ";
			if (!iri)
			{
				sql += "C.CHILDREN = 0 AND ";
			}
			
			sql += "A.DBNAME = C.DBNAME AND A.NAME = C.TSNAME AND B.SPACEF > 0 AND A.DBNAME = B.DBNAME AND A.DBNAME = G.DBNAME AND A.NAME = B.TSNAME AND A.NAME = G.TSNAME AND A.PARTITION = B.PARTITION AND A.PARTITION = G.PARTNUM AND A.DBNAME = D.DBNAME AND A.NAME = D.NAME AND D.PARTITIONS > 0 AND NOT D.TYPE = 'G' AND NOT D.TYPE = 'O' AND NOT D.TYPE = 'P' AND C.TYPE = 'T' GROUP BY C.CREATOR, C.NAME";
			rs = stmt.executeQuery(sql);
			i = 0;
			while (rs.next())
			{
				long kBytes = rs.getLong(1);
				String schema = rs.getString(2);
				String table = rs.getString(3);
				map.put(schema + "." + table, kBytes);
			}
			
			rs.close();
			
			//get total size for these tables and figure out what percentage can be archived
			//get total size for all indexes on each table and estimate that we can save that percentage of each index
			System.out.println("Gathering DASD usage information...");
			final int limit = map.size();
			int j = 0;
			boolean p10, p20, p30, p40, p50, p60, p70, p80, p90;
			p10 = false;
			p20 = false;
			p30 = false;
			p40 = false;
			p50 = false;
			p60 = false;
			p70 = false;
			p80 = false;
			p90 = false;
			
			for (String fTable : map.keySet())
			{
				int index = fTable.indexOf('.');
				String schema = fTable.substring(0, index);
				String table = fTable.substring(index+1);
				rs = stmt.executeQuery("SELECT CAST(SPACEF AS BIGINT) AS KBYTES FROM SYSIBM.SYSTABLES WHERE CREATOR = '" + schema + "' AND NAME = '" + table + "'");
				rs.next();
				long kBytes = rs.getLong(1);
				rs.close();
				long part = map.get(fTable);
				if (part < kBytes)
				{
					ArrayList<String> indexes = new ArrayList<String>();
					double percent = part * 1.0 / kBytes;
					rs = stmt.executeQuery("SELECT SUM(CAST(SPACEF AS BIGINT)) AS KBYTES, CREATOR, NAME FROM SYSIBM.SYSINDEXES WHERE SPACEF > 0 AND TBCREATOR = '" + schema + "' AND TBNAME = '" + table + "' GROUP BY CREATOR, NAME");
					while (rs.next())
					{
						long indexKB = rs.getLong(1);
						String indexSchema = rs.getString(2);
						String indexName = rs.getString(3);
						map2.put(indexSchema + "." + indexName, (long)(percent * indexKB));
						indexes.add(indexSchema + "." + indexName);
					}
					
					rs.close();
					
					if (indexes.size() > 0)
					{
						table2Index.put(fTable, indexes);
					}
				}
				
				j++;
				
				double p = j * 1.0 / limit;
				if (!p10 && p > 0.1)
				{
					p10 = true;
					System.out.println("10%");
				}
				
				if (!p20  && p > 0.2)
				{
					p20 = true;
					System.out.println("20%");
				}
				
				if (!p30 && p > 0.3)
				{
					p30 = true;
					System.out.println("30%");
				}
				
				if (!p40 && p > 0.4)
				{
					p40 = true;
					System.out.println("40%");
				}
				
				if (!p50 && p > 0.5)
				{
					p50 = true;
					System.out.println("50%");
				}
				
				if (!p60 && p > 0.6)
				{
					p60 = true;
					System.out.println("60%");
				}
				
				if (!p70 && p > 0.7)
				{
					p70 = true;
					System.out.println("70%");
				}
				
				if (!p80 && p > 0.8)
				{
					p80 = true;
					System.out.println("80%");
				}
				
				if (!p90 && p > 0.9)
				{
					p90 = true;
					System.out.println("90%");
				}
			}
			
			System.out.println("100%");
			System.out.println("Writing report...");
			writeReport(map, map2, table2Index, out, cost);
			
			out.close();
			con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void writeReport(HashMap<String, Long> map, HashMap<String, Long> map2, HashMap<String, ArrayList<String>> table2Index, PrintWriter out, double cost)
	{
		out.println("HPSS Estimated Savings Report");
		out.println("");
		out.println("Total summary:");
		lineBreak(out);
		long total = 0;
		long tableTotal = 0;
		long indexTotal = 0;
		for (long kBytes : map.values())
		{
			total += kBytes;
		}
		
		tableTotal = total;
		for (long kBytes : map2.values())
		{
			indexTotal += kBytes;
		}
		
		total += indexTotal;
		out.println("Total savings: " + printSize(total * 1024l) + " = $" + (long)(cost * total / (1024.0 * 1024.0)));
		out.println("Table savings: " + printSize(tableTotal * 1024l) + " = $" + (long)(cost * tableTotal / (1024.0 * 1024.0)));
		out.println("Index savings: " + printSize(indexTotal * 1024l) + " = $" + (long)(cost * indexTotal / (1024.0 * 1024.0)));
		out.println("");
		out.println("Summarized by table schema:");
		lineBreak(out);
		
		HashMap<String, Long> tableSavings = new HashMap<String, Long>();
		HashMap<String, Long> indexSavings = new HashMap<String, Long>();
		
		for (String fTable : map.keySet())
		{
			String schema = fTable.substring(0, fTable.indexOf('.'));
			Long current = tableSavings.get(schema);
			if (current == null)
			{
				tableSavings.put(schema, map.get(fTable));
			}
			else
			{
				tableSavings.put(schema, map.get(fTable) + current);
			}
			
			ArrayList<String> indexes = table2Index.get(fTable);
			total = 0;
			if (indexes != null)
			{
				for (String index : indexes)
				{
					total += map2.get(index);
				}
			
				current = indexSavings.get(schema);
				if (current == null)
				{
					indexSavings.put(schema, total);
				}
				else
				{
					indexSavings.put(schema, total + current);
				}
			}
		}
			
		ArrayList<String> sortedSchema = new ArrayList<String>(tableSavings.keySet());
		Collections.sort(sortedSchema);
		for (String schema2 : sortedSchema)
		{
			tableTotal = tableSavings.get(schema2);
			Long it = indexSavings.get(schema2);
			if (it == null)
			{
				indexTotal = 0;
			}
			else
			{
				indexTotal = it;
			}
				
			total = tableTotal + indexTotal;
			out.println("Schema " + schema2);
			out.println("\tTotal savings: " + printSize(total * 1024l) + " = $" + (long)(cost * total / (1024.0 * 1024.0)));
			out.println("\tTable savings: " + printSize(tableTotal * 1024l) + " = $" + (long)(cost * tableTotal / (1024.0 * 1024.0)));
			out.println("\tIndex savings: " + printSize(indexTotal * 1024l) + " = $" + (long)(cost * indexTotal / (1024.0 * 1024.0)));
			out.println("");
		}
			
		out.println("");
		out.println("Report details:");
		lineBreak(out);
			
		ArrayList<String> sortedTables = new ArrayList<String>(map.keySet());
		Collections.sort(sortedTables);
		for (String table : sortedTables)
		{
			tableTotal = map.get(table);
			out.println("Table " + table);
			out.println("\tTable savings: " + printSize(tableTotal * 1024l) + " = $" + (long)(cost * tableTotal / (1024.0 * 1024.0)));
			ArrayList<String> indexes = table2Index.get(table);
			if (indexes != null)
			{
				Collections.sort(indexes);
				for (String index : indexes)
				{
					out.println("\t\tIndex " + index);
					indexTotal = map2.get(index);
					out.println("\t\t\tIndex savings: " + printSize(indexTotal * 1024l) + " = $" + (long)(cost * indexTotal / (1024.0 * 1024.0)));
				}
			}
		}
	}
	
	private static void lineBreak(PrintWriter out)
	{
		out.println("--------------------------------------------------------------------");;
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
