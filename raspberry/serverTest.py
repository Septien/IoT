"""
Test the server process.
"""

import unittest
import socket
import threading as t
import queue as q
import select
import server as S
import time as tt

class testServer(unittest.TestCase):
    def setUp(self):
        """
        Init server and queues
        """
        self.serverQ = q.Queue(100)
        self.sQExit = q.Queue(1)
        self.serverLock = t.Lock()
        self.sExitL = t.Lock()
        self.server = S.ServerS(1, "Server", "log.txt", self.serverQ, self.serverLock, self.sQExit, self.sExitL)
        self.server.start()
        self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.client.connect(("127.0.0.1", 8081))

        with open("log.txt", "w") as file:
            file.write("DHT,23,23\n")
            file.write("DHT,24,24\n")
            file.write("HC, 12/34/56,1")

    def test_cmd_SET(self):
        sent = self.client.send(bytearray("SET", 'utf-8'))
        msgB = self.client.recv(3)
        msg = msgB.decode()
        assert msg == "ack"
        msg = "RS,1;LI,1;UI,1;SP,1;SV,1;TF,1"
        msgB = bytearray(msg, 'utf-8')
        sent = self.client.send(msgB)
        tt.Sleep(1)
        self.serverLock.acquire()
        while not self.serverLock.isEmpty():
            msg = self.serverQ.get()
            msgS = msg.aplit(",")
            assert len(msgS) == 2
            print(msgS)

    def test_cmd_GET(self):
        sent = self.client.send("GET")
        msgB = self.client.recv(15)
        msg = msgB.decode()
        msgS = msg.split(",")
        assert len(msgS) == 3
        msg = self.client.recv(15)
        msgS = msg.split(",")
        assert len(msgS) == 3
        msg = self.client.recv(15)
        msgS = msg.split(",")
        assert len(msgS) == 3

    def tearDown(self):
        self.client.close();
        self.server.join()

def suite():
    suite = unittest.TestSuite()
    suite.addTest(testServer('test_cmd_SET'))
    suite.addTest(testServer('test_cmd_GET'))
    return suite

if __name__ == '__main__':
    runner = unittest.TextTestRunner()
    runner.run(suite())
