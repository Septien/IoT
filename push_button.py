import RPi.GPIO as gpio
import time

class PushButton:
    def __init__(self):
        gpio.setmode(gpio.BOARD)
        gpio.setup(11, gpio.IN, pull_up_down=gpio.PUD_DOWN)
        gpio.add_event_detect(11, gpio.RISING, callback=self.button_callback, bouncetime=10)

    def button_callback(channel):
        """
        Action to perform when the button is pressed
        """
        pass
