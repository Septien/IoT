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
        data_writer.writerow(data)

def main():
    user = False
    saveDHTData = True
    logFile = "logger.txt"
    # Get the sensors
    dht22S = dht22.DHT22()
    hcsr04S = hcsr04.HCSR04()
    ledS = led.LED()
    resetPB = push_button.PushButton(dht22S, ledS, 20)
    # Create the queues for each sensor
    qDatadht = Q.Queue(1000)
    qdhtExit = Q.Queue(1)
    qDataHC = Q.Queue(1000)
    qhcExit = Q.Queue(1)
    serverQ = Q.Queue(100)
    sQExit = Q.Queue(1)
    # Create locks
    dhtLock = t.Lock()
    hcLock = t.Lock()
    dhtExitL = t.Lock()
    hcExitL = t.Lock()
    serverLock = t.Lock()
    sExitL = t.Lock()
    # Create the thread for DHT sensor
    thread1 = sThread.SensorThread(1, "Thread1", qDatadht, dhtLock, qdhtExit, dhtExitL, dht22S)
    # Create thread for HC-SR04 sensor
    thread2 = sThread.SensorThread(2, "Thread2", qDataHC, hcLock, qhcExit, hcExitL, hcsr04S)
    # Thread for the server
    serverT = server.ServerS(3, "Server", logFile, serverQ, serverLock, sQExit, sExitL)
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
				if dht22Data[0] != None:
					data = [" DHT", dht22Data[0], dht22Data[1]]
					writeToFile(logFile, data)

        # Verify is there are new params
        data = []
        interval = []
        serverLock.acquire()
        # Get the new parameters
        while not serverQ.empty():
            data.append(serverQ.get())
        serverLock.release()
        if not data:
            continue
        # Modify params is necessary
        for d in data:
            dSplit = d.split(",")
            value = float(dSplit[1])
            if dSplit[0] == "RS" and value == 1:
                dht22S.setDefaults()
                ledS.turnOffLED()
            if dSplit[0] == "LI" and value != -1:
                interval.append(dSplit[1])
            if dSplit[0] == "UI" and value != -1:
                interval.append(dSplit[1])
            if dSplit[0] == "SP" and value != -1:
                dht22S.setPSampling(float(dSplit[1]))
            if dSplit[0] == "SV" and value != -1:
                saveDHTData = bool(int(dSplit[1]))
            if dSplit[0] == "TF" and value != -1:
                ledS.turnOffLED()
        #
        if len(interval) > 0:
            dht22S.setSamplingInterval(int(interval[1]), int(interval[2]))
        if not dht22S.withinInterval(time.time()):
            saveDHTData = False

if __name__ == '__main__':
    main()
