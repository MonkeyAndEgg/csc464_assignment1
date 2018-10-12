import java.util.concurrent.Semaphore;

class WaitRooms {
    int room1 = 0;
    int room2 = 0;

    static Semaphore mutex = new Semaphore(1);
    static Semaphore t1 = new Semaphore(1);
    static Semaphore t2 = new Semaphore(0);

    void runThoughWaitingRooms(int threadId) {
        try {
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        room1 ++;
        System.out.println("Thread#" + threadId + " is in room1.");
        mutex.release();

        try {
            t1.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
        room2 ++;
        try {
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        room1 --;

        if (room1 == 0) {
            System.out.println("Room1 is empty now.");
            mutex.release();
            t2.release();
        } else {
            mutex.release();
            t1.release();
        }

        try {
            t2.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        room2 --;

        System.out.println("Thread#" + threadId + " is in room2.");

        if (room2 == 0) {
            System.out.println("Room2 is empty now.");
            t1.release();
        } else {
            t2.release();
        }
    }
}

class ClientThread implements Runnable{
    WaitRooms waitRooms;
    int threadId;
    
    ClientThread(WaitRooms waitRooms, int threadId) {
        this.waitRooms = waitRooms;
        this.threadId = threadId;

        new Thread(this, "ClientThread").start();
    }

    public void run() {
        this.waitRooms.runThoughWaitingRooms(this.threadId);
    }
}

public class NoStarveMutex {
    public static void main(String[] args) {
        WaitRooms waitRooms = new WaitRooms();

        for (int i = 0; i < 1000; i++) {
            new ClientThread(waitRooms, i);
        }
    }
}