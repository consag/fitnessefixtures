package com.ibm.jason.arnold;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.KeyStore.Entry;
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

public class SnapshotKeys 
{
	private static long THRESHOLD = 75 * 10 * 1024;
	
	public static void main(String[] args)
	{
		try
		{	
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			Scanner key = new Scanner(System.in);
			
			System.out.print(" Enter DB2 hostname or IP: ");
			String host = key.nextLine();
			System.out.print(" Enter DB2 port: ");
			int port = Integer.parseInt(key.nextLine());
			System.out.print(" Enter DB2 location name: ");
			String location = key.nextLine();
			System.out.print(" Enter userid: ");
			String user = key.nextLine();
			EraserThread et = new EraserThread(" Enter password (not displayed): ");
			et.start();
			String pwd = key.nextLine();
			et.stopMasking();
			System.out.print(" Enter output file name: ");
			String file = key.nextLine();
			System.out.print(" Enter the name of the accelerator you are taking the snapshot from: ");
			String sourceName = key.nextLine();
			System.out.println(" Enter the name of the accelerator you will be replaying the snapshot on: ");
			String targetName = key.nextLine();
			
			PrintWriter out = new PrintWriter(new FileWriter(file));
			
			String conString = "jdbc:db2://" + host + ":" + port + "/" + location;
			Connection con = con = DriverManager.getConnection(conString
					,
					user,
					pwd);
				
			System.out.println("Connected to " + location);
			PreparedStatement stmt = con.prepareStatement("CALL SYSPROC.ACCEL_GET_TABLES_INFO('" + sourceName + "', ?, ?)");
			stmt.setNull(1, java.sql.Types.CHAR);
			stmt.setNull(2, java.sql.Types.CHAR);
			StringBuilder xml = new StringBuilder();
			System.out.println("Requesting table info");
			stmt.execute();
			System.out.println("Starting retrieving results");
			ResultSet rs = stmt.getResultSet();

			if (rs == null)
			{
				System.out.println("IDAA failed to return the requested information");
				System.out.println("Checking to see if DB2 has a cached copy.");
				Statement stmt2 = con.createStatement();
				try
				{
					rs = stmt2.executeQuery("SELECT TABLE_SPECIFICATION FROM DSNAQT.ACCEL_TABLES_INFO_SPEC ORDER BY SEQID");
				}
				catch(Exception e)
				{
					System.out.println("An error occured while trying to check the DB2 cache");
					e.printStackTrace();
					return;
				}
					
				if (rs == null)
				{
					System.out.println("Nothing was found in the cache either");
					return;
				}
			}
				
			while (rs.next())
			{
				xml.append(rs.getString(2));
			}
				
			System.out.println("Done retrieving results from DB2 with size " + (xml.length() / (1024*1024)) + "MB");
			int index = 0;
			int length = xml.length();
			index = xml.indexOf("<table ", index);
			int i = 0;
			while (index != -1)
			{
				int bIndex = xml.indexOf(">", index);
				if (xml.charAt(bIndex-1) == '/')
				{
					//no keys
					String spec = xml.substring(index, bIndex+1);
					out.println("CALL SYSPROC.ACCEL_ALTER_TABLES('" + targetName + "', '<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <aqttables:tableSpecifications xmlns:aqttables=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> " + spec + " </aqttables:tableSpecifications>', null);");
					index = bIndex;
				}
				else
				{
					int tClauseEnd = xml.indexOf("</table>", index);
					String spec = xml.substring(index, tClauseEnd+8);
					out.println("CALL SYSPROC.ACCEL_ALTER_TABLES('" + targetName + "', '<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <aqttables:tableSpecifications xmlns:aqttables=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> " + spec + " </aqttables:tableSpecifications>', null);");
					index = tClauseEnd;
				}
				
				if (i % 100 == 0)
				{
					System.out.println((index * 100.0 / length) + "% done");
				}
					
				i++;
					
				index = xml.indexOf("<table ", index);
			}
				
			rs.close();
			stmt.close();
			con.close();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
