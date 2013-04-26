package com.github.lindenb.jbwa.ws.client;
import java.util.zip.GZIPInputStream;
import java.util.regex.Pattern;
import javax.xml.bind.*;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;
import javax.xml.stream.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BWAServiceClient
	{
	private static final Logger LOG=Logger.getLogger("bwaserviceclient");
	private BWAService bwaService=null;
	private Marshaller marshaller;
	private XMLStreamWriter writer;
	public BWAServiceClient()
		{
		
		}
	
	private void run(BufferedReader in) throws java.lang.Exception
		{
		String name=null;
		String seq=null;
		String line;
		int nLine=0;
		while((line=in.readLine())!=null)
			{
			switch(nLine%4)
				{
				case 0: name=line.substring(1);break;
				case 1: {
					seq=line;
					for(Alignment o:bwaService.align(name,seq))
						{
						marshaller.marshal(new JAXBElement<Alignment>(new QName("Alignment"),Alignment.class,o),this.writer);
						}
					
					break;
					}
				default:break;
				}
			nLine++;
			}
		}
	
	private void run(String[] args) throws java.lang.Exception
		{
		int optind=0;
		while(optind<args.length)
			{
			if(args[optind].equals("-h"))
				{
				System.out.println("Pierre Lindenbaum PhD. 2013.");
				System.out.println("Options:");
				System.out.println(" -L  <level> one value from "+Level.class.getName()+
					" default:"+LOG.getLevel());
				
				return;
				}
			
			else if(args[optind].equals("-L") && optind+1 <args.length)
				{
				LOG.setLevel(Level.parse(args[++optind]));
				}
			else if(args[optind].equals("--"))
				{
				optind++;
				break;
				}
			else if(args[optind].startsWith("-"))
				{
				System.err.println("Unnown option: "+args[optind]);
				return;
				}
			else
				{
				break;
				}
			++optind;
			}
		this.bwaService=new BWAServiceImplService().getBWAServiceImplPort();
		JAXBContext jaxbContext = JAXBContext.newInstance("com.github.lindenb.jbwa.ws.client");
		this.marshaller = jaxbContext.createMarshaller();
		this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		this.marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		
		javax.xml.stream.XMLOutputFactory xmlfactory= javax.xml.stream.XMLOutputFactory.newInstance();
		xmlfactory.setProperty(javax.xml.stream.XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
		this.writer= xmlfactory.createXMLStreamWriter(System.out,"UTF-8");
		this.writer.writeStartDocument("UTF-8","1.0");
		this.writer.writeStartElement("bwa-service");
		this.writer.writeAttribute("reference", this.bwaService.getReferenceName());
		if(optind==args.length)
			{
			this.run(new BufferedReader(new InputStreamReader(System.in)));
			}
		else while(optind < args.length)
        		   {
        		   
			        File file=new File(args[optind++]);
			        LOG.info("reading from "+file);
			        InputStream in=new FileInputStream(file);
			        if(file.getName().toLowerCase().endsWith(".gz"))
			        	{
			        	LOG.info("unzipping");
			        	in=new GZIPInputStream(in);
			   	     	}
			   
			      	this.run(new BufferedReader(new InputStreamReader(in)));
			        in.close();
        		   }
        

		this.writer.writeEndElement();
		this.writer.writeEndDocument();
		this.writer.flush();
		this.writer.close();
		LOG.info("done");
		}
	
	public static void main(String[] args) throws java.lang.Exception
		{
		new BWAServiceClient().run(args);
		}
	} 
