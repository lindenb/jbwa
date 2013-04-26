include make.properties
CC ?= gcc
JAVAC ?= java
BWAJNIQUALPACKAGE=com.github.lindenb.jbwa.jni
JAVASRCDIR=src/main/java
JAVACLASSNAME= Example BwaIndex BwaMem KSeq ShortRead AlnRgn BwaFrame
JAVACLASSSRC=$(addprefix src/main/java/com/github/lindenb/jbwa/jni/,$(addsuffix .java,$(JAVACLASSNAME)))
JAVAQUALNAME=$(addprefix ${BWAJNIQUALPACKAGE}.,$(JAVACLASSNAME))
BWAOBJS= utils.o kstring.o ksw.o bwt.o bntseq.o bwa.o bwamem.o bwamem_pair.o
native.dir=src/main/native

CC=gcc
.PHONY:all compile test1 test2

all:test1

test1:${native.dir}/libbwajni.so
	gunzip -c $(FASTQ) | head -n 4000 | java  -Djava.library.path=${native.dir} -cp ${JAVASRCDIR} ${BWAJNIQUALPACKAGE}.Example $(REF) -| tail 
	gunzip -c $(FASTQ) | head -n 4000 | $(BWA.dir)/bwamem-lite $(REF) - | tail 

test2:${native.dir}/libbwajni.so
	java  -Djava.library.path=${native.dir} bwa.BwaFrame $(REF)
	
${native.dir}/libbwajni.so : ${native.dir}/bwajni.o ${native.dir}/libbwa2.a
	$(CC) -shared -o $@ $<  -L ${native.dir} -lbwa2 -lm -lz -lpthread

${native.dir}/bwajni.o: ${native.dir}/bwajni.c ${native.dir}/bwajni.h
	$(CC) -c -Wall -O3 -o $@ $(CFLAGS) -fPIC  -I/java/include -I/java/include/solaris  -I $(BWA.dir) $<

${native.dir}/libbwa2.a:  $(foreach C,${BWAOBJS}, ${BWA.dir}/$(patsubst %.o,%.c,${C}) )
	 $(foreach C,${BWAOBJS}, $(CC) -o ${native.dir}/${C} -g -Wall -c -fPIC -I $(BWA.dir) ${BWA.dir}/$(patsubst %.o,%.c,${C});)
	 ar  rcs $@  $(foreach C,${BWAOBJS}, ${native.dir}/${C} )

${native.dir}/bwajni.h : compile
	javah -o $@ -jni -classpath ${JAVASRCDIR} $(JAVAQUALNAME)

compile: $(JAVACLASSSRC)
	javac -sourcepath ${JAVASRCDIR} -d ${JAVASRCDIR} $^

clean:
	rm -f ${native.dir}/*.a ${native.dir}/*.o ${native.dir}/*.so
