import java.util.concurrent.Semaphore; 

class DataBase {
    int data = 0;
    LightSwitch readSwitch = new LightSwitch();
    LightSwitch writeSwitch = new LightSwitch();
    
    static Semaphore noReaders = new Semaphore(1);
    static Semaphore noWriters = new Semaphore(1);

    void write () throws InterruptedException {
        writeSwitch.lock(noReaders);
        noWriters.acquire();
        
        data = data + 2;
        System.out.println("The writer is writing...");
        Thread.sleep(100);
        System.out.println("The writer wrote the data: " + data);

        noWriters.release();
        writeSwitch.unlock(noReaders);
    }

    void read() throws InterruptedException {
        noReaders.acquire();
        readSwitch.lock(noWriters);
        noReaders.release();
        
        System.out.println("The reader is reading...");
        Thread.sleep(100);
        System.out.println("The reader got the data: " + data);

        readSwitch.unlock(noWriters);
    }
}

class LightSwitch {
    int counter = 0;
    static Semaphore mutex = new Semaphore(1);

    void lock(Semaphore semaphore) {
        try {
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        counter = counter + 1;
        if (counter == 1) {
            try {
                semaphore.acquire();
            } catch(InterruptedException e) { 
                System.out.println("InterruptedException caught"); 
            } 
        }
        mutex.release();
    }

    void unlock(Semaphore semaphore) {
        try {
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        counter = counter - 1;
        if (counter == 0) {
            semaphore.release();
        }
        mutex.release();
    }
}

class Writer implements Runnable {
    DataBase db;
    
    Writer(DataBase db) {
        this.db = db;
        Thread t = new Thread(this, "Writer");
        t.start();
    }

    public void run() {
        try {
            db.write();
        } catch(InterruptedException e) {
            System.out.println("InterruptedException caught");
        }
    }
}

class Reader implements Runnable {
    DataBase db;

    Reader(DataBase db) {
        this.db = db;
        Thread t = new Thread(this, "Reader");
        t.start();
    }

    public void run() {
        try {
            db.read();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
    }
}

class ReaderWriter {
    public static void main(String args[]) {
        DataBase db = new DataBase();
        
        for (int i = 0; i < 50; i ++) {
            new Reader(db);
            new Writer(db);
        }

        try {
        Thread.sleep(20000);
        } catch(InterruptedException e) {
            System.out.println("InterruptedException caught");
        }
    }
}