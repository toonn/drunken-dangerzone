set terminal pngcairo dashed
set output 'ftp_cbr_throughput.png'

#attractive gnuplotting
set style line 1 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 2 # --- red
set style line 2 lc rgb '#5e9c36' pt 6 ps 1 lt 1 lw 2 # --- green

set style line 11 lc rgb '#808080' lt 1
set border 3 back ls 11
set tics nomirror

set style line 12 lc rgb '#808080' lt 0 lw 1
set grid back ls 12

#set title "Up/Down throughput"
set xlabel "time(s)"
set ylabel "throughput(kB)"
set label 1 "30" at -0.55,30 tc rgb '#808080'
set key on
plot "ex12_ftp.dat" using 1:($2/1000) title 'ftp' with lines ls 1, \
	"ex12_cbr.dat" using 1:($2/1000) title 'cbr' with lines ls 2, \
	"ex12_32.dat" using 1:2 title '' with lines ls 7 linecolor \
	rgb "black"
