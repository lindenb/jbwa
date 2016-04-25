jbwa
====

[![Build Status](https://travis-ci.org/lindenb/jbwa.svg)](https://travis-ci.org/lindenb/jbwa)

Java Bindings (JNI) for bwa

Author: 
	Pierre Lindenbaum PhD. @yokofakun (Institut du Thorax, Nantes, France)
	BWA is written by Heng Li  (Broad Institute)

Motivation
----------
BWA (http://bio-bwa.sourceforge.net/) contains a small C example(https://github.com/lh3/bwa/blob/master/example.c) for running *bwa-mem* as a library (bwamem-lite).
I created some JNI bindings to see if I can bind the C bwa library to java and get the same output than bwamem-lite.

Compilation
-----------

I've tested this code under linux and

* JAVA oracle JDK8
* GNU Make 3.81
* gcc 4.8.2
* wget

BWA for apache2 will be downloaded ( https://github.com/lh3/bwa/tree/Apache2 ) .

typing `make`, should download the sources bwa, compile and execute some tests.


See also
---------

  * https://github.com/broadinstitute/gatk/issues/1517


Contribute
----------

- Issue Tracker: http://github.com/lindenb/jbwa/issues
- Source Code: http://github.com/lindenb/jbwa

License
-------

The project is licensed under the Apache2 license.



Example  (Two FASTQs)
--------------------


```java
System.loadLibrary("bwajni");
//load the index
BwaIndex index=new BwaIndex(new File(args[0]));
//load the bwa engine
BwaMem mem=new BwaMem(index);
//get reads from two fastqs
KSeq kseq1=new KSeq(new File(args[1]));
KSeq kseq2=new KSeq(new File(args[2]));
//build a list of two fastqs, forward and reverse
List<ShortRead> L1=new ArrayList<ShortRead>();
List<ShortRead> L2=new ArrayList<ShortRead>();
//while something can be done
for(;;)
        {
        //read the pair of fastq
        ShortRead read1=kseq1.next();
        ShortRead read2=kseq2.next();
	//should we analyze and dump the data ?
        if(read1==null || read2==null || L1.size()>100)
                {
                if(!L1.isEmpty())
                        for(String sam:mem.align(L1,L2)) //get the SAM records
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
```


Testing
-------

Here is the ouput of the JAVA version:

```bash
java  -Djava.library.path=src/main/native -cp src/main/java com.github.lindenb.jbwa.jni.Example2 \
	human_g1k_v37.fasta  tmp1.fq  tmp2.fq

HWI-1KL149:20:C1CU7ACXX:4:1101:13638:2192       121     1       229568362       37      13S87M  =       229568362       0       GCTCTTCCGATCTGGCACGTTGAAGGTCTCAAACATGATCTGGGTCATCTTCTCGCGGTTGGCCTTGGGATTGAGGGGGGCCTCGGTGAGCAGGGNGGGG       AB?DDDDDDDBDCDDDDDDDDDDCDDDDCCC>(DCDDDDDDBDDDCCCCBDDDFFEEJIHIJIIHJIJJJJJJIJJJJJJJJJJJJJHHHHHDA2#FCCC    NM:i:1  AS:i:85 XS:i:61
HWI-1KL149:20:C1CU7ACXX:4:1101:13638:2192       181     1       229568362       0       *       =       229568362       0       GCTCTTCCGATCTCCCCACCCTGCTCACCGAGGNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNCAGNNNNNNNNNNNNNNNNNNAACGTGCC       ?DDDDDDDDDDDDDDB?9BDDDDDDDBBB?8,,######################################?12##################FFFFFCCC    AS:i:0  XS:i:0  
HWI-1KL149:20:C1CU7ACXX:4:1101:1424:2423        69      X       16753128        0       *       =       16753128        0       AGATNGGAAGAGCACACGTCTGAACTCCAGTCACCAAGGAGCATCTCGTATGCCGTCTTCTGCTTGAAAAAAAAAAAAAAACAAATACGGATGAGACATG       CCCF#2ADHHHHHJJJJJJJJJJJJJJ>9:1*1C3C8D600)0*0*/00-.8B)--5B().).=).?CFFFBBBDB########################    AS:i:0  XS:i:0  
HWI-1KL149:20:C1CU7ACXX:4:1101:1424:2423        137     X       16753128        0       58S34M8S        =       16753128        0       AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGTAGATCTCGGTGGTCGCCGTATCATTAAAAAAAAAAAAAAAAAACAAAAAAAGAGATGAACAAGCAAA       CCCFFFFFHHHHHJJJJJJJJJJJJJJJJJHIHIIJJJJJJJHJJIIJJJHFFFFEEEEEEEDDDD##################################    NM:i:0  AS:i:34    XS:i:29 
HWI-1KL149:20:C1CU7ACXX:4:1101:2908:2463        97      12      110765491       60      70M30S  =       110765491       70      AATTNGGGGAACAGCTTTCCAAAGTCATCTCCCTTATTTGCATTGCAGTCTGGATCATAAATATTGGGCAAGATCGGAAGAGCACACGTCTGAACTCCAG       CCCF#4BDHGHHHJJJJJJJJJIJHIJJJJJJJJJJJJJJJJIJJJJJJJJJJJJJIIIIHIJJJJIIIJIJJGHEHFFFEDDEEAA@BDDDCDDDD:C@    NM:i:1  AS:i:68 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:2908:2463        145     12      110765491       60      30S70M  =       110765491       -70     CTCTTTCCCTACACGACGCTCTTCCGATCTAATTTGGGGAACAGCTTTCCAAAGTCATCTCCCTTATTTGCATTGCAGTCTGGATCATAAATATTGGGCA       DDDDDDDDDCAB=DDBDEEFFFFHHHJJJGHHGGFJJJJJIIIIJJJJJIJJJJJIJIIIJJJJJJJJJJJJIJJHHHFHEEJJIJJHHHHHFFFFFCBC    NM:i:0  AS:i:70 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:4663:2297        81      4       114279632       60      100M    =       114279455       -277    GATTCCTACTGCACCCATGGAGAATGTGCCTTTTACTGAAAGCAAATCCAAAATTCCTGTAAGGACTATGCCCACTTCCACCCCAGCACCTCCATNTGCA       DCDDDDCACCDBCBCDDDCDDCCA?EEDDDFFDFFFHHHGHHHJJJJJJJJIJJIJIJJIJIJJJJJJJJJJIGJJIIHFIJJJJHGDHHDHDA2#FCCB    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:4663:2297        161     4       114279455       60      100M    =       114279632       277     CGTGCAAACGGGTGATATACCTCCTCTCTCTGGTGTAAAGCAGATATCCTGCCCCGACTCTTCTGAACCAGCTGTACAAGTCCAGTTAGATTTTTCCACA       CCBFFFFFHHHHFHIJJJJJIIJJJJJJJJJJJHIGIJIIJJJJJJJJJJHIJJJJJJJHHHHHHFDDDFDDEEEDDDADCCDDDCCDCCDEDDDCACCC    NM:i:0  AS:i:100  XS:i:0   
HWI-1KL149:20:C1CU7ACXX:4:1101:6872:2320        81      2       179597667       60      100M    =       179597628       -139    GGCTGTGCCTTCCACAAATGCTATCCTGTATCTGTCAGAAGCAGCTATTTCTTTGCCATCCTTAAACCAGGACACCCTCATGGGGAGGGAGCCTGNAATT       ABDDDDDBDDDDDDEDDEDDDEECEEFFFFFFHGHHHHJJIJJJJJIIJJIJJJJJJJJJIIJIHGJJJJJHHEJJIHJJJJJJJJJHHHHHDA2#FCCC    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:6872:2320        161     2       179597628       60      100M    =       179597667       139     CCCTGCATCATTCATGTCTACTCTGATGATCTCCAAAGAGGCTGTGCCTTCCACAAATGCTATCCTGTATCTGTCAGAAGCAGCTATTTCTTTGCCATCC       CCCFFFFFHHHHHJJJJJJJJJJJJIJJJJJJJJJJJJJJIIJJIIHJJJJIJJGIIIJJJIIJIIIHGIJJJJJIIEHHHHHHFBFFDEFECDECCDDA    NM:i:0  AS:i:100  XS:i:0   
HWI-1KL149:20:C1CU7ACXX:4:1101:9215:2408        97      2       220283746       60      100M    =       220283863       217     CAGCNGCTCAAGGCCAAGTGAGGGCCCGGCACCCCAGACTCCTCTTTCTGCGGGCAGGGCACAGGAGGCTAGGCCTGGGGGCTGGGGTCCCGCTGTCAGC       CCCF#2ADHHHHHFIJIIHIGIJJJJJJJJIIJJJJIJJJJJJJIIIJJIGFFFDDDDDDDBDDD?BDBDCBBDDCDDDDDBDDDBB>BBDDDDB@CDCD    NM:i:2  AS:i:93 XS:i:23
HWI-1KL149:20:C1CU7ACXX:4:1101:9215:2408        145     2       220283863       60      100M    =       220283746       -217    GCCCGGGACCCTCTCCTGCCCCATGTGGAGAAAGGGTCCTCCACCTGTGTGTTTCAAGGGGCCGTGACCTCCAGGTCTCTCCCCCTGCGATCCCATCTTG       BDDBDBC?DDDDDDDDDDDDDDDDDDDDDDDDDDDDBDDDDCADDDDDBEEEEEFFFFHHIJJJIHGJJJIJJJJJIIIIJIJJJJJHHHGHFFFFFCCC    NM:i:0  AS:i:100  XS:i:0   
HWI-1KL149:20:C1CU7ACXX:4:1101:9815:2325        97      22      46114322        60      100M    =       46114410        188     AAAGNCCGGAATTGGTACAAGCCATGTTTCCCAAACTGAACAATCAAGAAAGGTAACCCCCCAACCAGCGTGGTCTGGAGTATTTAGCATTCCATATAGG       CCCF#2ADHHHHHJJGHIJJJJJJJJIGJJJJJJJJJJJJJJJJJJJJGHIJJHIJJIIJJHFFFFDDCD?BDDDCCDCD>ACDEEDDDEDDEDCCCCCD    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:9815:2325        145     22      46114410        60      100M    =       46114322        -188    ATTCCATATAGGGTATTCGATGCACGTGACTGAAAAGCTGTGTGGTTTCTGAGTTGGCACAGAATCTCTAAATACATGTTTCTGTGTTGGTAATGGTTTT       DDCDEDCCDDDDCDDEEDEFFFFFHHHHIJJJJJJJIJJJJIIJJJIIGGJJJJJIJJJJJJJJIIHJJJJJIIJJJJJJJIIJIJIHFHHHFFFFFCCC    NM:i:0  AS:i:100  XS:i:0   
HWI-1KL149:20:C1CU7ACXX:4:1101:11401:2488       97      3       38763808        60      100M    =       38763855        147     CCACNATACGGTAGCAAGTCTTGCGCACCTGCCAGCCCACATCCCATGGACTCTTCGTGGTATCCAGTTTGCAGCAGGGACAGTGGCGAATGCATCCTGT       CCCF#4ADHHHHHJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJIJJJEIJJIJJJHHHFFFFFFFEEEEEEEDABBDDDBBCCDBD>BDDDDEDDDD>    NM:i:2  AS:i:93 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:11401:2488       145     3       38763855        60      100M    =       38763808        -147    GGACTCTTCGTGGTATCCAGTTTGCAGCAGGGACAGTGGCGAATGCATCCTGTGGGGAGAGGTGACTGATGGTGGGTGATGGCCAGTGGGCAAAGGGGAT       DDCDDDB?DCCCDECDDCDDDCDDEEDEFFFFFFHHHJJIJJJIJIIJIJJJIJJIJJJJJJJJIJJJJJJJJJJIJJJJJJJJJJJHHHHHFFFFFCCC    NM:i:1  AS:i:95 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:11658:2375       97      7       35293037        60      100M    =       35293129        192     CAGCNAGGGGCACAGACGGATGCGCAGCATCCCCAGTCCTCGGCGGACAGCCGGGTAGCCCAACTTACCCAGGGGTTTGATTGTGTTCTCCGTCGCCTCC       CCCF#2ADHHHHHJIIJJJJIJJJJJJJJJIJJJJJIJJJJJJJJDDDDDDDDDDBBDDDDDDDDDDDDDDDDDDDBBBDDDDDDDDCEDCB?ABDBDD1    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:11658:2375       145     7       35293129        60      100M    =       35293037        -192    TCGCCTCCTTCTCCTTAGAGCCGCCGCTCGACATGAGCGCGGCAATGGAGAAGGCGTTGGCCCGGGAGGAGAGTTGGGGCTTGGGGGACGCCGTGAACTC       DDBBBDDCA8DDDCC@DDDBDDDDDDDDDDEDDDDDDDDDDDDEDDDDCCDDDDFFFHHJJJJJJJJJHJJJJJJJJJJJJJJJJJJHHHHHFFFFDCBB    NM:i:1  AS:i:95 XS:i:20
HWI-1KL149:20:C1CU7ACXX:4:1101:12054:2300       97      2       40401764        60      100M    =       40401971        307     CAAGNTACATAAGATGTAGGTTTGGATTGATGGTTAAGGGTATTTGGGGAAAAATAAGGAACATTAAAAAAATAAGTCTTACCAAACAGGTATTTTCCTT       CCCF#4=DHHHHHIJJHIJJHIJJJHIJJIIJJEGHJJJJDGIJJJJJJGHHIJJIIJJJIIIIJIJJHHFDEDECDDEEDDDDDDDDDDCCDEEEDDCD    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:12054:2300       145     2       40401971        60      100M    =       40401764        -307    TTGTGAAGCCACCTAAAAAAGAAAAAAACAACAACAAATGTTATAATTTGACACTCTACATAACAAATACCAGTGACATCAGACTGCCTGACAACCCACC       @CC@DDDDDDDDDDDDDDDDDDFHHHHEIIHIIIJJJIJJJJJJJJJIHDIJJJJJIIJJJJIJJJJHFJJJJJJIJJJJJJJJJJJHHHHHFFFFDBCB    NM:i:0  AS:i:100  XS:i:0   
```
And the ouput of the Native C version:

```bash
bwa mem human_g1k_v37.fasta tmp1.fq tmp2.fq 2> /dev/null | grep -v -E '^@'

HWI-1KL149:20:C1CU7ACXX:4:1101:13638:2192       121     1       229568362       37      13S87M  =       229568362       0       GCTCTTCCGATCTGGCACGTTGAAGGTCTCAAACATGATCTGGGTCATCTTCTCGCGGTTGGCCTTGGGATTGAGGGGGGCCTCGGTGAGCAGGGNGGGG       AB?DDDDDDDBDCDDDDDDDDDDCDDDDCCC>(DCDDDDDDBDDDCCCCBDDDFFEEJIHIJIIHJIJJJJJJIJJJJJJJJJJJJJHHHHHDA2#FCCC    NM:i:1  AS:i:85 XS:i:61
HWI-1KL149:20:C1CU7ACXX:4:1101:13638:2192       181     1       229568362       0       *       =       229568362       0       GCTCTTCCGATCTCCCCACCCTGCTCACCGAGGNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNCAGNNNNNNNNNNNNNNNNNNAACGTGCC       ?DDDDDDDDDDDDDDB?9BDDDDDDDBBB?8,,######################################?12##################FFFFFCCC    AS:i:0  XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:1424:2423        69      X       16753128        0       *       =       16753128        0       AGATNGGAAGAGCACACGTCTGAACTCCAGTCACCAAGGAGCATCTCGTATGCCGTCTTCTGCTTGAAAAAAAAAAAAAAACAAATACGGATGAGACATG       CCCF#2ADHHHHHJJJJJJJJJJJJJJ>9:1*1C3C8D600)0*0*/00-.8B)--5B().).=).?CFFFBBBDB########################    AS:i:0  XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:1424:2423        137     X       16753128        0       58S34M8S        =       16753128        0       AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGTAGATCTCGGTGGTCGCCGTATCATTAAAAAAAAAAAAAAAAAACAAAAAAAGAGATGAACAAGCAAA       CCCFFFFFHHHHHJJJJJJJJJJJJJJJJJHIHIIJJJJJJJHJJIIJJJHFFFFEEEEEEEDDDD##################################    NM:i:0  AS:i:34    XS:i:29
HWI-1KL149:20:C1CU7ACXX:4:1101:2908:2463        97      12      110765491       60      70M30S  =       110765491       70      AATTNGGGGAACAGCTTTCCAAAGTCATCTCCCTTATTTGCATTGCAGTCTGGATCATAAATATTGGGCAAGATCGGAAGAGCACACGTCTGAACTCCAG       CCCF#4BDHGHHHJJJJJJJJJIJHIJJJJJJJJJJJJJJJJIJJJJJJJJJJJJJIIIIHIJJJJIIIJIJJGHEHFFFEDDEEAA@BDDDCDDDD:C@    NM:i:1  AS:i:68 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:2908:2463        145     12      110765491       60      30S70M  =       110765491       -70     CTCTTTCCCTACACGACGCTCTTCCGATCTAATTTGGGGAACAGCTTTCCAAAGTCATCTCCCTTATTTGCATTGCAGTCTGGATCATAAATATTGGGCA       DDDDDDDDDCAB=DDBDEEFFFFHHHJJJGHHGGFJJJJJIIIIJJJJJIJJJJJIJIIIJJJJJJJJJJJJIJJHHHFHEEJJIJJHHHHHFFFFFCBC    NM:i:0  AS:i:70 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:4663:2297        81      4       114279632       60      100M    =       114279455       -277    GATTCCTACTGCACCCATGGAGAATGTGCCTTTTACTGAAAGCAAATCCAAAATTCCTGTAAGGACTATGCCCACTTCCACCCCAGCACCTCCATNTGCA       DCDDDDCACCDBCBCDDDCDDCCA?EEDDDFFDFFFHHHGHHHJJJJJJJJIJJIJIJJIJIJJJJJJJJJJIGJJIIHFIJJJJHGDHHDHDA2#FCCB    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:4663:2297        161     4       114279455       60      100M    =       114279632       277     CGTGCAAACGGGTGATATACCTCCTCTCTCTGGTGTAAAGCAGATATCCTGCCCCGACTCTTCTGAACCAGCTGTACAAGTCCAGTTAGATTTTTCCACA       CCBFFFFFHHHHFHIJJJJJIIJJJJJJJJJJJHIGIJIIJJJJJJJJJJHIJJJJJJJHHHHHHFDDDFDDEEEDDDADCCDDDCCDCCDEDDDCACCC    NM:i:0  AS:i:100  XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:6872:2320        81      2       179597667       60      100M    =       179597628       -139    GGCTGTGCCTTCCACAAATGCTATCCTGTATCTGTCAGAAGCAGCTATTTCTTTGCCATCCTTAAACCAGGACACCCTCATGGGGAGGGAGCCTGNAATT       ABDDDDDBDDDDDDEDDEDDDEECEEFFFFFFHGHHHHJJIJJJJJIIJJIJJJJJJJJJIIJIHGJJJJJHHEJJIHJJJJJJJJJHHHHHDA2#FCCC    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:6872:2320        161     2       179597628       60      100M    =       179597667       139     CCCTGCATCATTCATGTCTACTCTGATGATCTCCAAAGAGGCTGTGCCTTCCACAAATGCTATCCTGTATCTGTCAGAAGCAGCTATTTCTTTGCCATCC       CCCFFFFFHHHHHJJJJJJJJJJJJIJJJJJJJJJJJJJJIIJJIIHJJJJIJJGIIIJJJIIJIIIHGIJJJJJIIEHHHHHHFBFFDEFECDECCDDA    NM:i:0  AS:i:100  XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:9215:2408        97      2       220283746       60      100M    =       220283863       217     CAGCNGCTCAAGGCCAAGTGAGGGCCCGGCACCCCAGACTCCTCTTTCTGCGGGCAGGGCACAGGAGGCTAGGCCTGGGGGCTGGGGTCCCGCTGTCAGC       CCCF#2ADHHHHHFIJIIHIGIJJJJJJJJIIJJJJIJJJJJJJIIIJJIGFFFDDDDDDDBDDD?BDBDCBBDDCDDDDDBDDDBB>BBDDDDB@CDCD    NM:i:2  AS:i:93 XS:i:23
HWI-1KL149:20:C1CU7ACXX:4:1101:9215:2408        145     2       220283863       60      100M    =       220283746       -217    GCCCGGGACCCTCTCCTGCCCCATGTGGAGAAAGGGTCCTCCACCTGTGTGTTTCAAGGGGCCGTGACCTCCAGGTCTCTCCCCCTGCGATCCCATCTTG       BDDBDBC?DDDDDDDDDDDDDDDDDDDDDDDDDDDDBDDDDCADDDDDBEEEEEFFFFHHIJJJIHGJJJIJJJJJIIIIJIJJJJJHHHGHFFFFFCCC    NM:i:0  AS:i:100  XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:9815:2325        97      22      46114322        60      100M    =       46114410        188     AAAGNCCGGAATTGGTACAAGCCATGTTTCCCAAACTGAACAATCAAGAAAGGTAACCCCCCAACCAGCGTGGTCTGGAGTATTTAGCATTCCATATAGG       CCCF#2ADHHHHHJJGHIJJJJJJJJIGJJJJJJJJJJJJJJJJJJJJGHIJJHIJJIIJJHFFFFDDCD?BDDDCCDCD>ACDEEDDDEDDEDCCCCCD    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:9815:2325        145     22      46114410        60      100M    =       46114322        -188    ATTCCATATAGGGTATTCGATGCACGTGACTGAAAAGCTGTGTGGTTTCTGAGTTGGCACAGAATCTCTAAATACATGTTTCTGTGTTGGTAATGGTTTT       DDCDEDCCDDDDCDDEEDEFFFFFHHHHIJJJJJJJIJJJJIIJJJIIGGJJJJJIJJJJJJJJIIHJJJJJIIJJJJJJJIIJIJIHFHHHFFFFFCCC    NM:i:0  AS:i:100  XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:11401:2488       97      3       38763808        60      100M    =       38763855        147     CCACNATACGGTAGCAAGTCTTGCGCACCTGCCAGCCCACATCCCATGGACTCTTCGTGGTATCCAGTTTGCAGCAGGGACAGTGGCGAATGCATCCTGT       CCCF#4ADHHHHHJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJIJJJEIJJIJJJHHHFFFFFFFEEEEEEEDABBDDDBBCCDBD>BDDDDEDDDD>    NM:i:2  AS:i:93 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:11401:2488       145     3       38763855        60      100M    =       38763808        -147    GGACTCTTCGTGGTATCCAGTTTGCAGCAGGGACAGTGGCGAATGCATCCTGTGGGGAGAGGTGACTGATGGTGGGTGATGGCCAGTGGGCAAAGGGGAT       DDCDDDB?DCCCDECDDCDDDCDDEEDEFFFFFFHHHJJIJJJIJIIJIJJJIJJIJJJJJJJJIJJJJJJJJJJIJJJJJJJJJJJHHHHHFFFFFCCC    NM:i:1  AS:i:95 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:11658:2375       97      7       35293037        60      100M    =       35293129        192     CAGCNAGGGGCACAGACGGATGCGCAGCATCCCCAGTCCTCGGCGGACAGCCGGGTAGCCCAACTTACCCAGGGGTTTGATTGTGTTCTCCGTCGCCTCC       CCCF#2ADHHHHHJIIJJJJIJJJJJJJJJIJJJJJIJJJJJJJJDDDDDDDDDDBBDDDDDDDDDDDDDDDDDDDBBBDDDDDDDDCEDCB?ABDBDD1    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:11658:2375       145     7       35293129        60      100M    =       35293037        -192    TCGCCTCCTTCTCCTTAGAGCCGCCGCTCGACATGAGCGCGGCAATGGAGAAGGCGTTGGCCCGGGAGGAGAGTTGGGGCTTGGGGGACGCCGTGAACTC       DDBBBDDCA8DDDCC@DDDBDDDDDDDDDDEDDDDDDDDDDDDEDDDDCCDDDDFFFHHJJJJJJJJJHJJJJJJJJJJJJJJJJJJHHHHHFFFFDCBB    NM:i:1  AS:i:95 XS:i:20
HWI-1KL149:20:C1CU7ACXX:4:1101:12054:2300       97      2       40401764        60      100M    =       40401971        307     CAAGNTACATAAGATGTAGGTTTGGATTGATGGTTAAGGGTATTTGGGGAAAAATAAGGAACATTAAAAAAATAAGTCTTACCAAACAGGTATTTTCCTT       CCCF#4=DHHHHHIJJHIJJHIJJJHIJJIIJJEGHJJJJDGIJJJJJJGHHIJJIIJJJIIIIJIJJHHFDEDECDDEEDDDDDDDDDDCCDEEEDDCD    NM:i:1  AS:i:98 XS:i:0
HWI-1KL149:20:C1CU7ACXX:4:1101:12054:2300       145     2       40401971        60      100M    =       40401764        -307    TTGTGAAGCCACCTAAAAAAGAAAAAAACAACAACAAATGTTATAATTTGACACTCTACATAACAAATACCAGTGACATCAGACTGCCTGACAACCCACC       @CC@DDDDDDDDDDDDDDDDDDFHHHHEIIHIIIJJJIJJJJJJJJJIHDIJJJJJIIJJJJIJJJJHFJJJJJJIJJJJJJJJJJJHHHHHFFFFDBCB    NM:i:0  AS:i:100  XS:i:0

```


Example  (One FASTQ)
--------------------
(compare to https://github.com/lh3/bwa/blob/master/example.c )

```java
System.loadLibrary("bwajni");
BwaIndex index=new BwaIndex(new File("hg19.fa"));
BwaMem mem=new BwaMem(index);
KSeq kseq=new KSeq(new File("input.fastq.gz");
ShortRead read=null;
while((read=kseq.next())!=null)
        {
        for(AlnRgn a: mem.align(read))
                {
                if(a.getSecondary()>=0) continue;
                System.out.println(  read.getName()+"\t"+  a.getStrand()+"\t"+  a.getChrom()+"\t"+
                        a.getPos()+"\t"+ a.getMQual()+"\t"+ a.getCigar()+"\t"+  a.getNm() );
                }
        }
kseq.dispose();
index.close();
mem.dispose();
```

Testing
-------

Here is the ouput of the JAVA version:

```bash
gunzip -c input.fastq.gz | head -n 4000 |\
java  -Djava.library.path=src/main/native -cp src/main/java \
   com.github.lindenb.jbwa.jni.Example human_g1k_v37.fasta -| tail 


HWI-1KL149:20:C1CU7ACXX:4:1101:3077:33410       +       3       38647538        60      89M11S  1
HWI-1KL149:20:C1CU7ACXX:4:1101:3396:33445       +       8       52567289        60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:10013:33288      -       1       156104115       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:10390:33496      -       6       123824853       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:13537:33483      +       2       157367092       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:14139:33390      +       20      31413797        60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:14514:33458      +       2       179401813       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:15292:33282      +       15      63335820        60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:16960:33276      -       12      110782784       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:17355:33322      +       6       126077895       60      100M    1
```

And the ouput of the Native C version:

```bash
gunzip -c input.fastq.gz | head -n 4000 |\
bwa-0.7.4/bwamem-lite human_g1k_v37.fasta - | tail 

HWI-1KL149:20:C1CU7ACXX:4:1101:3077:33410       +       3       38647538        60      89M11S  1
HWI-1KL149:20:C1CU7ACXX:4:1101:3396:33445       +       8       52567289        60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:10013:33288      -       1       156104115       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:10390:33496      -       6       123824853       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:13537:33483      +       2       157367092       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:14139:33390      +       20      31413797        60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:14514:33458      +       2       179401813       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:15292:33282      +       15      63335820        60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:16960:33276      -       12      110782784       60      100M    1
HWI-1KL149:20:C1CU7ACXX:4:1101:17355:33322      +       6       126077895       60      100M    1
```

GUI
---
As a test I also created a swing-Based interface for BWA:
```bash
java  -Djava.library.path=src/main/native  -cp src/main/java \
	com.github.lindenb.jbwa.jni.BwaFrame human_g1k_v37.fasta
```
![ScreenShot](https://raw.github.com/lindenb/jbwa/master/doc/bwajniswing.jpg)

WEB-SERVICE
-----------
As an example I've implemented a WebService for BWA.

### Server

The server is launched with make 'test.ws.server'
```bash
java   -Djava.library.path=src/main/native -cp src/main/java com.github.lindenb.jbwa.ws.server.BWAServiceImpl \
   -R human_g1k_v37.fasta -p 8081
Apr 26, 2013 9:54:34 PM com.github.lindenb.jbwa.ws.server.BWAServiceImpl main
INFO: Loading index for /commun/data/pubdb/broadinstitute.org/bundle/1.5/b37/human_g1k_v37.fasta
Apr 26, 2013 9:54:48 PM com.github.lindenb.jbwa.ws.server.BWAServiceImpl main
INFO: Service is published: http://localhost:8081/
```
once published, the server provides a WSDL -bases service description

```XML
<?xml version="1.0" encoding="UTF-8"?>
<!-- Published by JAX-WS RI at http://jax-ws.dev.java.net. RI's version is JAX-WS RI 2.2.4-b01. -->
<!-- Generated by JAX-WS RI at http://jax-ws.dev.java.net. RI's version is JAX-WS RI 2.2.4-b01. -->
<definitions xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsp="http://www.w3.org/ns/ws-policy" xmlns:
wsp1_2="http://schemas.xmlsoap.org/ws/2004/09/policy" xmlns:wsam="http://www.w3.org/2007/05/addressing/metadata" xmlns:soap="http://schemas.xmlsoap.org/wsdl/
soap/" xmlns:tns="http://server.ws.jbwa.lindenb.github.com/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://schemas.xmlsoap.org/wsdl/" targetName
space="http://server.ws.jbwa.lindenb.github.com/" name="BWAServiceImplService">
  <types>
    <xsd:schema>
      <xsd:import namespace="http://server.ws.jbwa.lindenb.github.com/" schemaLocation="http://localhost:8081/?xsd=1"/>
    </xsd:schema>
  </types>
  <message name="align">
    <part name="parameters" element="tns:align"/>
  </message>
  <message name="alignResponse">
    <part name="parameters" element="tns:alignResponse"/>
  </message>
  <message name="Exception">
    <part name="fault" element="tns:Exception"/>
  </message>
  <message name="getReferenceName">
    <part name="parameters" element="tns:getReferenceName"/>
  </message>
  <message name="getReferenceNameResponse">
    <part name="parameters" element="tns:getReferenceNameResponse"/>
  </message>
  <portType name="BWAService">
    <operation name="align">
      <input wsam:Action="http://server.ws.jbwa.lindenb.github.com/BWAService/alignRequest" message="tns:align"/>
      <output wsam:Action="http://server.ws.jbwa.lindenb.github.com/BWAService/alignResponse" message="tns:alignResponse"/>
      <fault message="tns:Exception" name="Exception" wsam:Action="http://server.ws.jbwa.lindenb.github.com/BWAService/align/Fault/Exception"/>
    </operation>
    <operation name="getReferenceName">
      <input wsam:Action="http://server.ws.jbwa.lindenb.github.com/BWAService/getReferenceNameRequest" message="tns:getReferenceName"/>
      <output wsam:Action="http://server.ws.jbwa.lindenb.github.com/BWAService/getReferenceNameResponse" message="tns:getReferenceNameResponse"/>
    </operation>
  </portType>
  <binding name="BWAServiceImplPortBinding" type="tns:BWAService">
(...)
  </binding>
  <service name="BWAServiceImplService">
    <port name="BWAServiceImplPort" binding="tns:BWAServiceImplPortBinding">
      <soap:address location="http://localhost:8081/"/>
    </port>
  </service>
</definitions>

```

### The Client

when the server is up and running, A client is generated for this service:

```bash
wsimport -keep -d tmp -p com.github.lindenb.jbwa.ws.client "http://localhost:8081/?wsdl"
```

The Makefile contains a target named 'test.ws.client' that reads a FASTQ, invoke the web-service and dump the result as XML:

```bash
gunzip -c test.fastq.gz |\
 java  -cp tmp  com.github.lindenb.jbwa.ws.client.BWAServiceClient 
```

Output:

```XML
<?xml version="1.0" encoding="UTF-8"?>
<bwa-service reference="human_g1k_v37.fasta">
  <Alignment xmlns="" xmlns:ns2="http://server.ws.jbwa.lindenb.github.com/">
    <chrom>1</chrom>
    <cigar>13S87M</cigar>
    <MQual>37</MQual>
    <nm>1</nm>
    <position>0</position>
    <readBases>CCCCNCCCTGCTCACCGAGGCCCCCCTCAATCCCAAGGCCAACCGCGAGAAGATGACCCAGATCATGTTTGAGACCTTCAACGTGCCAGATCGGAAGAGC</readBases>
    <readName>HWI-1KL149:20:C1CU7ACXX:4:1101:13638:2192 1:N:0:CAAGGAGC</readName>
    <secondary>-1</secondary>
    <strand>45</strand>
  </Alignment>
  <Alignment xmlns="" xmlns:ns2="http://server.ws.jbwa.lindenb.github.com/">
    <chrom>2</chrom>
    <cigar>82M18S</cigar>
    <MQual>0</MQual>
    <nm>5</nm>
    <position>0</position>
    <readBases>CCCCNCCCTGCTCACCGAGGCCCCCCTCAATCCCAAGGCCAACCGCGAGAAGATGACCCAGATCATGTTTGAGACCTTCAACGTGCCAGATCGGAAGAGC</readBases>
    <readName>HWI-1KL149:20:C1CU7ACXX:4:1101:13638:2192 1:N:0:CAAGGAGC</readName>
    <secondary>0</secondary>
    <strand>43</strand>
  </Alignment>
(...)
```
