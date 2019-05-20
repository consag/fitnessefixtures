fileName=$1
nrRecords=$2
#1;10;Demo1;1
if [ -z "$nrRecords" ] ; then
   echo "usage: $0 <filename> <nrrecords>"
   exit 1
fi
i=0
while [ $i -lt $nrRecords ] ; do
  i=$((i+1))
  echo "$i;$((i*10));Demo$i;$i" >> $fileName
done

