#Create simulator
set ns [new Simulator]

$ns color 1 Blue
$ns color 2 Red
$ns color 3 Green

#trace file
set tf [open /tmp/ex1.out.tr w]
$ns trace-all $tf

#nam tracefile
set nf [open /tmp/ex1.out.nam w]
$ns namtrace-all $nf

proc finish {} {
	#finalize trace files
	global ns nf tf
	$ns flush-trace
	close $tf
	close $nf

	exec nam /tmp/ex1.out.nam &
	exit 0
}

#create nodes
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]
set n4 [$ns node]
set n5 [$ns node]
set n6 [$ns node]
set n7 [$ns node]

#and links
$ns duplex-link $n0 $n2 10Mb 0.2ms DropTail
$ns duplex-link $n1 $n2 10Mb 0.2ms DropTail
$ns duplex-link $n2 $n3 10Mb 0.2ms DropTail
$ns simplex-link $n3 $n4 256kb 0.2ms DropTail
$ns simplex-link $n4 $n3 4Mb 0.2ms DropTail
$ns duplex-link $n4 $n5 100Mb 0.3ms DropTail
$ns duplex-link $n5 $n6 100Mb 0.3ms DropTail
$ns duplex-link $n5 $n7 100Mb 0.3ms DropTail

#Set Queue Size of link (n0-n1) to 20
#$ns queue-limit $n0 $n1 20

#connections
#Setup a TCP connection
set tcp1 [new Agent/TCP]
$ns attach-agent $n6 $tcp1
set sink1 [new Agent/TCPSink]
$ns attach-agent $n1 $sink1
$ns connect $tcp1 $sink1
$tcp1 set fid_ 1
$tcp1 set window_ 80

#Setup a FTP over TCP connection
set ftp1 [new Application/FTP]
$ftp1 attach-agent $tcp1

$ns at 0.1 "$ftp1 start"
$ns at 9.9 "$ftp1 stop"
$ns at 10 "finish"

$ns run

