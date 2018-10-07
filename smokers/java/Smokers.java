import java.util.Random;
import java.util.concurrent.Semaphore;

public class Smokers{
 public static void main(String[] args) {
    Pushers pushers = new Pushers();

    new AgentThread(pushers);

    new PusherAThread(pushers);
    new PusherBThread(pushers);
    new PusherCThread(pushers);

    new TobaccoSmoker(pushers);
    new PaperSmoker(pushers);
    new MatchSmoker(pushers);
 }
}

class AgentThread implements Runnable { 
    Pushers pushers;

    AgentThread(Pushers pushers) {
        this.pushers = pushers;

        new Thread(this, "AgentThread").start();;
    }

    public void run() {
        while (true) {
            this.pushers.agent();
        }
    }
}

class PusherAThread implements Runnable {
    Pushers pushers;

    PusherAThread(Pushers pushers) {
        this.pushers = pushers;

        new Thread(this, "PusherAThread").start();;
    }

    public void run() {
        while (true) {
            this.pushers.pusherA();
        }
    }
}

class PusherBThread implements Runnable {
    Pushers pushers;

    PusherBThread(Pushers pushers) {
        this.pushers = pushers;

        new Thread(this, "PusherBThread").start();;
    }

    public void run() {
        while (true) {
            this.pushers.pusherB();
        }
    } 
}

class PusherCThread implements Runnable {
    Pushers pushers;

    PusherCThread(Pushers pushers) {
        this.pushers = pushers;

        new Thread(this, "PusherCThread").start();;
    }

    public void run() {
        while (true) {
            this.pushers.pusherC();
        }
    } 
}

class TobaccoSmoker implements Runnable {
    Pushers pushers;

    TobaccoSmoker(Pushers pushers) {
        this.pushers = pushers;

        new Thread(this, "TobaccoSmoker").start();;
    }

    public void run() {
        try{
            while (true) {
                this.pushers.smokerT();
            }
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
    } 
}

class PaperSmoker implements Runnable {
    Pushers pushers;

    PaperSmoker(Pushers pushers) {
        this.pushers = pushers;

        new Thread(this, "PaperSmoker").start();;
    }

    public void run() {
        try {
            while (true) {
                this.pushers.smokerP();
            }
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
    } 
}

class MatchSmoker implements Runnable {
    Pushers pushers;

    MatchSmoker(Pushers pushers) {
        this.pushers = pushers;

        new Thread(this, "MatchSmoker").start();;
    }

    public void run() {
        try {
            while (true) {
                this.pushers.smokerM();
            }
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
    } 
}

class Pushers {
    int tobaccoOnTable = 0;
    int paperOnTable = 0;
    int matchOnTable = 0;

    static Semaphore mutex = new Semaphore(1);
    static Semaphore agentSem = new Semaphore(1);
    
    static Semaphore tobacco = new Semaphore(0);
    static Semaphore paper = new Semaphore(0);
    static Semaphore match = new Semaphore(0);

    static Semaphore tobaccoSmoker = new Semaphore(0);
    static Semaphore paperSmoker = new Semaphore(0);
    static Semaphore matchSmoker = new Semaphore(0);    

    void pusherA() {
        try {
            tobacco.acquire();
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
        if (paperOnTable > 0) {
            paperOnTable--;
            matchSmoker.release();
        } else if (matchOnTable > 0) {
            matchOnTable--;
            paperSmoker.release();
        } else {
            tobaccoOnTable++;
        }
        mutex.release();
    }

    void pusherB() {
        try {
            paper.acquire();
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
        if (tobaccoOnTable > 0) {
            tobaccoOnTable--;
            matchSmoker.release();
        } else if (matchOnTable > 0) {
            matchOnTable--;
            tobaccoSmoker.release();
        } else {
            paperOnTable++;
        }
        mutex.release();
    }

    void pusherC() {
        try {
            match.acquire();
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
        if (tobaccoOnTable > 0) {
            tobaccoOnTable--;
            paperSmoker.release();
        } else if (paperOnTable > 0) {
            paperOnTable--;
            tobaccoSmoker.release();
        } else {
            matchOnTable++;
        }
        mutex.release();
    }

    void agent() {
        try {
            agentSem.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
        Random rand = new Random();
        int dice = rand.nextInt(3);

        switch (dice) {
            case 0:
                tobacco.release();
                paper.release();
                break;

            case 1:
                paper.release();
                match.release();
                break;

            case 2:
                tobacco.release();
                match.release();
                break;
        }
    }

    void smokerT() throws InterruptedException {
        tobaccoSmoker.acquire();
        makeCigarettes("TobaccoHolder");
        agentSem.release();
        smoke("TobaccoHolder");
    }

    void smokerP() throws InterruptedException {
        paperSmoker.acquire();
        makeCigarettes("PaperHolder");
        agentSem.release();
        smoke("PaperHolder");
    }

    void smokerM() throws InterruptedException {
        matchSmoker.acquire();
        makeCigarettes("MatchHolder");
        agentSem.release();
        smoke("MatchHolder");
    }

    void makeCigarettes(String holderId) throws InterruptedException {
        System.out.println(holderId + " is making cigarettes.");
        Thread.sleep(new Random().nextInt(1001));
    }

    void smoke(String holderId) throws InterruptedException {
        System.out.println(holderId + " is smoking.");
        Thread.sleep(new Random().nextInt(1001));
    }
}