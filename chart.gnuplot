##############################################################################
## Plots a chart to display the frequency of validated ledgers over time    ##
## Usage:                                                                   ##
## 1. Update the path below to reference the output file with records.      ## 
## (This should point to the same file configured in the properties)        ##
## 2. Run from gnuplot terminal with command load 'chart.gnuplot'           ##
##############################################################################
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
set key left top
set grid
plot 'src\main\resources\output\output.csv' \
using 1:2 with linespoints title 'Sequence'
