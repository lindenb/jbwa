package com.github.lindenb.jbwa.ws.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;



@WebService
public interface BWAService
	{
	@WebMethod
	@WebResult(name="referenceName")
	public String getReferenceName();
	
	@WebMethod
	@WebResult(name="alignments")
	public Alignment[] align(
		@WebParam(name="name")String name,
		@WebParam(name="sequence")String sequence
		) throws Exception;
	}
