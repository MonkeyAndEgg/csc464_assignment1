import threading
import time

class ReaderWriter():
	def __init__(self):
		self.read_switch = LightSwitch()
		self.write_switch = LightSwitch()
		self.no_readers = threading.Lock()
		self.no_writers = threading.Lock()
		self.readers_queue = threading.Lock()
	
	def reader_acquire(self):
		self.readers_queue.acquire()
		self.no_readers.acquire()
		self.read_switch.acquire(self.no_writers)
		self.no_readers.release()
		self.readers_queue.release()
	
	def reader_release(self):
		self.read_switch.release(self.no_writers)
	
	def writer_acquire(self):
		self.write_switch.acquire(self.no_readers)
		self.no_writers.acquire()
	
	def writer_release(self):
		self.no_writers.release()
		self.write_switch.release(self.no_readers)
	

class LightSwitch():
	def __init__(self):
		self.counter = 0
		self.mutex = threading.Lock()
	
	def acquire(self, lock):
		self.mutex.acquire()
		self.counter += 1
		if self.counter == 1:
			lock.acquire()
		self.mutex.release()

	def release(self, lock):
		self.mutex.acquire()
		self.counter -= 1
		if self.counter == 0:
			lock.release()
		self.mutex.release()

class Writer(threading.Thread):
	def __init__(self, db):
		threading.Thread.__init__(self)
		self.db = db

	def run(self):
		self.db.write()
		
class Reader(threading.Thread):
	def __init__(self, db):
		threading.Thread.__init__(self)
		self.db = db

	def run(self):
		self.db.read()		

class DataBase:
	def __init__(self, readerWriter):
		self.data = 0
		self.readerWriter = readerWriter
	
	def write(self):
		self.readerWriter.writer_acquire()
		self.data += 1
		print "The writer wrote: ", self.data
		time.sleep(0.4)
		print "Writer done process."
		self.readerWriter.writer_release()

	def read(self):
		self.readerWriter.reader_acquire()
		print "The reader read: ", self.data
		time.sleep(0.2)
		print "Reader done process."
		self.readerWriter.reader_release()

class Main():
    def __init__(self):
        self.db = DataBase(ReaderWriter())

    def main(self):
		Reader(self.db).start()
		Writer(self.db).start()
		Reader(self.db).start()
		Writer(self.db).start()
		Writer(self.db).start()
		Writer(self.db).start()
		

if __name__ == '__main__':
    Main().main()

