import java.util.Random;
import java.util.concurrent.Semaphore;

final class Constants {
    public static final int NUM_OF_BOOKS = 8;
    public static final int NUM_OF_STUDENTS = 8;
}


class Book {
    int id;

    Semaphore lock;

    Book(int id) {
        this.id = id;
        this.lock = new Semaphore(1);
    }

    void read(Student student) throws InterruptedException {
        lock.acquire();
        System.out.println("Student#" + student.id + " is reading book#" + this.id + ".");
        Thread.sleep(1000);
        System.out.println("Student#" + student.id + " is done reading book#" + this.id + ".");
        lock.release();
    }
}

class Student implements Runnable {
    int id;
    Book[] books;
    int previousBook = -1;

    Student(int id, Book[] books) {
        this.id = id;
        this.books = books;
        new Thread(this, "StudentThread" + this.id).start();;
    }  

    public void run() {
        Random rand = new Random();

        int bookId = rand.nextInt(Constants.NUM_OF_BOOKS);
        while (bookId == previousBook) {
            bookId = rand.nextInt(Constants.NUM_OF_BOOKS);
        }

        try {
            books[bookId].read(this);
        } catch(InterruptedException e) { 
            System.out.println("InterruptedException caught"); 
        }
        previousBook = bookId;
    }
}

public class BooksBorrow {
    public static void main(String[] args) {
        Student[] student = new Student[Constants.NUM_OF_STUDENTS];
        Book[] books = new Book[Constants.NUM_OF_BOOKS];

        for (int i = 0; i < Constants.NUM_OF_BOOKS; i++) {
            books[i] = new Book(i);
        }

        for (int i = 0; i < Constants.NUM_OF_STUDENTS; i++) {
            student[i] = new Student(i, books);
        }

        try{
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException caught");
        }

    }
}