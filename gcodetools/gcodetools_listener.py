import os

print "it's alive"

while 1:
	if os.path.exists("/home/pi/Desktop/SVG2GCode/SVGReceived"):
                os.remove("/home/pi/Desktop/SVG2GCode/SVGReceived")	

	# print("1")

	while not os.path.exists("/home/pi/Desktop/SVG2GCode/SVGReceived"):
		pass

	# print("2")
	# os.system("ls -l /home/pi/Desktop/SVG2GCode")

	f = open("/home/pi/Desktop/SVG2GCode/execCommand", 'r')
	execCommand = f.readline()
	# print(execCommand)
	f.close()

	os.system("python " + execCommand)

	f = open("/home/pi/Desktop/SVG2GCode/gcodeDone", 'w')
	f.write("you are welcome ;)")
	f.close()

	# print("3")
	# os.system("ls -l /home/pi/Desktop/SVG2GCode")
