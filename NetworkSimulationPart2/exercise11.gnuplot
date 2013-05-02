set terminal pngcairo
set output 'ftp_throughput.png'

#attractive gnuplotting
set style line 1 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 2 # --- red

set style line 11 lc rgb '#808080' lt 1
set border 3 back ls 11
set tics nomirror

set style line 12 lc rgb '#808080' lt 0
set grid back ls 12

#set title "FTP throughput"
set xlabel "time(s)"
set ylabel "throughput(kB)"
set key off
plot "ex11.dat" using 1:($2/1000) with lines ls 1
