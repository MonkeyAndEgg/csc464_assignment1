import threading

class RoomInit:
    room1 = 0
    room2 = 0
    mutex = threading.Semaphore(1)
    t1 = threading.Semaphore(1)
    t2 = threading.Semaphore(0)

def waitingRooms(roomInit, threadId):
    roomInit.mutex.acquire()
    roomInit.room1 += 1
    print "room1 now is: ", roomInit.room1
    roomInit.mutex.release()

    roomInit.t1.acquire()
    roomInit.room2 += 1
    roomInit.mutex.acquire()
    roomInit.room1 -= 1

    print "room1 after room2++ is: ", roomInit.room1

    if roomInit.room1 == 0:
        roomInit.mutex.release()
        roomInit.t2.release()
    else:
        roomInit.mutex.release()
        roomInit.t1.release()

    roomInit.t2.acquire()
    roomInit.room2 -= 1

    print "This is " + threadId 

    if roomInit.room2 == 0:
        roomInit.t1.release()
    else:
        roomInit.t2.release()

def runThreads():
    roomInit = RoomInit()
    t1 = threading.Thread(target=waitingRooms, args=[roomInit, 't1'])
    t2 = threading.Thread(target=waitingRooms, args=[roomInit, 't2'])
    t1.start()
    t2.start()

runThreads()