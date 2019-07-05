"""
Main module for the raspberry.
"""

import csv
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
    # Get the sensors
    dht22S = dht22.DHT22()
    hcsr04S = hcsr04.HCSR04()
    ledS = led.LED()
    pb = push_button.PushButton(dht22S, ledS)
    # Create the queues for each sensor
    qDatadht = q.Queue(1000)
    qdhtExit = q.Queue(1)
    qDataHC = q.Queue(1000)
    qhcExit = q.Queue(1)
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
        dhtLock.acquire()
        if not qDatadht.empty():
            data = qDatadht.get()
        dhtLock.release()

if __name__ == '__main__':
    main()
