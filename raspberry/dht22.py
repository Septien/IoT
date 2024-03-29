"""
This module contain a class which contains the pertinent parameters
for reading the DHT22 sensor using the Adafruit library.
"""

import Adafruit_DHT as dht
from time import sleep
import time as t
import sensor as S

class DHT22(S.Sensor):
    def __init__(self):
        super().__init__()
        self.dht = dht.DHT22
        self.pin = 18
        self.pSampling = 30 #s
        self.initTime = 0
        self.endTime = 0

    def setPSampling(self, period):
        """
        Set the period of sampling, which will be in the
        interval [30, 300] seconds (0.5 and 5 min).
        """
        if (period < 30 or period > 300):
            return
        self.pSampling = period

    def setSamplingInterval(self, initTime, endTime):
        """
        Sets the interval of time on which the sensor
        will obtain the data
        """
        if initTime > endTime:
            return
        self.initTime = initTime
        self.endTime = endTime

    def setDefaults(self):
        """
        Set default parameters
        """
        self.pSampling = 30
        self.initTime = 0
        self.endTime = 0

    def getReadings(self):
        """
        Get the readings from the sensor and
        returns it
        """
        if self.withinInterval(t.time()):
            hum, temp = dht.read(self.dht, self.pin)
            return [temp, hum]
        return None

    def Sleep(self):
        """
        Sleep for the stablished period of time
        """
        sleep(self.pSampling)

    def withinInterval(self, t):
        """
        Checks whether the time t is within interval.
        If initTime == endTime, return true.
        """
        if (self.initTime == self.endTime):
            return True
        if (self.initTime <= t and t <= self.endTime):
            return True
        else:
            return False
