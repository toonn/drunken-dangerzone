#! /usr/bin/env sh

ns exercise15.tcl
perl throughput.pl /tmp/ex1.out.tr 1 0.2 > ex15_ftp.dat
perl throughput.pl /tmp/ex1.out.tr 7 0.2 > ex15_cbr.dat
gnuplot exercise15.gnuplot
