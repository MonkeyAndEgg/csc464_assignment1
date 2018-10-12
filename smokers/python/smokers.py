import threading
import random
import time
import psutil

class Pushers():
    mutex = threading.Semaphore(1)
    agentSem = threading.Semaphore(1)

    tobacco = threading.Semaphore(0)
    paper = threading.Semaphore(0)
    match = threading.Semaphore(0)

    tobaccoSmoker = threading.Semaphore(0)
    paperSmoker = threading.Semaphore(0)
    matchSmoker = threading.Semaphore(0)

    tobaccoOnTable = 0
    paperOnTable = 0
    matchOnTable = 0

    def agent(self):
        while 1:
            self.agentSem.acquire()
            randomInt = random.randint(0, 2)
            if (randomInt == 0):
                self.tobacco.release()
                self.paper.release()
            elif (randomInt == 1):
                self.paper.release()
                self.match.release()
            else:
                self.tobacco.release()
                self.match.release()
            print "CPU: ", psutil.cpu_percent()

    def PusherA(self):
        while 1:
            self.tobacco.acquire()
            self.mutex.acquire()
            if self.paperOnTable:
                self.paperOnTable -= 1
                self.matchSmoker.release()
            elif self.matchOnTable:
                self.matchOnTable -= 1
                self.paperSmoker.release()
            else:
                self.tobaccoOnTable += 1
            self.mutex.release()
	
    def PusherB(self):
        while 1:
            self.paper.acquire()
            self.mutex.acquire()
            if self.tobaccoOnTable:
                self.tobaccoOnTable -= 1
                self.matchSmoker.release()
            elif self.matchOnTable:
                self.matchOnTable -= 1
                self.tobaccoSmoker.release()
            else:
                self.paperOnTable += 1
            self.mutex.release()
        
    def PusherC(self):
        while 1:
            self.match.acquire()
            self.mutex.acquire()
            if self.tobaccoOnTable:
                self.tobaccoOnTable -= 1
                self.paperSmoker.release()
            elif self.paperOnTable:
                self.paperOnTable -= 1
                self.tobaccoSmoker.release()
            else:
                self.matchOnTable += 1
            self.mutex.release()

    def TobaccoHoldingSmoker(self):
        while 1:
            self.tobaccoSmoker.acquire()
            self.makeCigarettes("TobaccoHolder")
            self.agentSem.release()
            self.smoke("TobaccoHolder")

    def PaperHoldingSmoker(self):
        while 1:
            self.paperSmoker.acquire()
            self.makeCigarettes("PaperHolder")
            self.agentSem.release()
            self.smoke("PaperHolder")

    def MatchHoldingSmoker(self):
        while 1:
            self.matchSmoker.acquire()
            self.makeCigarettes("MatchHolder")
            self.agentSem.release()
            self.smoke("MatchHolder")
        
    def makeCigarettes(self, holderId):
        print holderId, " is makinhg cigarettes."
        time.sleep(0.1)
        
    def smoke(self, holderId):
        print holderId, " is smoking."
        time.sleep(0.1)

def main():
    pushers = Pushers()

    pusherA = threading.Thread(target=pushers.PusherA)
    pusherB = threading.Thread(target=pushers.PusherB)
    pusherC = threading.Thread(target=pushers.PusherC)
    agent = threading.Thread(target=pushers.agent)

    tobaccoSmoker = threading.Thread(target=pushers.TobaccoHoldingSmoker)
    paperSmoker = threading.Thread(target=pushers.PaperHoldingSmoker)
    matchSmoker = threading.Thread(target=pushers.MatchHoldingSmoker)

    pusherA.start()
    pusherB.start()
    pusherC.start()
    agent.start()

    tobaccoSmoker.start()
    paperSmoker.start()
    matchSmoker.start()

main()
	
