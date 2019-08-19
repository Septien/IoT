import RPi.GPIO as gpio
import time
import btWrapper

class PushButton:
    def __init__(self, dht22, led, pin):
        self.dht22 = dht22
        self.led = led
        self.pin = pin
        gpio.setwarnings(False)
        gpio.setmode(gpio.BCM)
        gpio.setup(self.pin, gpio.IN, pull_up_down=gpio.PUD_DOWN)
        # Set callback
        gpio.add_event_detect(self.pin, gpio.RISING, callback=self.button_callback, bouncetime=10)

    def button_callback(channel):
        """
        Action to perform when the button is pressed
        """
        self.dht22.setDefaults()
        self.led.turnOffLED()
