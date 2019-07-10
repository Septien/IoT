"""
Main module for the raspberry.
"""

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


def writeToFile(fileName, data):
    """
    """
    with open(fileName, 'a') as logger:
        data_writer = writer(logger)
        data_writer.write_row(data)

def main():
    user = False
    saveDHTData = True
    hclogFile = "hclog.csv";
    dhtlogFile = "dhtlog.csv"
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
    # Create locks
    dhtLock = t.Lock()
    hcLock = t.Lock()
    dhtExitL = t.Lock()
    hcExitL = t.Lock()
    # Create the thread for DHT sensor
    thread1 = sThread.SensorThread(1, "Thread1", qDatadht, dhtLock, qdhtExit, dhtExitL)
    # Create thread for HC-SR04 sensor
    thread2 = sThread.SensorThread(2, "Thread2", qDataHC, hcLock, qhcExit, hcExitL)
    # Start
    thread1.start()
    thread2.start()

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
            data = [pTime, hcData]
            if user:
                pass
            else:
                ledS.turnOnLED();
                writeToFile(hclogFile, data)
        # DHT data
        if saveDHTData:
            if dht22Data:
                if user:
                    pass
                else:
                    writeToFile(dhtlog, dht22Data)
        # Get params


if __name__ == '__main__':
    main()
