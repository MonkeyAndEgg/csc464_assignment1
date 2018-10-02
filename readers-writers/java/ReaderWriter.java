import java.util.concurrent.Semaphore; 

class DataBase {
    int data = 0;
    LightSwitch readSwitch = new LightSwitch();
    LightSwitch writeSwitch = new LightSwitch();
    
    static Semaphore noReaders = new Semaphore(1);
    static Semaphore noWriters = new Semaphore(1);

    void write() {
        writeSwitch.lock(noReaders);
        try {
            noWriters.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        
        data = data + 5;
        System.out.println("The writer wrote the data: " + data);

        noWriters.release();
        writeSwitch.unlock(noReaders);
    }

    void read() {
        try {
            noReaders.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
        readSwitch.lock(noWriters);
        noReaders.release();
        
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
        new Thread(this, "Writer").start();
    }

    public void run() {
        for (int i = 0; i < 5; i++) {
            db.write();
        } 
    }
}

class Reader implements Runnable {
    DataBase db;

    Reader(DataBase db) {
        this.db = db;
        new Thread(this, "Reader").start();
    }

    public void run() {
        for (int i = 0; i < 5; i++) {
            db.read();
        }
    }
}

class ReaderWriter {
    public static void main(String args[]) {
        DataBase db = new DataBase();

        new Reader(db);

        new Writer(db);
    }
}