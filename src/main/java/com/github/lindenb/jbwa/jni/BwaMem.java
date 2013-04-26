package com.github.lindenb.jbwa.jni;
import java.io.*;

public class BwaMem	
	{
	protected long ref=0L;
	private BwaIndex bwaIndex=null;
	public BwaMem(BwaIndex bwaIndex)
		{
		this.ref=BwaMem.mem_opt_init();
		this.bwaIndex=bwaIndex;
		}
	
	public AlnRgn[] align(ShortRead read) throws IOException
		{
		if(ref==0L) return null;
		return align(this.bwaIndex,read.getBases());
		}
	
	@Override
	protected void finalize()
		{
		dispose();
		}
	
	public native void dispose();
	
	private static native long mem_opt_init();
	private native AlnRgn[] align(BwaIndex bwaIndex,byte bases[])  throws IOException;
	}
