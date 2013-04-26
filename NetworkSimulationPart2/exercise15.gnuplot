set terminal pngcairo
set output 'ftp_cbr30k_througput.png'

set title "Up/Down throughput"
set xlabel "time(s)"
set ylabel "throughput(kB)"
set key on
plot "ex15_ftp.dat" using 1:($2/1000) title 'ftp' with lines ls 1, \
	"ex15_cbr.dat" using 1:($2/1000) title 'cbr' with lines ls 2
