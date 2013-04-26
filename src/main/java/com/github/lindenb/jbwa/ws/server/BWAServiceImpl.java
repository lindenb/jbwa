package com.github.lindenb.jbwa.ws.server;
import com.github.lindenb.jbwa.jni.BwaIndex;
import com.github.lindenb.jbwa.jni.BwaMem;
import com.github.lindenb.jbwa.jni.AlnRgn;
import com.github.lindenb.jbwa.jni.ShortRead;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Endpoint;
import java.io.File;
import javax.jws.WebService;

@WebService(endpointInterface="com.github.lindenb.jbwa.ws.server.BWAService")
public class BWAServiceImpl
	implements BWAService
	{
	private static final Logger LOG=Logger.getLogger("bwaservice");
	private BwaIndex bwaIndex=null;
	private File indexFile=null;
	
	public BWAServiceImpl()
		{
		}
	
	@Override
	public String getReferenceName()
		{
		return this.indexFile.getName();
		}
	
	@Override
	public Alignment[] align(String name,String sequence) throws Exception
		{
		if(sequence==null) throw new IllegalArgumentException("Empty Sequence");
		String dna= sequence.replaceAll("[ \n]","").trim().toUpperCase();
		if(dna.length()<10 || !dna.matches("[ATGNC]+"))
			{
			throw new IllegalArgumentException("Bad input "+sequence);
			}
		ShortRead read=new ShortRead("Any",dna.getBytes(),dna.replaceAll("[ANTGC]","I").getBytes());
		BwaMem mem=null;
		try
			{
			
			mem=new BwaMem(this.bwaIndex);
			AlnRgn alns[]=mem.align(read);
			Alignment ret[]=new Alignment[alns.length];
			for(int i=0;i< ret.length;++i)
				{
				AlnRgn a1=alns[i];
				Alignment a2=new Alignment();
				ret[i]=a2;
				a2.setReadName(name);
				a2.setReadBases(sequence);
				a2.setChrom(a1.getChrom());
				a2.setStrand((char)a1.getStrand());
				a2.setCigar(a1.getCigar());
				a2.setMQual(a1.getMQual());
				a2.setNm(a1.getNm());
				a2.setSecondary(a1.getSecondary());
				}
			return ret;
			}
		finally
			{
			if(mem!=null) mem.dispose();
			}
		}
	
	
	public static void main(String[] args) throws Exception
		{
		System.loadLibrary("bwajni");
		BWAServiceImpl app=new BWAServiceImpl();
		LOG.setLevel(Level.ALL);
		int optind=0;
		int port=8080;
		String path="/";
		while(optind<args.length)
			{
			if(args[optind].equals("-h"))
				{
				System.out.println("Pierre Lindenbaum PhD. 2013.");
				System.out.println("Options:");
				System.out.println(" -p (int) port default:"+port);
				System.out.println(" -R (file) reference:");
				System.out.println(" -f (path) defaut:"+path);
				System.out.println(" -L  <level> one value from "+Level.class.getName()+
					" default:"+LOG.getLevel());
				
				return;
				}
			else if(args[optind].equals("-R") && optind+1 <args.length)
				{
				app.indexFile=new File(args[++optind]);
				}
			else if(args[optind].equals("-p") && optind+1 <args.length)
				{
				port=Integer.parseInt(args[++optind]);
				}
			else if(args[optind].equals("-f") && optind+1 <args.length)
				{
				path=args[++optind];
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

		if(app.indexFile==null)
			{
			System.err.println("Reference file missing");
			return;
			}
		LOG.info("Loading index for "+app.indexFile);
		app.bwaIndex=new BwaIndex(app.indexFile);
		String url="http://localhost:"+port+path;
	   	Endpoint.publish(url, app);
	   	LOG.info("Service is published: "+url);
    		}
	
	}
