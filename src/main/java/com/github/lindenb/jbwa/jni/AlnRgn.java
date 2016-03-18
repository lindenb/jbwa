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


public class AlnRgn	
	{
	private String chrom;
	private long pos;
	private byte strand;
	private String cigar;
	private int mqual;
	private int NM;
	private int secondary;
	public AlnRgn(String chrom,long pos,byte strand,String cigar,int mqual,int NM,int secondary)
		{
		this.chrom=chrom;
		this.pos=pos;
		this.strand=strand;
		this.cigar=cigar;
		this.mqual=mqual;
		this.NM=NM;
		this.secondary=secondary;
		}
	
	public String getChrom() { return this.chrom;}
	public long getPos() { return this.pos;}
	public char getStrand() { return (char)this.strand;}
	public String getCigar() { return this.cigar;}	
	public int getMQual() { return this.mqual;}	
	public int getNm() { return this.NM;}	
	public int getSecondary() { return this.secondary;}
	
	@Override
	public String toString()
		{
		return ""+chrom+":"+String.valueOf(pos)+"("+(char)this.strand+");"+cigar+";"+mqual+";"+NM+";"+getSecondary();
		}
	}
