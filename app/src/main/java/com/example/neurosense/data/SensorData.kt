package com.example.neurosense.data

data class SensorData(
    val timestamp: Long,

    // BMI270
    val bmi270X: Float,
    val bmi270Y: Float,
    val bmi270Z: Float,

    // MPU9250
    val mpu9250AccelX: Float,
    val mpu9250AccelY: Float,
    val mpu9250AccelZ: Float,

    val mpu9250GyroX: Float,
    val mpu9250GyroY: Float,
    val mpu9250GyroZ: Float,

    val mpu9250MagX: Float,
    val mpu9250MagY: Float,
    val mpu9250MagZ: Float,

    // FSR402
    val fsr402Force: Float,

    // FlexiForce A201
    val flexiForcePressure: Float
)