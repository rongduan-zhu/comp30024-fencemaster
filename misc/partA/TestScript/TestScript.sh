#! /bin/sh
#
# Test Script for comp30016, semester 1, 2014 
# Author: Masud Moshtaghi Revised: Mahsa Salehi

compiler="/usr/java1.6/bin/javac"
java="/usr/java1.6/bin/java -Xmx1m"
classpath="-cp aima-core.jar"
    	if [ ! -d tempbin ]; then
	mkdir tempbin
	fi
	#========== find jar files
	submittedfiles=`ls *.jar 2> /dev/null` 
	for F in $submittedfiles
	do
		classpath=$classpath:$F
	done
	if [ ! -f manifest.mf ]; then
		echo ">>>   manifest.mf is missing."
		exit
	fi
	$compiler $classpath -d tempbin *.java -g 2> tempbin/compile1
	size=`stat -c %s tempbin/compile1`
	if [ $size -eq 0 ]; then
	 echo "Project compiled successfully."
	 Manifest=`grep 'Main-Class:' < manifest.mf | sed 's/Main-Class: //' | tr -d '\r\n'`
	 echo "Manifest read from manifest.mf: " $Manifest
	 classpath=$classpath":tempbin:."
	 Manifest=$java" "$classpath" "$Manifest
	 rlimit -t 2 -T 2 -f 100K -c 0 $Manifest <input1 >tempbin/results
	 rlimit -t 2 -T 2 -f 100K -c 0 $Manifest <input2 >>tempbin/results
	 diff -w tempbin/results output > tempbin/temp
	 size=`stat -c %s  tempbin/temp`
	 if [ $size -lt 1 ]; then         # Test exit status of "diff" command.
	  echo "========== Test passed successfully ========"
	  
	fi
	 cat tempbin/temp 
	rm -r tempbin
	fi

