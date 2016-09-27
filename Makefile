SHELL=/bin/bash
CC ?= gcc
JAVA ?= java
JAVAC ?= javac
JAVAH ?= javah
CFLAGS=-O3 -Wall  -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE
BWAJNIQUALPACKAGE=com.github.lindenb.jbwa.jni
JAVASRCDIR=src/main/java
JAVACLASSNAME= Example Example2 ExampleSEChangeMemOpt BwaIndex BwaMem KSeq ShortRead AlnRgn BwaFrame
JAVACLASSSRC=$(addprefix src/main/java/com/github/lindenb/jbwa/jni/,$(addsuffix .java,$(JAVACLASSNAME)))
JAVAQUALNAME=$(addprefix ${BWAJNIQUALPACKAGE}.,$(JAVACLASSNAME))
JAR=jbwa.jar
NATIVETARFILE=jbwa-native.tar
BWAOBJS= utils.o kstring.o ksw.o bwt.o bntseq.o bwa.o bwamem.o bwamem_pair.o kthread.o bwamem_extra.o
TESTDIR=test
REF=${TESTDIR}/ref.fa
FASTQ1=${TESTDIR}/R1.fq
FASTQ2=${TESTDIR}/R2.fq
FASTQ=${FASTQ1}
OSNAME=$(shell uname)



ifeq (${JAVA_HOME},)
$(error $${JAVA_HOME} is not defined)
endif

## find path where to find include files
JDK_JNI_INCLUDES?=$(addprefix -I,$(sort $(dir $(shell find ${JAVA_HOME}/include -type f -name "*.h"))))


ifeq (${JDK_JNI_INCLUDES},)
$(error Cannot find C header files under $${JAVA_HOME})
endif


# my C source code path
native.dir=src/main/native

## see https://github.com/lindenb/jbwa/pull/5
ifeq (${OSNAME},Darwin)
native.extension=jnilib
else
native.extension=so
endif

#bwa version (apache2)
BWA.version?=8e2da1e407972170d1a660286f07a3a3a71ee6fb

#path to a Reference genome (testing)
REF?=human_g1k_v37.fasta
#path to a gzipped fastq file (testing)
FASTQ?=file.fastq.gz

CC?=gcc
.PHONY:all compile jar tar test.cmdline.simple test.cmdline.double test.cmdline.simpleOpt test.gui test.ws test .ws.client test.ws.server clean

all: jar tar test.cmdline.simple test.cmdline.double test.cmdline.simpleOpt

test.ws: test.ws.server

#compile and publish a WebService
test.ws.server: compile ${native.dir}/libbwajni.${native.extension}
	javac  -sourcepath ${JAVASRCDIR} -d ${JAVASRCDIR} ${JAVASRCDIR}/com/github/lindenb/jbwa/ws/server/BWAServiceImpl.java
	wsgen -keep -d ${JAVASRCDIR} -cp ${JAVASRCDIR} com.github.lindenb.jbwa.ws.server.BWAServiceImpl
	$(JAVA)   -Djava.library.path=${native.dir} -cp ${JAVASRCDIR} com.github.lindenb.jbwa.ws.server.BWAServiceImpl -R $(REF) -p 8081

#create a client from the WSDL file of the server
test.ws.client:
	mkdir  -p tmp
	wsimport -keep -d tmp -p com.github.lindenb.jbwa.ws.client "http://localhost:8081/?wsdl"
	$(JAVAC) -d tmp -sourcepath tmp:${JAVASRCDIR} ${JAVASRCDIR}/com/github/lindenb/jbwa/ws/client/BWAServiceClient.java
	gunzip -c $(FASTQ) | head -n 8 | java  -cp tmp  com.github.lindenb.jbwa.ws.client.BWAServiceClient | xmllint --format - 
	rm -rf tmp

test.cmdline.simple :${native.dir}/libbwajni.${native.extension} ${REF}.bwt
	echo "TEST BWA/JNI:"
	java  -Djava.library.path=${native.dir} -cp ${JAVASRCDIR} ${BWAJNIQUALPACKAGE}.Example $(REF) $(FASTQ) | tail 
	echo "TEST BWA/NATIVE:"
	bwa-${BWA.version}/bwamem-lite ${REF} $FASTQ | tail 

