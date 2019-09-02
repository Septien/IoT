"""
This module will contain the class for the server part. It inherits from thread,
to make it run on the background.
"""

import socket as skt
import threading
import Queue as q
import select

class ServerS(threading.Thread):
    """
    Handles the server thread
    """
    def __init__(self, threadID, name, fileName, dataQ, dataQLock, exitQ, exitQLock):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.dataQ = dataQ
        self.dataQLock = dataQLock
        self.exitQ = exitQ
        self.exitQLock = exitQLock
        # Create server socket
        self.servSocket = skt.socket(skt.AF_INET, skt.SOCK_STREAM)
        self.fileName = fileName

        # Bind socket
        self.servSocket.bind(('192.168.43.215', 8080))
        # Accept at most one connection
        self.servSocket.listen(1)

    def run(self):
        """
        Server main loop
        """
        state = 0 # Wait for connections
        clientSkt = 0
        while True:
            if state == 0:       # Accept connections
                clientSkt, addr = self.servSocket.accept()
                state = 1
            # Recv/send data
            if state == 1:
                cmdB = self.read(clientSkt, 6)
                if not cmdB:
                    # connection close, wait for more.
                    state = 0
                    clientSkt.close()
                    continue
                cmd = cmdB.replace('\n', '')
                if cmd == "SET":
                    data = self.read(clientSkt, 1024)
                    data1 = data.split(";")
                    for d in data1:
                        self.write2Q(d)
                elif cmd == "GET":
                    with open(self.fileName, "rw") as file:
                        while True:
                            line = file.readline()
                            st = self.write(clientSkt, line, 512)
                            if st == -1:
                                state = 0
                                break
                            if line == "":
                                break
                        file.write("")

                else:
                    # command not known
                    continue
            self.exitQLock.acquire()
            if not self.exitQ.empty():
                cmd = self.exitQ.get()
                self.exitQLock.release()
                break;
            self.exitQLock.release()
        clientSkt.close()
        self.servSocket.close()


    def read(self, socketS, msglen):
        """
        Get from data from the socket
        """
        chunks = []
        bytes_recd = 0
        chunks = socketS.recv(msglen)
        return chunks.decode()

    def write(self, socketS, data, msglen):
        """
        Send data to client
        """
        totalSent = 0
        msg = bytearray(data, 'utf-8')
        msglen = len(msg)
        while totalSent < msglen:
            sent = socketS.send(msg[totalSent:])
            if sent == 0:
                return -1
            totalSent += sent
        return 1

    def write2Q(self, data):
        """
        """
        self.dataQLock.acquire()
        self.dataQ.put(data)
        self.dataQLock.release()
