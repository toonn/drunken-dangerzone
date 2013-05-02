set terminal pngcairo dashed
set output 'ftp_3http.png'

#attractive gnuplotting
set style line 1 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 2 # --- red
set style line 2 lc rgb '#5e9c36' pt 6 ps 1 lt 1 lw 2 # --- green

set style line 11 lc rgb '#808080' lt 1
set border 3 back ls 11
set tics nomirror

set style line 12 lc rgb '#808080' lt 0 lw 1
set grid back ls 12

#set title "FTP throughput (3 intermittent 'http' requests)"
set xlabel "time(s)"
set ylabel "throughput(kB)"
set label "http request 1" at 4.6, 300 rotate left
set label "http request 2" at 9.6, 300 rotate left
set label "http request 3" at 14.6, 300 rotate left
set key on
plot "ex21.dat" using 1:($2/1000) title 'ftp' with lines ls 1, \
	"ex21.bursts" using 1:4 title '' with lines ls 7 linecolor rgb "black",\
	"ex21.bursts" using 2:4 title '' with lines ls 7 linecolor rgb "black",\
	"ex21.bursts" using 3:4 title '' with lines ls 7 linecolor rgb "black"
