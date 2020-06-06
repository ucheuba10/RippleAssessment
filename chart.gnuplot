set datafile separator ","
#set terminal png size 900,400
set title "Validated Ledger Sequence"
set ylabel "Sequence Num Per Time"
set xlabel "Date/Time"
set xtic auto rotate 
set xdata time
set timefmt "%H:%M:%S"
set format x "%H:%M:%S"
set format y "%0.0f"
#; set ytics(123456789)
set key left top
set grid
plot 'F:\Backup\Documents\NetBeansProjects\RippleAssessment\src\main\resources\output\output.csv' \
using 1:2 with linespoints title 'Seq'
