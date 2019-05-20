package nl.jacbeekers.testautomation.fitnesse.IDAATools.com.ibm.jason.arnold;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class RebuildTables 
{
	public static void main(String[] args)
	{
		try
		{
			System.out.print("Input file: ");
			Scanner key = new Scanner(System.in);
			String tableFile = key.nextLine().trim();
			System.out.print("Output file: ");
			String sqlFile = key.nextLine().trim();
			System.out.print("Accelerator name: ");
			String accelName = key.nextLine().trim();
			BufferedReader in = new BufferedReader(new FileReader(tableFile));
			BufferedWriter o = new BufferedWriter(new FileWriter(sqlFile));
			PrintWriter out = new PrintWriter(o);
			String line = in.readLine();
			ArrayList<String> tables = new ArrayList<String>();
			while (line != null)
			{
				line = line.trim();
				if (!line.equals(""))
				{
					tables.add(line);
				}
				line = in.readLine();
			}
			
			StringBuffer sb = new StringBuffer();
			for (String name : tables)
			{
				if (sb.length() == 0)
				{
					sb.append("CALL SYSPROC.ACCEL_REMOVE_TABLES('" + accelName + "', ");
					sb.append("'<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <aqttables:tableSet xmlns:aqttables=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> ");
				}
				
				StringTokenizer tokens = new StringTokenizer(name, ".", false);
				String schema = tokens.nextToken();
				String table = tokens.nextToken();
				
				sb.append("<table name=\"" + table + "\" schema=\"" + schema + "\" /> ");
				
				if (sb.length() > 31000)
				{
					sb.append("</aqttables:tableSet>', null);");
					out.println(sb.toString());
					sb.setLength(0);
				}
			}
			
			if (sb.length() > 0)
			{
				sb.append("</aqttables:tableSet>', null);");
				out.println(sb.toString());
			}
			
			sb.setLength(0);
			for (String name : tables)
			{
				if (sb.length() == 0)
				{
					sb.append("CALL SYSPROC.ACCEL_ADD_TABLES('" + accelName + "', ");
					sb.append("'<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <aqttables:tableSpecifications xmlns:aqttables=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> ");
				}
				
				StringTokenizer tokens = new StringTokenizer(name, ".", false);
				String schema = tokens.nextToken();
				String table = tokens.nextToken();
				
				sb.append("<table name=\"" + table + "\" schema=\"" + schema + "\" /> ");
				
				if (sb.length() > 31000)
				{
					sb.append("</aqttables:tableSpecifications>', null);");
					out.println(sb.toString());
					sb.setLength(0);
				}
			}
			
			if (sb.length() > 0)
			{
				sb.append("</aqttables:tableSpecifications>', null);");
				out.println(sb.toString());
			}
			
			sb.setLength(0);
			for (String name : tables)
			{
				if (sb.length() == 0)
				{
					sb.append("CALL SYSPROC.ACCEL_LOAD_TABLES('" + accelName + "', 'TABLE', ");
					sb.append("'<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <aqttables:tableSetForLoad xmlns:aqttables=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> ");
				}
				
				StringTokenizer tokens = new StringTokenizer(name, ".", false);
				String schema = tokens.nextToken();
				String table = tokens.nextToken();
				
				sb.append("<table name=\"" + table + "\" schema=\"" + schema + "\" forceFullReload=\"true\"/> ");
				
				if (sb.length() > 31000)
				{
					sb.append("</aqttables:tableSetForLoad>', null);");
					out.println(sb.toString());
					sb.setLength(0);
				}
			}
			
			if (sb.length() > 0)
			{
				sb.append("</aqttables:tableSetForLoad>', null);");
				out.println(sb.toString());
			}
			
			sb.setLength(0);
			for (String name : tables)
			{
				if (sb.length() == 0)
				{
					sb.append("CALL SYSPROC.ACCEL_SET_TABLES_ACCELERATION('" + accelName + "', 'ON', ");
					sb.append("'<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <aqttables:tableSet xmlns:aqttables=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> ");
				}
				
				StringTokenizer tokens = new StringTokenizer(name, ".", false);
				String schema = tokens.nextToken();
				String table = tokens.nextToken();
					
				sb.append("<table name=\"" + table + "\" schema=\"" + schema + "\" /> ");
					
				if (sb.length() > 31000)
				{
					sb.append("</aqttables:tableSet>', null);");
					out.println(sb.toString());
					sb.setLength(0);
				}
			}
				
			if (sb.length() > 0)
			{
				sb.append("</aqttables:tableSet>', null);");
				out.println(sb.toString());
			}
			
			in.close();
			out.close();
			o.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
