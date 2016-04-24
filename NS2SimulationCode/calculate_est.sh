#The command line argument are as follows
#$1 - Initial nodes number -initial number of nodes Including reader and tags
#$2 - Final nodes number- final number of nodes after $3 intervals  
#$3 - Interval (tics) - frequency in which tag papulation increase
#$4 - Iterations- how many times the simulation need to be run.


#Uncomment if the previous simulation calculations need to be erased before
#initialising a new simulation
#rm -f *.tr
#rm -f traces/ns-2/*.dat
#rm -f traces/ns-2/*.tar.bz2
#rm -f traces/ns-2/*.thr
#rm -f traces/ns-2/*.col
#rm -f traces/ns-2/*.suc
#rm -f traces/ns-2/*.idl
#rm -f traces/ns-2/START
#rm -f traces/ns-2/END
#rm -f traces/ns-2/LOG

#start time of the simulation
echo $(date) >> results/START

#for loop to run simulation
for ((j=$1; j<=$2; j=j+$3))
do
	for i in $(seq 1 $4)
	do
		echo "Starting TIme: $j.$i - " $(date +%d/%m/%y-%k:%M) >> results/LOG
		ns dcs3.tcl rfid.$j.$i.tr $j
		# the run command , the tcl file + the trace file+ number of nodes 
		# the awk script to run through the trace files to abtain the running time of each reader
                thr=$(awk -f time.awk rfid.$j.$i.tr)
		#save results for each tracefiles, zip similar simulations and delete the zipped files  
                echo $thr >> results/$j.thr
		tar jcvf results/rfid.$j.$i.tar.bz2 rfid.$j.$i.tr
		rm -f rfid.$j.$i.tr
		echo "Ending Time   : $j.$i - " $(date +%d/%m/%y-%k:%M) >> results/LOG
	done
done

cd results
#call finish.sh in result folder to calculate Mean and confidence interval using statistical library.
./finish.sh
echo "End   : $1.$2 - " $(date +%d/%m/%y-%k:%M) >> results/END
