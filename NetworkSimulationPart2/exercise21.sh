#! /usr/bin/env sh

ns exercise21.tcl
perl throughput.pl /tmp/ex21.out.tr 2 0.2 > ex21.dat
gnuplot exercise21.gnuplot
