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
import java.util.*;

public class Example2
	{
	public static void main(String args[]) throws IOException
		{
		System.loadLibrary("bwajni");
		if(args.length!=3)
			{
			System.out.println("Usage [ref.fa] fastq1 fasta2\n");
			return;
			}

		BwaIndex index=new BwaIndex(new File(args[0]));
		BwaMem mem=new BwaMem(index);
		KSeq kseq1=new KSeq(new File(args[1]));
		KSeq kseq2=new KSeq(new File(args[2]));
	
		List<ShortRead> L1=new ArrayList<ShortRead>();
		List<ShortRead> L2=new ArrayList<ShortRead>();
		for(;;)
			{
			ShortRead read1=kseq1.next();
			ShortRead read2=kseq2.next();
			
			if(read1==null || read2==null || L1.size()>100)
				{
				if(!L1.isEmpty())
					for(String sam:mem.align(L1,L2))
						{
						System.out.print(sam);
						}
				if(read1==null || read2==null) break;
				L1.clear();
				L2.clear();
				}
			L1.add(read1);
			L2.add(read2);
			}
		kseq1.dispose();
		kseq2.dispose();
		index.close();
		mem.dispose();
		}
	}

