import java.util.concurrent.Semaphore;

class WaitRooms {
    int room1 = 0;
    int room2 = 0;

    static Semaphore mutex = new Semaphore(1);
    static Semaphore t1 = new Semaphore(1);
    static Semaphore t2 = new Semaphore(0);

    void runThoughWaitingRooms(String threadId) {
        try {
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        room1 ++;
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

        System.out.println("This is " + threadId);

        if (room2 == 0) {
            t1.release();
        } else {
            t2.release();
        }
    }
}

class ClientThread implements Runnable{
    WaitRooms waitRooms;
    String threadId;
    
    ClientThread(WaitRooms waitRooms, String threadId) {
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

        new ClientThread(waitRooms, "thread1");
        new ClientThread(waitRooms, "thread2");
    }
}