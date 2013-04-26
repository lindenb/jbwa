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