test.cmdline.simpleOpt :${native.dir}/libbwajni.${native.extension} ${REF}.bwt
	echo "TEST BWA/JNI modify mem_opt:"
	(gunzip -c $(FASTQ) | cat ${FASTQ}) | java  -Djava.library.path=${native.dir} -cp ${JAVASRCDIR} ${BWAJNIQUALPACKAGE}.ExampleSEChangeMemOpt $(REF) - > ${TESTDIR}/ExampleSEChangeMemOptTestOutput.txt

test.cmdline.double :${native.dir}/libbwajni.${native.extension} ${REF}.bwt
	echo "TEST BWA/JNI:"
	java  -Djava.library.path=${native.dir} -cp ${JAVASRCDIR} ${BWAJNIQUALPACKAGE}.Example2 $(REF)  ${FASTQ1} ${FASTQ2}
	echo "TEST BWA/NATIVE:"
	bwa-${BWA.version}/bwa mem $(REF) ${FASTQ1} ${FASTQ2} 2> /dev/null | grep -v -E '^@'

test.gui:${native.dir}/libbwajni.${native.extension}
	$(JAVA)  -Djava.library.path=${native.dir}  -cp ${JAVASRCDIR} ${BWAJNIQUALPACKAGE}.BwaFrame $(REF)


${REF}.bwt: ${REF} bwa-${BWA.version}/libbwa.a
	bwa-${BWA.version}/bwa index $<

#create a shared dynamic library for BWA
${native.dir}/libbwajni.${native.extension} : ${native.dir}/bwajni.o ${native.dir}/libbwa2.a
	$(CC) -dynamiclib -shared -o $@ $<  -L ${native.dir} -lbwa2 -lm -lz -lpthread

#compile the JNI bindings
${native.dir}/bwajni.o: ${native.dir}/bwajni.c ${native.dir}/bwajni.h bwa-${BWA.version}/libbwa.a
	$(CC) -c $(CFLAGS) -o $@ $(CFLAGS) -fPIC  ${JDK_JNI_INCLUDES}  -I bwa-${BWA.version} $<

#libbwa must be recompiled with fPIC to create a dynamic library.
${native.dir}/libbwa2.a:  bwa-${BWA.version}/libbwa.a
	 $(foreach C,${BWAOBJS}, $(CC) -o ${native.dir}/${C} $(CFLAGS) -c -fPIC -I bwa-${BWA.version} bwa-${BWA.version}/$(patsubst %.o,%.c,${C});)
	 ar  rcs $@  $(foreach C,${BWAOBJS}, ${native.dir}/${C} )

#create JNI header
${native.dir}/bwajni.h : compile
	$(JAVAH) -o $@ -jni -classpath ${JAVASRCDIR} $(JAVAQUALNAME)
	
#compile java classes
compile: $(JAVACLASSSRC)
	$(JAVAC) -sourcepath ${JAVASRCDIR} -d ${JAVASRCDIR} $^

#create a JAR
jar: ${JAVASRCDIR}
	jar cvf ${JAR} -C ${JAVASRCDIR} .

#create a tar of the native libraries
tar: ${native.dir}
	tar cf ${NATIVETARFILE} -C ${native.dir} .

arch=$(shell uname -m)
ifeq ($(arch),ppc64le)
bwa-${BWA.version}/libbwa.a :
	rm -rf "${BWA.version}.zip" "bwa-${BWA.version}"
	wget -O "${BWA.version}.zip"  "https://github.com/lh3/bwa/archive/${BWA.version}.zip"
	unzip -o "${BWA.version}.zip" && patch -p6 < "bwaPPC64.patch" && (cd "bwa-${BWA.version}" && ${MAKE} ) && rm -f "${BWA.version}.zip"
else
bwa-${BWA.version}/libbwa.a :
	rm -rf "${BWA.version}.zip" "bwa-${BWA.version}"
	wget -O "${BWA.version}.zip"  "https://github.com/lh3/bwa/archive/${BWA.version}.zip"
	unzip -o "${BWA.version}.zip" && (cd "bwa-${BWA.version}" && ${MAKE} ) && rm -f "${BWA.version}.zip"
endif
clean:
	rm -rf ${native.dir}/*.a ${native.dir}/*.o ${native.dir}/*.${native.extension} bwa-${BWA.version} "${BWA.version}.zip"
	find ${JAVASRCDIR} -type f -name "*.class" -exec rm '{}' ';'
	rm ${JAR}
