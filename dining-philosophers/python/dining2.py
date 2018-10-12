import threading
import psutil
import time
import random

class Forks:
    states = ['thinking'] * 5
    sem = [threading.Semaphore(0) for i in range(5)]
    mutex = threading.Semaphore(1)

    def left(self, i):
        if i == 0:
            return self.states.__len__()-1
        else:
            return i - 1
    
    def right(self, i):
        return (i + 1) % 5

    def get_fork(self, i):
        self.mutex.acquire()
        self.states[i] = 'hungry'
        self.test(i)
        self.mutex.release()
        self.sem[i].acquire()

    def put_fork(self, i):
        self.mutex.acquire()
        self.states[i] = 'thinking'
        self.test(self.right(i))
        self.test(self.left(i))
        self.mutex.release()
    
    def test(self, i):
        if self.states[i] == 'hungry' and self.states[self.left(i)] != 'eating' and self.states[self.right(i)] != 'eating':
          self.states[i] = 'eating'
          self.sem[i].release()

def attendFeast(forksInit, i, personId):
    forksInit.get_fork(i)

    print "Philosopher#%d is eating." % personId

    time.sleep(0.1)

    print "Philosopher#%d is done eating." % personId

    forksInit.put_fork(i)

def dinnerTime():
    forks = Forks()

    num_people = 100

    t1 = threading.Thread(target=attendFeast, args=[forks, 0, 0])
    t2 = threading.Thread(target=attendFeast, args=[forks, 1, 1])
    t3 = threading.Thread(target=attendFeast, args=[forks, 2, 2])
    t4 = threading.Thread(target=attendFeast, args=[forks, 3, 3])
    t5 = threading.Thread(target=attendFeast, args=[forks, 4, 4])
    
    t1.start()
    t2.start()
    t3.start()
    t4.start()
    t5.start()

    t1.join()
    t2.join()
    t3.join()
    t4.join()
    t5.join()
    
    for i in range((num_people-5)/5):
        t1 = threading.Thread(target=attendFeast, args=[forks, 0, 0 + 5 * (i + 1)])
        t2 = threading.Thread(target=attendFeast, args=[forks, 1, 1 + 5 * (i + 1)])
        t3 = threading.Thread(target=attendFeast, args=[forks, 2, 2 + 5 * (i + 1)])
        t4 = threading.Thread(target=attendFeast, args=[forks, 3, 3 + 5 * (i + 1)])
        t5 = threading.Thread(target=attendFeast, args=[forks, 4, 4 + 5 * (i + 1)])
        t1.start()
        t2.start()
        t3.start()
        t4.start()
        t5.start()
        
        t1.join()
        t2.join()
        t3.join()
        t4.join()
        t5.join()


    print "CPU: ", psutil.cpu_percent()

dinnerTime()