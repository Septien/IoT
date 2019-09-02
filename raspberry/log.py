import random as r
import time as t

with open('logger.txt', 'a') as fileT:
    for i in range(500):
        data = " DHT," + str(r.random()*100) + "," + str(r.random()*100) + "\n"
        fileT.write(data)
    data = " HC,1," + t.asctime() + "\n"
    fileT.write(data)
    for i in range(500):
        data = " DHT," + str(r.random()*100) + "," + str(r.random()*100) + "\n"
        fileT.write(data)
