"""
Main module for the raspberry.
"""

import os
import csv
from csv import writer
import time
import threading as t
import dht22
import hcsr04
import sThread
import led
import push_button
import queue as Q
import server


def writeToFile(fileName, data):
    """
    """
    with open(fileName, 'a') as logger:
        data_writer = writer(logger)
        data_writer.write_row(data)

def connectWiFi():
    """
    Use wpa_supplicant to connect to Wi-Fi service
    """
    os.system("sudo wpa_supplicant -iwlan0 -c/etc/wpa_supplicant.conf -B")

def main():
    connectWiFi()
    user = False
    saveDHTData = True
    logFile = "logger.txt"
    # Get the sensors
    dht22S = dht22.DHT22()
    hcsr04S = hcsr04.HCSR04()
    ledS = led.LED()
    resetPB = push_button.PushButton(dht22S, ledS, 11)
    # Create the queues for each sensor
    qDatadht = q.Queue(1000)
    qdhtExit = q.Queue(1)
    qDataHC = q.Queue(1000)
    qhcExit = q.Queue(1)
    serverQ = q.Queue(100)
    sQExit = q.Queue(1)
    # Create locks
    dhtLock = t.Lock()
    hcLock = t.Lock()
    dhtExitL = t.Lock()
    hcExitL = t.Lock()
    serverLock = t.Lock()
    sExitL = t.Lock()
    # Create the thread for DHT sensor
    thread1 = sThread.SensorThread(1, "Thread1", qDatadht, dhtLock, qdhtExit, dhtExitL)
    # Create thread for HC-SR04 sensor
    thread2 = sThread.SensorThread(2, "Thread2", qDataHC, hcLock, qhcExit, hcExitL)
    # Thread for the server
    serverT = server.ServerS(3, "Server", logFile, serverQ, sQExit, sExitL)
    # Start
    thread1.start()
    thread2.start()
    serverT.start()

    endProcess = False

    while not endProcess:
        dht22Data = None
        # Is there data from DHT22?
        dhtLock.acquire()
        if not qDatadht.empty():
            dht22Data = qDatadht.get()
        dhtLock.release()
        # Is there data from HC-SR04?
        hcData = None
        hcLock.acquire()
        if not qDataHC.empty():
            hcData = qDataHC.get()
        hcLock.release()
        # Was there a presence?
        if hcData:
            pTime = time.asctime()
            data = ["HC", hcData, pTime]
            ledS.turnOnLED();
            writeToFile(logFile, data)
        # DHT data
        if saveDHTData:
            if dht22Data:
                data = ["DHT", dht22Data[0], dht22Data[1]]
                writeToFile(logFile, data)

        # Get params
        serverLock.acquire()
        if not serverQ.empty():
            data = serverQ.get()
        serverLock.release()
        if data[0] == "1":
            dht22S.setDefaults()
            self.led.turnOffLED()
        dht22S.setSamplingInterval(int(data[1]), int(data[2]))
        dht22S.setPSampling(int(data[3]))
        saveDHTData = bool(int(data[4]))

if __name__ == '__main__':
    main()
