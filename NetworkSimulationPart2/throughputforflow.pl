#!/usr/bin/perl

use strict;

#type: throughput.pl <trace file> <required node> <fid> <granularity> > file

my $infile=$ARGV[0];
my $tonode=$ARGV[1];
my $flow=$ARGV[2];
my $granularity=$ARGV[3];

# We compute how many bytes were transmitted during time interval specified
# by granularity parameter in seconds

# relevant fields in the trace file
my $event;
my $time;
my $from;
my $to;
my $pkttype;
my $pktsize;
my $flags;
my $fid;
my $rest;

my $sum=0;
my $clock=0;
my $throughput;
my $line;

open(DATA, "<$infile") || die "Can't open $infile";

while ($line = <DATA>) {
	($event, $time, $from, $to, $pkttype, $pktsize, $flags, $fid, $rest) = split(' ', $line);

	if ($time - $clock > $granularity) {
		$throughput = $sum / $granularity;
		print STDOUT "$time $throughput\n";
		$clock += $granularity;
		$sum = 0;
	}

	if ( ($event eq 'r') && ($to eq $tonode)
         && ($fid eq $flow)
         && ( ($pkttype eq 'tcp') || ($pkttype eq 'cbr') ) ) {
		$sum += $pktsize;
	}
}

$throughput = $sum / $granularity;
print STDOUT "$time $throughput\n";

close DATA;


