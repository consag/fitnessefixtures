package nl.consag.testautomation.supporting;

import java.net.*;
import java.io.*;
import java.lang.*;


/************************  ** * * * * * * * * * * * * ** * * * *******************/


class HttpListener 
{

/*****************Variable declaration*************************/
	static Socket Soc[]=new Socket[500];
    static boolean running_thread[]=new boolean[500];
	static messageReceiver mReceiver[]=new messageReceiver[500];
    static Socket t_soc;
	public static int messageNo=0;
	public static int port=9893;
    static ServerSocket SSoc=null,send_ssoc=null,off_ssoc=null; 
/**************************************************************/

	HttpListener()/* HttpListener constructor*/
	{
	}
	public static void main(String[] args) 
	{
  		try{
		      
			  if(!(args.length==0))
					port=java.lang.Integer.parseInt(args[0]);			  
			  else
				{
	  			  System.out.println("No port defined :: usage:: java HttpListener <port no>");
				  System.out.println("Setting the default port::"+port);
				}
			  SSoc=new ServerSocket(port);/*Create a server soc*/
  			  System.out.println("Listener started on port "+port);
   		}
		catch(BindException be)
		{
			System.out.println("Please change the port number::"+be);
			System.out.println("usage::==>java HttpListener <port no>");
			System.exit(0);
		}

   		catch(Exception e){
			System.err.println("could not initialise server socket:: "+e);
			System.exit(0);
		}
    	
		while(true)
		{
			try{
				t_soc=SSoc.accept();
				messageNo++;
				System.out.println("one more request comes to socket.."+t_soc);
			}
			catch(Exception e)
			{
				System.out.println("Error alloting socket to client::"+e);
			}
			mReceiver[messageNo]=new messageReceiver(t_soc);
			//start_receiver_thread(Soc[messageNo]);
			mReceiver[messageNo].start();
		}
	}


	/************************Start a new thread**************************************/
	public static void start_receiver_thread(Socket soc)
	{
		messageReceiver receiver=new messageReceiver(soc);
		receiver.start();
	}/*End start_receiver_thread function*/
	
}

/************************Extending Thread class**************************************************/
class messageReceiver extends Thread
{

/**************************************/
	public DataInputStream is=null;
	public PrintStream ps=null;
	public BufferedReader d=null;
	Socket socket=null;
	static byte b[]=null;
    String message="";
	boolean newLineflg1=false;
	String messageHeader=null;
	String contentLength=null;
	String line="";
	int data,count,dataLen=-7;
	String msg="";
/**************************************/

	public messageReceiver(Socket soc)
	{
		super();
		socket=soc;
		System.out.println("Receiver Started....");
		try{is=new DataInputStream(soc.getInputStream());
			ps=new PrintStream(soc.getOutputStream());
			d= new BufferedReader(new InputStreamReader(soc.getInputStream()));

		}catch(Exception e){
			    System.out.println("Error opening Streams:: "+e);
		}
	}
	
	public void run()
	{
            boolean wsdlFound=false;
            String wsdlLine=null;
	  try{
		while(is.available()==0){
			System.out.println("Sleeping...");
			sleep(360);
		}
		try{
			System.out.println("Inside run method");
			 count=0;
			while((data=d.read())!=-1 && !wsdlFound)
			{
		 	 if(((data)==13)&&(newLineflg1)) 
				{
				  newLineflg1=false;
				  count=0;
				  messageHeader=msg;
				  msg="";
				}
			 if(((char)data)=='\n')
				{
				 newLineflg1=true;
				     System.out.println("Header >" +messageHeader+"<.");
                                 if(line.indexOf("Content-Length")!=-1)
					{
					  contentLength=line.substring(line.indexOf(":")+1);
					  Integer len=new Integer(contentLength.trim());
					  dataLen=len.intValue();
					}
                                 if(line.indexOf("GET /wsh/services/BatchServices/DataIntegration?WSDL") != -1) {
                                     wsdlLine=line;
                                     wsdlFound=true;
                                 }
				     if(line.indexOf("GET /wsh/services/BatchServices/Metadata?WSDL") != -1) {
				         wsdlLine=line;
                                         wsdlFound=true;
				     }
	 			 msg=msg+line;
		//		 System.out.println(line);
				 line="";
				 }
			   else
				{
			     newLineflg1=false;
				}
				line+=""+(char)data;
 			 count=count+1;
				 if(count==(dataLen+2))
					 break;
			}
		    System.out.println("Sending response........................");
  		    //ps.println ("Message Received "+ count +"len"+dataLen);
			System.out.println ("Message Received >"+ count +"< len >"+dataLen +"<.");
			socket.close();
		 //System.out.println("Data recieved ::"+msg+line);
                    
                    String fileName="InvalidGet";
            if(wsdlLine.contains("DataIntegration?WSDL")) {
                fileName="wsdl/DataIntegrationService.wsdl";
            } else if(wsdlLine.contains("Metadata?WSDL")) {
                fileName="wsdl/MetadataService.wsdl";
            }
                    ps.println("Content-Type: XHTML 1.0 Transitional=<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
                        "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            try {
                 File file=new File(fileName);
                String fileLine="";
	     BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    while ((fileLine = in.readLine()) != null) {
                ps.println(fileLine);
//                        System.out.println(fileLine);
                }

             in.close();
            }catch(Exception e) {
                System.out.println("Error reading from file >" + fileName +"<. Error: " +e.toString());
            }
                }catch(Exception ex){System.err.println("Error reading data from input stream ::"+ex);}	
	  }
	  catch(Exception e)
	  {
	  	System.err.println("Error occured in sleep ::"+e);
	  }
			
	}
}
/***************************************************************************************************/
