"""
This module contains a wrap up for the bluetooth packege BlueZ.
It works as a server.
"""

import bluetooth as bt
import os

def connBT():
    # Create the socket
    server_sock = bt.BluetoothSocket(bt.RFCOMM)

    # Turn on bluetooth
    os.system("rfkill unblock bluetooth")
    os.system("sudo hciconfig hci0 piscan")
    os.system("sudo sdptool add SP")

    server_sock.bind(("", bt.PORT_ANY))
    server_sock.listen(1)
    print("listening")

    uuid = "abcd"
    bt.advertise_service(server_sock, "Connection", uuid)

    client_sock, address = server_sock.accept()
    print("Accepted connection from %d" % address)

    data = client_sock.recv(1024)
    print("Recieved: [%s]" % data)

    # Write to file
    ndata = data.split(",")
    with open("/etc/wpa_supplicant.conf", "w") as wpa:
        wpa.write("ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev\n")
        wpa.write("update_config=1\n")
        wpa.write("country=MX\n\n")
        wpa.write("network={\n")
        wpa.write("ssid=%s" % ndata[0])
        wpa.write("psk=%s" % ndata[1])
        wpa.write("key_mgmt=WPA-PSK")
    
    # Connect
    os.system("sudo killall wpa_supplicant")
    os.system("sudo wpa_supplicant -iwlan0 -c/etc/wpa_supplicant.conf -B")
    client_sock.close()
    server_sock.close()
    # Turn off bluetooth
    os.system("rfkill block bluetooth")
    
