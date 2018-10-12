import threading
import time
import random
import psutil

NUM_OF_BOOKS = 8
NUM_OF_STUDENTS = 50

class Book:
    id = -1
    bookSem = None
    
    def __init__(self, id):
        self.id = id
        self.bookSem = threading.Semaphore(1)

    def read(self, studentId):
        self.bookSem.acquire()
        print "Student#%d is reading book#%d." % (studentId, self.id)
        time.sleep(1)
        print "Student#%d is done reading book#%d." % (studentId, self.id)
        self.bookSem.release()

class Student(threading.Thread):
    id = -1
    books = []
    previousBook = -2
     
    def __init__(self, id, books):
        threading.Thread.__init__(self)
        self.id = id
        self.books = books

    def run(self):
        bookId = random.randint(0, NUM_OF_BOOKS - 1)
        while bookId == self.previousBook:
            bookId = random.randint(0, NUM_OF_BOOKS - 1)
        
        self.books[bookId].read(self.id)
        self.previousBook = bookId

def main():
    books = [Book(-1)] * NUM_OF_BOOKS

    for i in range(books.__len__()):
        books[i] = Book(i)
    
    students = [Student(-1, books)] * NUM_OF_STUDENTS

    for i in range(students.__len__()):
        students[i] = Student(i , books)
        students[i].start()

    for i in range(students.__len__()):
        students[i].join()

    print "CPU: ", psutil.cpu_percent()


main()

