package com.example.localization;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;


public class DetectMotion {

    private static int bufferSize = 50;



    private static ReentrantLock accBufferLock = new ReentrantLock();
    private static ReentrantLock magBufferLock = new ReentrantLock();

    //writes the sensor values into the buffers
    public static void writeData(SensorEvent event) {

         ArrayList<Double> xBuffer = new ArrayList<Double>( bufferSize);
         ArrayList<Double> yBuffer = new ArrayList<Double>( bufferSize);
         ArrayList<Double> zBuffer = new ArrayList<Double>( bufferSize);
         ArrayList<Double> azimuthBuffer = new ArrayList<Double>( bufferSize);

        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            accBufferLock.lock();
            try {
                //write acc data into buffers
                if (xBuffer.size() >=bufferSize )
                    xBuffer.remove(0);
                xBuffer.add((double) event.values[0]);

                if (yBuffer.size() >= bufferSize)
                    yBuffer.remove(0);
                yBuffer.add((double) event.values[1]);

                if (zBuffer.size() >= bufferSize)
                    zBuffer.remove(0);
                zBuffer.add((double) event.values[2]);


            }finally {
                accBufferLock.unlock();
            }


        }else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD){

            float[] lastAccelerometerReading = new float[3];
            float[] lastMagnetometerReading = new float[3];
            float[] rotationMatrix = new float[9];
            float[] orientationAngles = new float[3];
            accBufferLock.lock();
            try{
                //check if accelerometer data is there(checking 1 axis buffer is sufficient)
                if (xBuffer.isEmpty()){
                    return;
                }
                //get the last accelerometer readings
                lastAccelerometerReading[0] = (float) xBuffer.get(xBuffer.size() - 1).doubleValue();
                lastAccelerometerReading[1] = (float) yBuffer.get(yBuffer.size() - 1).doubleValue();
                lastAccelerometerReading[2] = (float) zBuffer.get(zBuffer.size() - 1).doubleValue();

            }finally {
                accBufferLock.unlock();

            }
            magBufferLock.lock();
            try {
                //get the last magnetometer reading
                System.arraycopy(event.values, 0, lastMagnetometerReading, 0, lastMagnetometerReading.length);
                //compute the device orientation
                SensorManager.getRotationMatrix(rotationMatrix,null, lastAccelerometerReading,lastMagnetometerReading );
                /* in the above line we the vectors are transformed from the device cordinate system to the earths cordinate system*/
                SensorManager.getOrientation(rotationMatrix,orientationAngles);
                /*computes devise orientation from the rotation matrix, where, values[0] -Azimuth, angle of rotation about the -z axis.the angle between the device's y axis and the magnetic north pole. When facing north, this angle is 0, when facing south, this angle is π. Likewise, when facing east, this angle is π/2, and when facing west, this angle is -π/2. The range of values is -π to π.*/

                //extracting the azimuth values from the orientaionAngles array
                double azimuth = (Math.toDegrees(orientationAngles[0]));

                //write azimuth data into buffer
                if (azimuthBuffer.size() >= bufferSize)
                    azimuthBuffer.remove(0);
                azimuthBuffer.add(azimuth);
                System.out.println("New azimuth is " + azimuth);

            }finally {
                magBufferLock.unlock();

            }
        }
    }
}

