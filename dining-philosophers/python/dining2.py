import threading

class Forks:
    states = ['thinking'] * 5
    sem = [threading.Semaphore(0) for i in range(5)]
    mutex = threading.Semaphore(1)

    def left(self, i):
        return i
    
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

def attendFeast(forksInit, i):
    forksInit.get_fork(i)

    print "I am eating."

    forksInit.put_fork(i)

def dinnerTime():
    forks = Forks()
    
    t1 = threading.Thread(target=attendFeast, args=[forks, 1])
    t1.start()

dinnerTime()