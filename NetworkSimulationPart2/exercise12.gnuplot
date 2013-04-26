set terminal pngcairo
set output 'ftp_cbr_througput.png'

set title "Up/Down throughput"
set xlabel "time(s)"
set ylabel "throughput(kB)"
set label 1 "32" at -0.55,32
set key on
plot "ex12_ftp.dat" using 1:($2/1000) title 'ftp' with lines ls 1, \
	"ex12_cbr.dat" using 1:($2/1000) title 'cbr' with lines ls 2, \
	"ex12_32.dat" using 1:2 title '' with lines ls 0 linecolor \
	rgb "black"
