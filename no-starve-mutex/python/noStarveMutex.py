import threading
import psutil

class RoomInit:
    room1 = 0
    room2 = 0
    mutex = threading.Semaphore(1)
    t1 = threading.Semaphore(1)
    t2 = threading.Semaphore(0)

def waitingRooms(roomInit, threadId):
    roomInit.mutex.acquire()
    roomInit.room1 += 1
    print "Thread#%d is in room1." % (threadId)
    roomInit.mutex.release()

    roomInit.t1.acquire()
    roomInit.room2 += 1
    roomInit.mutex.acquire()
    roomInit.room1 -= 1
    print "Thread#%d is in room2." % (threadId)

    if roomInit.room1 == 0:
        print "room1 is empty now."
        roomInit.mutex.release()
        roomInit.t2.release()
    else:
        roomInit.mutex.release()
        roomInit.t1.release()

    roomInit.t2.acquire()
    roomInit.room2 -= 1

    if roomInit.room2 == 0:
        print "room2 is empty now."
        roomInit.t1.release()
    else:
        roomInit.t2.release()

def runThreads():
    roomInit = RoomInit()

    N_THREADS = 1000

    t = [None] * N_THREADS

    for i in range (0, N_THREADS):
        t[i] = threading.Thread(target=waitingRooms, args=[roomInit, i])
        t[i].start()
    
    for i in range (0, N_THREADS):
        t[i].join()
        
        
    print "CPU: ", psutil.cpu_percent()
    

runThreads()