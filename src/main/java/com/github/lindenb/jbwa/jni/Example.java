/*
The MIT License (MIT)

Copyright (c) 2016 Pierre Lindenbaum PhD , Institut du Thorax, Nantes, France

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package com.github.lindenb.jbwa.jni;
import java.io.File;
import java.io.IOException;

public class Example	
	{
	public static void main(String args[]) throws IOException
		{
		System.loadLibrary("bwajni");
		if(args.length==0)
			{
			System.out.println("Usage [ref.fa] (stdin|fastq)\n");
			return;
			}

		BwaIndex index=new BwaIndex(new File(args[0]));
		BwaMem mem=new BwaMem(index);
		KSeq kseq=new KSeq(args.length<2 || args[1].equals("-")?null:new File(args[1]));

		ShortRead read=null;
		while((read=kseq.next())!=null)
			{
			for(AlnRgn a: mem.align(read))
				{
				if(a.getSecondary()>=0) continue;
				System.out.println(
					read.getName()+"\t"+
					a.getStrand()+"\t"+
					a.getChrom()+"\t"+
					a.getPos()+"\t"+
					a.getMQual()+"\t"+
					a.getCigar()+"\t"+
					a.getNm()
					);
				}
			}
		kseq.dispose();
		index.close();
		mem.dispose();
		}
	}

