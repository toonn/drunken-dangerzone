set terminal pngcairo dashed
set output 'ftp_3http_wnd_thresh.png'

#attractive gnuplotting
set style line 1 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 2 # --- red
set style line 2 lc rgb '#5e9c36' pt 6 ps 1 lt 1 lw 2 # --- green

set style line 11 lc rgb '#808080' lt 1
set border 3 back ls 11
set tics nomirror

set style line 12 lc rgb '#808080' lt 0 lw 1
set grid back ls 12

#set title "Congestion window & slow start threshold"
set xlabel "time(s)"
set ylabel "# segments"
set label "http request 1" at 4.6, 20 rotate left
set label "http request 2" at 9.6, 20 rotate left
set label "http request 3" at 14.6, 20 rotate left
set key on

set label "exponential\n'slow start'" at 17.2,3 font ",7"
set arrow from 17, 3 to 16.4, 5
set label " linear\n  after\nthreshold" at 16, 35 font ",7"
set arrow from 16.5, 25 to 17, 20
set label "  threshold\nset to half of\n congestion\n    window" at 18.3, 37.3 font ",7"
set arrow from 18.8, 21 to 18.5, 15

plot "ex21.wnd.dat" using 1:2 title 'congestion window' with lines ls 1, \
	"ex21.thresh.dat" using 1:2 title 'slow start threshold' with lines \
	ls 2, \
	"ex22.bursts" using 1:4 title '' with lines ls 7 linecolor rgb "black",\
	"ex22.bursts" using 2:4 title '' with lines ls 7 linecolor rgb "black",\
	"ex22.bursts" using 3:4 title '' with lines ls 7 linecolor rgb "black"
