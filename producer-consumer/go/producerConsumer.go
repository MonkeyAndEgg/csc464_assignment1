package main

import (
	"fmt"
)

var done = make(chan bool)
var msgs = make(chan int)

func produce() {
	for i := 0; i < 10; i++ {
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
	go consume()
	go produce()
	<-done
}
