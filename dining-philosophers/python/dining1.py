import threading

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

def attendFeast(forksInit, i):
    forksInit.get_forks(i)

    print "I am eating."

    forksInit.put_forks(i)

def dinnerTime():
    forks = Forks()
    
    t1 = threading.Thread(target=attendFeast, args=[forks, 1])
    t1.start()

dinnerTime()