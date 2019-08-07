"""
A class which contains all necessary stuff for creating
threads for the sensors.
"""

import threading as t
import queue as Q

class SensorThread(t.Thread):
    """
    Handles the thread for each sensor.
        -dataQ: Queue for sending the readings from the sensor
        -dataQLock: For writing on the dataQ.
        -exitQ: Indicates whether to end the thread.
        -exitQLock: Locks the exitQ.
    """
    def __init__(self, threadID, name, dataQ, dataQLock, exitQ, exitQLock, sensor):
        t.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.dataQ = dataQ
        self.exitQ = exitQ
        self.dataQLock = dataQLock
        self.exitQLock = exitQLock
        self.sensor = sensor

    def run(self):
        """
        Run Forrest! Run!
        """
        getOut = False
        while not getOut:
            self.exitQLock.acquire()
            if not self.exitQ.empty():
                r = self.exitQ.get()
                getOut = True
            self.exitQLock.release()
            # Get the data
            data = self.sensor.getReadings()
            if data == None:
                continue
            self.dataQLock.acquire()
            self.dataQ.put(data)
            self.dataQLock.release()
            self.sensor.Sleep()
