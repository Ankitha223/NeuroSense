package com.example.neurosense.data

import kotlin.random.Random

class SimulatedSensorRepository {

    fun getCurrentSensorData(): SensorData {

        return SensorData(

            timestamp = System.currentTimeMillis(),

            // BMI270
            bmi270X = Random.nextFloat() * 2f - 1f,
            bmi270Y = Random.nextFloat() * 2f - 1f,
            bmi270Z = 9.8f + (Random.nextFloat() * 0.4f - 0.2f),

            // MPU9250 Accelerometer
            mpu9250AccelX = Random.nextFloat() * 2f - 1f,
            mpu9250AccelY = Random.nextFloat() * 2f - 1f,
            mpu9250AccelZ = 9.8f + (Random.nextFloat() * 0.4f - 0.2f),

            // MPU9250 Gyroscope
            mpu9250GyroX = Random.nextFloat() * 4f - 2f,
            mpu9250GyroY = Random.nextFloat() * 4f - 2f,
            mpu9250GyroZ = Random.nextFloat() * 4f - 2f,

            // MPU9250 Magnetometer
            mpu9250MagX = Random.nextFloat() * 60f - 30f,
            mpu9250MagY = Random.nextFloat() * 60f - 30f,
            mpu9250MagZ = Random.nextFloat() * 60f - 30f,

            // FSR402
            fsr402Force = Random.nextFloat() * 10f,

            // FlexiForce A201
            flexiForcePressure = Random.nextFloat() * 10f
        )
    }
}