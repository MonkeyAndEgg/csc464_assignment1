package main

import (
	"fmt"

	"github.com/pkg/profile"
)

var done = make(chan bool)
var msgs = make(chan int)

const bufferSize = 100

func produce() {
	for i := 0; i < bufferSize; i++ {
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
	defer profile.Start().Stop()
	go consume()
	go produce()
	<-done
}
