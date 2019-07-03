import RPi.GPIO as gpio
import time as t

class LED:
    def __init__(self):
        self.pin = 13
        gpio.setmode(gpio.BOARD)
        gpio.setup(self.pin, gpio.OUT)
        
    def turnOnLED(self):
        """
        Turn on LED
        """
        gpio.output(self.pin, True)
        
    def turnOffLED(self):
        """
        """
        gpio.output(self.pin, False)
