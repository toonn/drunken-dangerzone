#! /usr/bin/env sh

ns exercise12.tcl
perl throughput.pl /tmp/ex1.out.tr 1 0.2 > ex12_ftp.dat
perl throughput.pl /tmp/ex1.out.tr 7 0.2 > ex12_cbr.dat
gnuplot exercise12.gnuplot
