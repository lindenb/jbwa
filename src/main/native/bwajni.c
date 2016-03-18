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
#include <stdlib.h>
#include <stdio.h>
#include "bwamem.h"
#include "kseq.h" // for the FASTA/Q parser
#include "bwajni.h"
KSEQ_DECLARE(gzFile)

#define QUALIFIEDMETHOD(fun) Java_com_github_lindenb_jbwa_jni_##fun
#define PACKAGEPATH "com/github/lindenb/jbwa/jni/"




#define WHERE fprintf(stderr,"[%s][%d]\n",__FILE__,__LINE__)

#define VERIFY_NOT_NULL(a) if((a)==0) do {fprintf(stderr,"Throwing error from %s %d\n",__FILE__,__LINE__); throwIOException(env,"Method returned Null");} while(0)

#define CAST_REF_OBJECT(retType,method,ref) \
static retType _get##method(JNIEnv * env, jobject self)\
	{\
	jlong ptr;jfieldID field;\
	jclass c= (*env)->GetObjectClass(env,(self));\
	VERIFY_NOT_NULL(c);\
	field = (*env)->GetFieldID(env,c, ref, "J");\
	VERIFY_NOT_NULL(field);\
	ptr= (*env)->GetLongField(env,self,field);\
	return (retType)ptr;\
	}\
static void _set##method(JNIEnv * env, jobject self,retType value)\
	{\
	jfieldID field;\
	jclass c= (*env)->GetObjectClass(env,(self));\
	VERIFY_NOT_NULL(c);\
	field = (*env)->GetFieldID(env,c, ref, "J");\
	VERIFY_NOT_NULL(field);\
	(*env)->SetLongField(env,self, field,(jlong)value);\
	}	

static void throwIOException(JNIEnv *env,const char* msg)
	{
	jclass newExcCls = (*env)->FindClass(env,"java/io/IOException");
	(*env)->ThrowNew(env, newExcCls, msg);
	}

/***************************************************************************************************/
/***************************************************************************************************/
/***************************************************************************************************/
/***************************************************************************************************/
CAST_REF_OBJECT(bwaidx_t*,BwaIndex,"ref")

/** close a BWAIndex by caling bwa_idx_destroy */
JNIEXPORT void JNICALL QUALIFIEDMETHOD(BwaIndex_close)(JNIEnv * env, jobject self)
  	{
  	bwaidx_t* ptr= _getBwaIndex(env,self);
  	if(ptr==0) return;
  	bwa_idx_destroy(ptr);
  	_setBwaIndex(env,self,0);
  	}


/** open a BWAIndex  by calling ::bwa_idx_load */
JNIEXPORT jlong JNICALL QUALIFIEDMETHOD(BwaIndex__1open)(JNIEnv *env, jclass c, jstring indexFile)
  	{
  	bwaidx_t *idx=0;
  	if(indexFile==0) return 0;
	const char* str= ( const char *) (*env)->GetStringUTFChars(env, indexFile, NULL);
	VERIFY_NOT_NULL(str);
  	idx = bwa_idx_load((const char*) str, BWA_IDX_ALL);
  	(*env)->ReleaseStringUTFChars(env,indexFile,str);
  	if(idx==0) throwIOException(env,"bwa_idx_load failed");
  	return (jlong)idx;
  	}

/***************************************************************************************************/
/***************************************************************************************************/
/***************************************************************************************************/
/***************************************************************************************************/

CAST_REF_OBJECT(mem_opt_t*,BwaMem,"ref")

/** BWAMem : a simple call to ::mem_opt_init  */
JNIEXPORT jlong JNICALL QUALIFIEDMETHOD(BwaMem_mem_1opt_1init)(JNIEnv *env, jclass c)
	{
	return (jlong)mem_opt_init();
	}

/** BWAMem dispose the resource. A simple call to free() */
JNIEXPORT void JNICALL QUALIFIEDMETHOD(BwaMem_dispose)(JNIEnv* env, jobject self)
	{
  	mem_opt_t* ptr= _getBwaMem(env,self);
  	if(ptr==0) return;
  	free((void*)ptr);
  	 _setBwaMem(env,self,0);
	}

