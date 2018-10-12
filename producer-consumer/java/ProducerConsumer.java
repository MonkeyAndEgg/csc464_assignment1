import java.util.concurrent.Semaphore; 
import java.util.Queue;
import java.util.LinkedList;
  
class Q { 
    static final int BUFFER_SIZE = 3;
    Queue<Integer> queue = new LinkedList<Integer>();
    
    static Semaphore items = new Semaphore(0); 
    static Semaphore mutex = new Semaphore(1); 
    static Semaphore spaces = new Semaphore(BUFFER_SIZE);
       
    void get() { 
        try { 
            items.acquire(); 
            mutex.acquire();
        }  
        catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
          
        System.out.println("Consumer consumed item : " + this.queue.remove()); 
          
        mutex.release();
        spaces.release();
    } 
      
    void put(int item) { 
        try { 
            spaces.acquire(); 
            mutex.acquire();
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        } 
          
        this.queue.add(item);
          
        System.out.println("Producer produced item : " + item); 
          
        mutex.release(); 
        items.release();
    } 
} 
  
class Producer implements Runnable { 
    Q q;
    int data; 
    Producer(Q q, int data) { 
        this.q = q; 
        this.data = data;
        new Thread(this, "Producer").start(); 
    } 
      
    public void run() {
        for (int i = 0; i < 500; i++) {
            q.put(this.data * i);
        }
    } 
} 
  
class Consumer implements Runnable { 
    Q q; 
    Consumer(Q q) { 
        this.q = q; 
        new Thread(this, "Consumer").start(); 
    } 
      
    public void run() {
        for (int i = 0; i < 500; i++) {
            q.get();
        }
    } 
} 
  
class ProducerConsumer { 
    public static void main(String[] args) {
        Q q = new Q(); 

        new Consumer(q);
        new Producer(q, 1);

        new Consumer(q);
        new Producer(q, 2);
    } 
} 