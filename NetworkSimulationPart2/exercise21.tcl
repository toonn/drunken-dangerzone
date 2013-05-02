#Create simulator
set ns [new Simulator]

$ns color 1 Blue
$ns color 2 Red
$ns color 3 Green

#file size of each transfer
set filesize [open ex21.size.dat w]

#congestion window
set wnd [open ex21.wnd.dat w]

#slow start threshold
set ssthresh [open ex21.thresh.dat w]

#trace file
set tf [open /tmp/ex21.out.tr w]
$ns trace-all $tf

#nam tracefile
#set nf [open /tmp/ex1.out.nam w]
#$ns namtrace-all $nf

proc finish {} {
	#finalize trace files
	global ns nf tf filesize wnd ssthresh
	$ns flush-trace
	close $filesize
	close $wnd
	close $ssthresh
	close $tf
	#close $nf

	#exec nam /tmp/ex1.out.nam &
	exit 0
}

#create nodes
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]
set n4 [$ns node]
set n5 [$ns node]

#and links
$ns duplex-link $n0 $n1 10Mb 10ms DropTail
$ns duplex-link $n0 $n2 10Mb 10ms DropTail
$ns duplex-link $n0 $n4 10Mb 10ms DropTail
$ns duplex-link $n1 $n3 10Mb 10ms DropTail
$ns duplex-link $n1 $n5 10Mb 10ms DropTail

#Set Queue Size of link (n0-n1) to 20
$ns queue-limit $n0 $n1 20

#connections
#Setup a TCP connection
set tcp1 [new Agent/TCP]
$ns attach-agent $n3 $tcp1
set sink1 [new Agent/TCPSink]
$ns attach-agent $n2 $sink1
$ns connect $tcp1 $sink1
$tcp1 set fid_ 1
$tcp1 set window_ 80

#Setup a FTP over TCP connection
set ftp1 [new Application/FTP]
$ftp1 attach-agent $tcp1

#Generators
set rng [new RNG]
$rng seed 0

set RVtiming [new RandomVariable/Exponential]
$RVtiming set avg_ 0.05
$RVtiming use-rng $rng

set RVsize [new RandomVariable/Pareto]
$RVsize set avg_ 150000
$RVsize set shape_ 1.5
$RVsize use-rng $rng

#120 connections
set nbConn 120
set Tadd 0

for {set i 0} {$i < $nbConn} {incr i} {
	set tcp_src [new Agent/TCP]
	$ns attach-agent $n5 $tcp_src
	set tcp_snk [new Agent/TCPSink]
	$ns attach-agent $n4 $tcp_snk
	$ns connect $tcp_src $tcp_snk

	set ftp [$tcp_src attach-source FTP]

	set fileSize [$RVsize value]
	$tcp_src set size $fileSize

	set startT [expr [expr $i/40 + 1] * 5]
	if {[expr [expr $i-1]/40] < [expr $i/40]} {
		set Tadd 0
	}
	$ns at [expr $startT + $Tadd] "$ftp start"
	set Tadd [expr $Tadd + [$RVtiming value]]

	puts $filesize "[expr $i/40 + 1] \t [expr $startT + $Tadd] \t $fileSize"
}

#logging congestion window and slow start threshold
proc logWndThresh {} {
	global wnd ssthresh ns tcp1

	puts $wnd "[$ns now] [$tcp1 set cwnd_]"
	puts $ssthresh "[$ns now] [$tcp1 set ssthresh_]"

	$ns at [expr [$ns now] + 0.2] "logWndThresh"
}

$ns at 0.1 "$ftp1 start"
$ns at 0.2 "logWndThresh"
$ns at 19.9  "$ftp1 stop"
$ns at 20 "finish"

$ns run

