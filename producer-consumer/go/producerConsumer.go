package main

import (
	"fmt"
	"time"

	"github.com/pkg/profile"
	psutil "github.com/shirou/gopsutil/cpu"
)

var done = make(chan bool)
var msgs = make(chan int)

const bufferSize = 3

func produce() {
	for i := 0; i < 500; i++ {
		fmt.Println("Produced: ", i)
		msgs <- i
	}
	done <- true
}

func consume() {
	for {
		msg := <-msgs
		fmt.Println("Consumed: ", msg)
	}
}

func main() {
	defer profile.Start(profile.MemProfile).Stop()
	go produce()
	go consume()
	go produce()
	go consume()

	<-done
	percentage, err := psutil.Percent(time.Duration(5)*time.Millisecond, false)
	if err != nil {
		fmt.Println(err)
	}

	fmt.Println("CPU Percent: ", percentage)
}
