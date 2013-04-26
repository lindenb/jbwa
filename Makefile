include make.properties
CC ?= gcc
JAVA ?= java
JAVAC ?= javac
JAVAH ?= javah
CFLAGS=-O3 -Wall  -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE
BWAJNIQUALPACKAGE=com.github.lindenb.jbwa.jni
JAVASRCDIR=src/main/java
JAVACLASSNAME= Example BwaIndex BwaMem KSeq ShortRead AlnRgn BwaFrame
JAVACLASSSRC=$(addprefix src/main/java/com/github/lindenb/jbwa/jni/,$(addsuffix .java,$(JAVACLASSNAME)))
JAVAQUALNAME=$(addprefix ${BWAJNIQUALPACKAGE}.,$(JAVACLASSNAME))
BWAOBJS= utils.o kstring.o ksw.o bwt.o bntseq.o bwa.o bwamem.o bwamem_pair.o
native.dir=src/main/native
#path to bwa directory
BWA.dir?=bwa-0.7.4
#path to a Reference genome (testing)
REF?=human_g1k_v37.fasta
#path to a gzipped fastq file (testing)
FASTQ?=file.fastq.gz

CC=gcc
.PHONY:all compile test.cmdline test.gui test.ws test.ws.client test.ws.server clean 

all:test.cmdline

test.ws: test.ws.server

test.ws.server: compile
	javac  -sourcepath ${JAVASRCDIR} -d ${JAVASRCDIR} ${JAVASRCDIR}/com/github/lindenb/jbwa/ws/server/BWAServiceImpl.java
	wsgen -verbose -keep -d ${JAVASRCDIR} -cp ${JAVASRCDIR} com.github.lindenb.jbwa.ws.server.BWAServiceImpl
	java -cp ${JAVASRCDIR} com.github.lindenb.jbwa.ws.server.BWAServiceImpl -R $(REF)

test.cmdline:${native.dir}/libbwajni.so
	echo "TEST BWA/JNI:"
	gunzip -c $(FASTQ) | head -n 4000 | java  -Djava.library.path=${native.dir} -cp ${JAVASRCDIR} ${BWAJNIQUALPACKAGE}.Example $(REF) -| tail 
	echo "TEST BWA/NATIVE:"
	gunzip -c $(FASTQ) | head -n 4000 | $(BWA.dir)/bwamem-lite $(REF) - | tail 

test.gui:${native.dir}/libbwajni.so
	$(JAVA)  -Djava.library.path=${native.dir}  -cp ${JAVASRCDIR} ${BWAJNIQUALPACKAGE}.BwaFrame $(REF)

#create a shared dynamic library for BWA
${native.dir}/libbwajni.so : ${native.dir}/bwajni.o ${native.dir}/libbwa2.a
	$(CC) -shared -o $@ $<  -L ${native.dir} -lbwa2 -lm -lz -lpthread

#compile the JNI bindings
${native.dir}/bwajni.o: ${native.dir}/bwajni.c ${native.dir}/bwajni.h
	$(CC) -c $(CFLAGS) -o $@ $(CFLAGS) -fPIC  -I/java/include -I/java/include/solaris  -I $(BWA.dir) $<

#libbwa must be recompiled with fPIC to create a dynamic library.
${native.dir}/libbwa2.a:  $(foreach C,${BWAOBJS}, ${BWA.dir}/$(patsubst %.o,%.c,${C}) )
	 $(foreach C,${BWAOBJS}, $(CC) -o ${native.dir}/${C} $(CFLAGS) -c -fPIC -I $(BWA.dir) ${BWA.dir}/$(patsubst %.o,%.c,${C});)
	 ar  rcs $@  $(foreach C,${BWAOBJS}, ${native.dir}/${C} )

#create JNI header
${native.dir}/bwajni.h : compile
	$(JAVAH) -o $@ -jni -classpath ${JAVASRCDIR} $(JAVAQUALNAME)
	
#compile java classes
compile: $(JAVACLASSSRC)
	$(JAVAC) -sourcepath ${JAVASRCDIR} -d ${JAVASRCDIR} $^

clean:
	rm -f ${native.dir}/*.a ${native.dir}/*.o ${native.dir}/*.so
	find ${JAVASRCDIR} -name "*.class" -exec rm '{}' ';'