/** align a short read, return an array of AlnRgn */
JNIEXPORT jobjectArray JNICALL QUALIFIEDMETHOD(BwaMem_align)(JNIEnv *env, jobject self,jobject bwaIndex, jbyteArray baseArray)
	{
	int i;
	jmethodID constructor;
	jobject returnedArray=0;
	mem_opt_t* opt= _getBwaMem(env,self);
	bwaidx_t* idx= _getBwaIndex(env,bwaIndex);
	jbyte* bases;
	jclass alnClass;
	
	VERIFY_NOT_NULL(bases = (*env)->GetByteArrayElements(env, baseArray,NULL));
  	mem_alnreg_v ar=mem_align1(opt, idx->bwt, idx->bns, idx->pac, (*env)->GetArrayLength(env,baseArray), (char*)bases);
  	
  	
  	VERIFY_NOT_NULL(alnClass = (*env)->FindClass(env, PACKAGEPATH "AlnRgn"));
	VERIFY_NOT_NULL(constructor = (*env)->GetMethodID(env,alnClass,"<init>", "(Ljava/lang/String;JBLjava/lang/String;III)V"));
  	VERIFY_NOT_NULL(returnedArray = (*env)->NewObjectArray(env,ar.n, alnClass, 0));
	
	// get all the hits
  	for (i = 0; i < ar.n; ++i)
  		{
  		size_t cigar_l=0;
  		jobject alnRgn=0;
  		int k;
		mem_aln_t a;
		// get forward-strand position and CIGAR
		a = mem_reg2aln(opt, idx->bns, idx->pac, (*env)->GetArrayLength(env,baseArray), (char*)bases, &ar.a[i]); 
		
		//get the length of the cigar string
		for (k = 0; k < a.n_cigar; ++k) 
			{
			cigar_l += snprintf(NULL, 0, "%d%c", a.cigar[k]>>4, "MIDSH"[a.cigar[k]&0xf]);
			}
		char* cigarStr=malloc(cigar_l+1);
		
		cigar_l=0;
		for (k = 0; k < a.n_cigar; ++k)
			{
			int n=sprintf(&cigarStr[cigar_l],"%d%c", a.cigar[k]>>4, "MIDSH"[a.cigar[k]&0xf]);
			cigar_l+=n;
			}
		
		cigarStr[cigar_l]=0;
		
		
		VERIFY_NOT_NULL(alnRgn = (*env)->NewObject(env, alnClass, constructor, 
			 (*env)->NewStringUTF(env, idx->bns->anns[a.rid].name),
			 (jlong)a.pos,
			 (jbyte)"+-"[a.is_rev],
			 (*env)->NewStringUTF(env, cigarStr),
			 (jint)a.mapq,
			 a.NM,
			 ar.a[i].secondary
			 ));

		(*env)->SetObjectArrayElement(env,returnedArray,i,alnRgn);
		free(a.cigar); // don't forget to deallocate CIGAR
		free(cigarStr);//deallocate cigar string
  		}
  	(*env)->ReleaseByteArrayElements(env,baseArray, bases, 0 );
  	free(ar.a);
  	return returnedArray;
	}



/*
 * Class:     com_github_lindenb_jbwa_jni_BwaMem
 * Method:    align2
 * Signature: ([Lcom/github/lindenb/jbwa/jni/ShortRead;[Lcom/github/lindenb/jbwa/jni/ShortRead;)I
 */
