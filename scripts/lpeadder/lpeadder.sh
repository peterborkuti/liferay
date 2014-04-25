#LPE adder
#run it from the directory, where the customer's fixes are
#OUTPUT
#in LPE.txt file
#HINT: you can gather customer's fixes with internal patching-tool, like this:
#
#patching-tool>patching-tool.bat download administration-1-6130
#y-1-6130, collaboration-5-6130, development-2-6130, document-management-7-6130, dynamic-data-lists-4-6130, misc-1-
#erface-4-6130, wcm-core-5-6130, web-content-7-6130
#
# Author: P�ter Borkuti, 2014.03.12
# Version 1.1
#
# history
# 1.0 -> 1.1 
#    There are such an LPEs: LPE-11301=LPE-11450+LPE-11451
#    It must be transfomed to:  LPE-11301,LPE-11450,LPE-11451
#
echo > tmp.txt
for i in *.zip; do
	echo $i
	unzip -p "$i" fixpack_documentation.xml|grep fixed-issues|sed -e 's@</*fixed-issues>@@g'|tr -d '\t' >> tmp.txt
done
grep -v '^ *$' tmp.txt|tr ',' '\n' |sort|uniq| tr '\n' ',' |sed -e 's/,$//'| tr '=+' ',,' > LPE.txt
