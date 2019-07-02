class motor:
    def __init__(stallTorque,freeSpeed):
        stall = stallTorque
        free = freeSpeed
        torquePerSpeed = stallTorque/freeSpeed
        speedPerTorque = freeSpeed/stallTorque
    def getSpeed(torque):
        return speedPerTorque * torque;
    def getTorque(speed):
        return torquePerSpeed * speed
    
class inertial_disk:
    def __init__(rotational_inertia):
    
dataFile = open("cim-motor-curve-data-20151104.csv",'r')

print(dataFile.readline())