JNIEXPORT jobjectArray JNICALL QUALIFIEDMETHOD(BwaMem_align2)(
	JNIEnv *env,
	 jobject self,
	 jobject bwaIndex,
	 jobjectArray ks1,
	 jobjectArray ks2
	 )
  	{
  	bseq1_t *seqs=NULL;
  	int i;
  	int numPairs=0;
  	jclass shortReadClass;
  	jmethodID shortReadGetName;
  	jmethodID shortReadBases;
  	jmethodID shortReadQualities;  
  	jobjectArray samRetArray=NULL;
  	mem_opt_t* opt= _getBwaMem(env,self);
	bwaidx_t* idx= _getBwaIndex(env,bwaIndex);	
	
  	opt->flag |= MEM_F_PE;//PROBLEM FOR SHARED LIB HERE
  	if(ks1==NULL) return NULL;
  	if(ks2==NULL) return NULL;
  	
  	
  	if((numPairs=(*env)->GetArrayLength(env,ks1))!=(*env)->GetArrayLength(env,ks2)) return NULL;
  	
  	seqs=calloc(numPairs*2,sizeof(bseq1_t));
  	if(seqs==NULL) return NULL;
  	
  	VERIFY_NOT_NULL(shortReadClass = (*env)->FindClass(env, PACKAGEPATH "ShortRead"));
  	VERIFY_NOT_NULL(shortReadGetName = (*env)->GetMethodID(env,shortReadClass,"getName", "()Ljava/lang/String;"));
  	VERIFY_NOT_NULL(shortReadBases = (*env)->GetMethodID(env,shortReadClass,"getBases", "()[B"));
  	VERIFY_NOT_NULL(shortReadQualities = (*env)->GetMethodID(env,shortReadClass,"getQualities", "()[B"));
  	  	  	
  	for( i = 0; i < numPairs; i++)
		{
		int side=0;
		for(side=0;side<2;++side)
			{
			
			jbyteArray arrayOfBytes=NULL;
			jbyte* data;
			jsize length;
			jobject readName;
			bseq1_t* curr_bseq1=&seqs[i*2+side];
			
			jobject readObj = (*env)->GetObjectArrayElement(env,(side==0?ks1:ks2), i);
			
			//SAM 
			curr_bseq1->sam=NULL;

			//COMMENT 
			curr_bseq1->comment=strdup("");

			
			//NAME
			readName=(jbyteArray) (*env)->CallObjectMethod(env,readObj, shortReadGetName);
			VERIFY_NOT_NULL(readName);
			const char* str = (*env)->GetStringUTFChars(env,(jstring) readName, NULL);
			VERIFY_NOT_NULL(str);
			curr_bseq1->name=strdup(str);
			VERIFY_NOT_NULL(curr_bseq1->name);
			  (*env)->ReleaseStringUTFChars(env,readName, str);
			

			
			//SEQUENCE
			arrayOfBytes=(jbyteArray) (*env)->CallObjectMethod(env,readObj, shortReadBases);
			VERIFY_NOT_NULL(arrayOfBytes);
			data = (*env)->GetByteArrayElements(env,arrayOfBytes, NULL);
			VERIFY_NOT_NULL(data);
  			curr_bseq1->l_seq = (*env)->GetArrayLength(env,arrayOfBytes);
			curr_bseq1->seq=strndup((const char*)data,curr_bseq1->l_seq );

			
			VERIFY_NOT_NULL(curr_bseq1->seq);
			(*env)->ReleaseByteArrayElements(env,arrayOfBytes, data, 0);
			

			
			//QUALITIES
			arrayOfBytes=(jbyteArray) (*env)->CallObjectMethod(env,readObj, shortReadQualities);
			VERIFY_NOT_NULL(arrayOfBytes);
			data = (*env)->GetByteArrayElements(env,arrayOfBytes, NULL);
			VERIFY_NOT_NULL(data);
  			length = (*env)->GetArrayLength(env,arrayOfBytes);
			curr_bseq1->qual=strndup( (const char*)data,length);
			VERIFY_NOT_NULL(curr_bseq1->qual);
			(*env)->ReleaseByteArrayElements(env,arrayOfBytes, data, 0);
			

			}
		}

	mem_process_seqs(
		opt, /*  mem_opt_t *  */
		idx->bwt, /*  const bwt_t *bwt   */
		idx->bns, /* const bntseq_t *bns   */
		idx->pac,/*   const uint8_t *pac  */
		0,/* n_processed */
		numPairs*2,/*  n  */
		seqs,/*  bseq1_t *seqs   */
		0 /*  const mem_pestat_t *pes0  */
		);
	
	samRetArray = (jobjectArray)(*env)->NewObjectArray(env, (numPairs*2 ),  
         	(*env)->FindClass(env,"java/lang/String"),  
         	NULL
         	);
	VERIFY_NOT_NULL(samRetArray);
	
	for (i = 0; i <  (numPairs*2 ); ++i)
		{
		(*env)->SetObjectArrayElement(env,  
			samRetArray,i,
			(*env)->NewStringUTF(env,seqs[i].sam));  
		//fputs(seqs[i].sam, stderr);
		free(seqs[i].name);
		free(seqs[i].comment);
		free(seqs[i].seq);
		free(seqs[i].qual);
		free(seqs[i].sam);
		}
	free(seqs);
	return samRetArray;
  	}

