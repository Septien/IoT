import RPi.GPIO as gpio
import time

class PushButton:
    def __init__(self, dht22, led):
        self.dht22 = dht22
        self.led = led
        gpio.setmode(gpio.BOARD)
        gpio.setup(11, gpio.IN, pull_up_down=gpio.PUD_DOWN)
        gpio.add_event_detect(11, gpio.RISING, callback=self.button_callback, bouncetime=10)

    def button_callback(channel):
        """
        Action to perform when the button is pressed
        """
        self.dht22.setDefaults()
        self.led.turnOffLED()