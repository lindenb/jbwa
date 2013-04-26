package com.github.lindenb.jbwa.ws.server;
import javax.xml.ws.Endpoint;
import com.github.lindenb.jbwa.ws.BWAService;


@WebService
@WebService(endpointInterface="com.github.lindenb.jbwa.ws.BWAService")
public interface BWAServiceImpl
	implements BWAService
	{
	@Override
	public Alignment[] align(@WebParam(name="sequence")String sequence) throws Exception
		{
		
		}
	
	
	public static void main(String[] args)
		{
		BWAServiceImpl app=null;
		
	   	Endpoint.publish("http://localhost:8888/ws/server", service);
 
	   	System.out.println("Service is published!");
    		}
	
	}