/***************************************************************************************************/
/***************************************************************************************************/
/***************************************************************************************************/
/***************************************************************************************************/

/** structure holding bith the fastq reader and the gzInput */
typedef struct KSeqFile_t
	{
	kseq_t *ks;
	gzFile fp;
	} KSeqFile;


CAST_REF_OBJECT(KSeqFile*,KSeq,"ref")


/** dispose a KSeqFile ( kseq_destroy + gzclose) */
JNIEXPORT void JNICALL QUALIFIEDMETHOD(KSeq_dispose)(JNIEnv *env, jobject self)
	{
	KSeqFile* ptr = _getKSeq(env,self);
  	if(ptr==0) return;
  	kseq_destroy(((KSeqFile*)ptr)->ks);
  	gzclose( ((KSeqFile*)ptr)->fp);
  	free((void*)ptr);
  	 _setKSeq(env,self,0);
	}

JNIEXPORT jlong JNICALL QUALIFIEDMETHOD(KSeq_init)(JNIEnv* env, jobject self, jstring filename)
	{
	int try_stdin=1;
	KSeqFile *ksf;
  	VERIFY_NOT_NULL(ksf=(KSeqFile*)malloc(sizeof(KSeqFile)));

  	
  	if(filename!=0)
  		{
  		try_stdin=0;
	  	const char *str=(const char *) (*env)->GetStringUTFChars(env, filename, NULL);
		if (str == 0)
			{
			free((void*)ksf);
			throwIOException(env,"GetStringUTFChars failed");
			}
		if(strcmp("-",str)!=0)
			{
			ksf->fp= gzopen(str, "r") ;
			}
		else
			{
			try_stdin=1;
			}
		(*env)->ReleaseStringUTFChars(env, filename, str);
		}
	
	if(try_stdin)
		{
		ksf->fp= gzdopen(fileno(stdin), "r");
		}
	if(ksf->fp==0)
		{
		free((void*)ksf);
		throwIOException(env,"Cannot open Fastq input");
		}
	ksf->ks= kseq_init(ksf->fp);
  	return (jlong)ksf;
	}


/** read the next ShortRead */
JNIEXPORT jobject JNICALL QUALIFIEDMETHOD(KSeq_next)(JNIEnv *env, jobject self)
	{
	jobject result=0;
	jclass shortReadClass;
	jmethodID constructor;
	jstring argName;
	jbyteArray argSeq;
	jbyteArray argQual;
  	
  	KSeqFile* ksf = _getKSeq(env,self);
  	if(ksf==0) return 0;
  	if(kseq_read(ksf->ks)<0)
  		{
  		QUALIFIEDMETHOD(KSeq_dispose)(env,self);
  		return 0;
  		}
	VERIFY_NOT_NULL(shortReadClass = (*env)->FindClass(env, PACKAGEPATH "ShortRead"));
	VERIFY_NOT_NULL(constructor = (*env)->GetMethodID(env,shortReadClass,"<init>", "(Ljava/lang/String;[B[B)V"));

	
	VERIFY_NOT_NULL(argName = (*env)->NewStringUTF(env,ksf->ks->name.s)); 
	VERIFY_NOT_NULL(argSeq=(*env)->NewByteArray(env,ksf->ks->seq.l));
	(*env)-> SetByteArrayRegion(env, argSeq, 0, ksf->ks->seq.l, (jbyte*)ksf->ks->seq.s);
	VERIFY_NOT_NULL(argQual=(*env)->NewByteArray(env,ksf->ks->qual.l));
	(*env)-> SetByteArrayRegion(env, argQual, 0, ksf->ks->qual.l,(jbyte*) ksf->ks->qual.s);
	VERIFY_NOT_NULL(result = (*env)->NewObject(env, shortReadClass, constructor, argName,argSeq,argQual));
	
	return result;
	}

