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
package com.github.lindenb.jbwa.ws.server;
import javax.xml.bind.annotation.*;


@XmlRootElement(name="Alignment")
public class Alignment
implements java.io.Serializable
	 {
	private String name=null;
	private String sequence=null;
	private String chrom=null;
	private int position=0;
	private char strand;
	private String cigar;
	private int mqual;
	private int NM;
	private int secondary;

	public Alignment()
		{
		}
	public String getReadName() { return this.name; }
	public void setReadName(String name) { this.name=name; }
	public String getReadBases() { return this.sequence; }
	public void setReadBases(String sequence) { this.sequence=sequence; }
	public String getChrom() { return this.chrom; }
	public void setChrom(String chrom) { this.chrom=chrom; }
	public int getPosition() { return this.position; }
	public void setPosition(int position) { this.position=position; }
	public char getStrand() { return this.strand;}
	public void setStrand(char strand) { this.strand=strand; }
	public String getCigar() { return this.cigar;}
	public void setCigar(String cigar) { this.cigar=cigar; }
	public int getMQual() { return this.mqual;}
	public void setMQual(int mqual) { this.mqual=mqual; }
	public int getNm() { return this.NM;}
	public void setNm(int NM) { this.NM=NM; }
	public int getSecondary() { return this.secondary;}
	public void setSecondary(int secondary) { this.secondary=secondary;}
	}
