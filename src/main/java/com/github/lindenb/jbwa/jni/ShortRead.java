package com.github.lindenb.jbwa.jni;

public class ShortRead	
	{
	private String name;
	private byte[] seq;
	private byte[] qual;
	
	public ShortRead(String name,byte[] seq,byte[] qual)
		{
		this.name=name;
		this.seq=seq;
		this.qual=qual;
		}
	
	public String getName()
		{
		return this.name;
		}
	
	public byte[] getBases()
		{
		return this.seq;
		}
		
	public byte[] getQualities()
		{
		return this.qual;
		}
	
	@Override
	public String toString()
		{
		return "@"+name+"\n"+new String(this.seq)+"\n+\n"+new String(qual);
		}
	}
