#!/usr/bin/perl

use strict;

#type: throughput.pl <trace file> <required node> <granularity> > file

my $infile=$ARGV[0];
my $tonode=$ARGV[1];
my $granularity=$ARGV[2];

# We compute how many bytes were transmitted during time interval specified
# by granularity parameter in seconds

# relevant fields in the trace file
my $event;
my $time;
my $from;
my $to;
my $pkttype;
my $pktsize;
my $rest;

my $sum=0;
my $clock=0;
my $throughput;
my $line;

open(DATA, "<$infile") || die "Can't open $infile";

while ($line = <DATA>) {
	($event, $time, $from, $to, $pkttype, $pktsize, $rest) = split(' ', $line);

	if ($time - $clock > $granularity) {
		$throughput = $sum / $granularity;
		print STDOUT "$time $throughput\n";
		$clock += $granularity;
		$sum = 0;
	}

	if ( ($event eq 'r') && ($to eq $tonode) && ($pkttype eq 'tcp')) {
		$sum += $pktsize;
	}
}

$throughput = $sum / $granularity;
print STDOUT "$time $throughput\n";

close DATA;


