#! /usr/bin/env sh

ns exercise11.tcl
perl throughput.pl /tmp/ex1.out.tr 1 0.2 > ex11.dat
gnuplot exercise11.gnuplot
