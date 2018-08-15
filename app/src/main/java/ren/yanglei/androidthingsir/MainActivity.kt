package ren.yanglei.androidthingsir

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.SpiDevice
import java.io.IOException

/**
 * 使用SPI的MOSI口作为输出
 * Use the MOSI port of the SPI as the output
 */
class MainActivity : Activity() {
    private val SPI_DEVICE_NAME: String = "SPI0.0"
    private lateinit var mSPIDevice: SpiDevice


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val manager: PeripheralManager = PeripheralManager.getInstance()
            mSPIDevice = manager.openSpiDevice(SPI_DEVICE_NAME)
            configureSpiDevice(mSPIDevice)

            //init and send order
            val order = initOrder()
            mSPIDevice.write(order, order.size)

        } catch (e: IOException) {
            Log.w(TAG, "Unable to access SPI device", e);
        }

    }

    /**
     * 拼接命令DEMO，乐高的命令由开始标志+3段命令+校验位+结束标志组成
     * Lego's order consists of START_BIT + Nibble1 + Nibble2 + Nibble3 + LRC + STOP_BIT
     * LRC(4 bit) = 0xF xor Nibble 1 xor Nibble 2 xor Nibble 3
     */
    private fun initOrder(): ByteArray {
        val nibble1 = 0b0100
        val nibble2 = 0b1010
        val nibble3 = 0b0000
        val lrc = 0xff.xor(nibble1).xor(nibble2).xor(nibble3)
        val signal = ArrayList<Byte>()
        signal.addAll(Util.START_AND_STOP_BIT)
        signal.addAll(if (nibble1.and(0b1000) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble1.and(0b0100) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble1.and(0b0010) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble1.and(0b0001) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble2.and(0b1000) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble2.and(0b0100) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble2.and(0b0010) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble2.and(0b0001) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble3.and(0b1000) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble3.and(0b0100) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble3.and(0b0010) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (nibble3.and(0b0001) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (lrc.and(0b1000) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (lrc.and(0b0100) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (lrc.and(0b0010) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(if (lrc.and(0b0001) != 0) Util.HIGHT_BIT else Util.LOW_BIT)
        signal.addAll(Util.START_AND_STOP_BIT)
        return signal.toByteArray()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::mSPIDevice.isInitialized) {
                mSPIDevice.close()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Unable to access SPI device", e);
        }
    }

    @Throws(IOException::class)
    fun configureSpiDevice(device: SpiDevice) {
        device.setMode(SpiDevice.MODE0)
        device.setFrequency(Util.frequency)
        device.setBitsPerWord(Util.bitsPerWord)
        device.setBitJustification(Util.bitJustification)
    }
}
