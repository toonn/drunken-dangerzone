#Create simulator
set ns [new Simulator]

$ns color 0 Blue
$ns color 1 Red

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

#and links
$ns duplex-link $n0 $n1 10Mb 10ms DropTail
$ns duplex-link $n1 $n2 7Mb 10ms DropTail

#connections
#Setup a UDP connection
set udp0 [new Agent/UDP]
$ns attach-agent $n0 $udp0
set null0 [new Agent/Null]
$ns attach-agent $n2 $null0
$ns connect $udp0 $null0
$udp0 set fid_ 0

#Setup a CBR over UDP connection
set cbr0 [new Application/Traffic/CBR]
$cbr0 attach-agent $udp0
$cbr0 set packetSize_ 1500
$cbr0 set rate_ 10Mb
$cbr0 set random_ false

#Setup a UDP connection
set udp2 [new Agent/UDP]
$ns attach-agent $n2 $udp2
set null2 [new Agent/Null]
$ns attach-agent $n0 $null2
$ns connect $udp2 $null2
$udp2 set fid_ 1

#Setup a CBR over UDP connection
set cbr2 [new Application/Traffic/CBR]
$cbr2 attach-agent $udp2
$cbr2 set packetSize_ 1500
$cbr2 set rate_ 5Mb
$cbr2 set random_ false


$ns at 0.0 "$cbr0 start"
$ns at 2.0 "$cbr2 start"
$ns at 3.0 "$cbr0 stop"
$ns at 5.0 "$cbr2 stop"
$ns at 5.0 "finish"

$ns run

