import threading
import psutil
import random
import time

class Forks:
    forks = [threading.Semaphore(1) for i in range(5)]
    footman = threading.Semaphore(4)

    def left(self, i):
        return i
    
    def right(self, i):
        return (i + 1) % 5

    def get_forks(self, i):
        self.footman.acquire()
        self.forks[self.right(i)].acquire()
        self.forks[self.left(i)].acquire()

    def put_forks(self, i):
        self.forks[self.right(i)].release()
        self.forks[self.left(i)].release()
        self.footman.release()

def attendFeast(forksInit, i, personId):
    forksInit.get_forks(i)

    print "Philosopher#%d is eating." % personId

    time.sleep(0.1)

    print "Philosopher#%d is done eating." % personId

    forksInit.put_forks(i)

def dinnerTime():
    forks = Forks()

    num_people = 100

    people = [None] * num_people
    
    for i in range(0, num_people):
        people[i] = threading.Thread(target=attendFeast, args=[forks, random.randint(0, 4), i])
        people[i].start()
   
    for i in range(0, num_people):
        people[i].join()

    print "CPU: ", psutil.cpu_percent()
dinnerTime()