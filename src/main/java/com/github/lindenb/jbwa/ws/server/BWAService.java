package com.github.lindenb.jbwa.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;



@WebService
public interface BWAService
	{
	@WebMethod
	@WebResult(name="alignments")
	public Alignment[] align(@WebParam(name="sequence")String sequence) throws Exception;
	}
