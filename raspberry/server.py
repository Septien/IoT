"""
This module will contain the class for the server part. It inherits from thread,
to make it run on the background.
"""

import socket as sck
import threading as t
import queue as q
import select

def ServerS(t.Thread):
    """
    Handles the server thread
    """
    def __init__(self, threadID, name, fileName, dataQ, dataQLock, exitQ, exitQLock):
        t.thread.__init__(self)
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
        self.servSocket.bind((skt.gethostname(), 8080))
        # Accept at most one connection
        self.servSocket.listen(1)

    def run(self):
        """
        Server main loop
        """
        state = 0 # Wait for connections
        clientSkt = 0
        while True:
            # Get the available sockets
            rdy2read, rdy2write, err = select.select([self.servSocket], [clientSkt], [])
            if state == 0 and rdy2read[0] == self.servSocket:       # Accept connections
                clientSkt, addr = self.servSocket.accept()
                state = 1
            if state == 1 and rdy2write[0] == clientSkt:            # Recv/send data
                cmd = self.read(clientSkt, 3)
                if cmd == "SET":
                    data = self.read(clientSkt, 1024)
                    data1 = data.split(";")
                    for d in data1:
                        self.write2Q(d)
                elif cmd == "GET":
                    with open(self.fileName) as file:
                        while True:
                            line = file.readline()
                            st = self.write(clientSkt, line, 512)
                            if st == -1:
                                state = 0
                                break
                            if line == "":
                                break
                        file.write("")

                elif not cmd:
                    # connection close, wait for more.
                    state = 0
                    self.servSocket.listen(1)
                else:
                    # command not known
                    continue

    def read(self, socketS, msglen):
        """
        Get from data from the socket
        """
        chunks = []
        bytes_recd = 0
        while bytes_recd < msglen:
            chunk = socketS.recv(min(msglen - bytes_recd, 2048))
            if chunk == b'':
                return None
            chunks.append(chunk)
            bytes_recd += len(chunk)
        return ''.join(chunks)

    def write(self, socketS, data, msglen):
        """
        Send data to client
        """
        totalSent = 0
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
