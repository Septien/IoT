"""
Module for obtaining the readings from the
HC-SR04 ultrasonic sensor.
"""
import RPi.GPIO as gpio
import time as t
import sensor as S

class HCSR04(S.Sensor):
    def __init__(self):
        super().__init__()
        # Set interface pins
        self.gpio_trigger = 25
        self.gpio_echo = 24

        # Set gpio mode
        gpio.setmode(gpio.BCM)

        # set gpio direction
        gpio.setup(self.gpio_trigger, gpio.OUT)
        gpio.setup(self.gpio_echo, gpio.IN)

    def getReadings(self):
        """
        Get the read distance from the sensor
        """
        # Set Trigger to high
        gpio.output(self.gpio_trigger, True)

        # Set Trigger after 0.01ms to low
        t.sleep(0.000001)
        gpio.output(self.gpio_trigger, False)

        StartTime = t.time()
        StopTime = t.time()

        # save StartTime
        while gpio.input(self.gpio_echo) == 0:
            StartTime = t.time()
            
        # save time of arrival
        while gpio.input(self.gpio_echo) == 1:
            StopTime = t.time()

        timeElapsed = StopTime - StartTime
        distance = (timeElapsed * 34300) / 2

        return (distance <= 400)

    def Sleep(self):
        """
        For compatibility only
        """
        pass
