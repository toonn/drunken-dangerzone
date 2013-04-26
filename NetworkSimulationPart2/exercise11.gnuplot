set terminal pngcairo
set output 'ftp_througput.png'

set title "FTP throughput"
set xlabel "time(s)"
set ylabel "throughput(kB)"
set key off
plot "ex11.dat" using 1:($2/1000) with lines ls 1
