package ren.yanglei.androidthingsir

import com.google.android.things.pio.SpiDevice

object Util {
    /**
     * 每字节代表一次闪烁，当红外发射管以38KHz闪烁的时候，接收管才能收到信号
     * one byte per cycle, 1/38K second
     */
    val frequency = 38000 * 8
    val bitsPerWord = 8
    val bitJustification = SpiDevice.BIT_JUSTIFICATION_MSB_FIRST

    /**
     * 一次闪烁的信号,时长1/38K秒
     * one cycle of IR
     * 11110000
     */
    val IR = 0xf0.toByte()

    /**
     * 一次不闪烁的信号,时长1/38K秒
     * one cycle of PAUSE
     * 00000000
     */
    val PAUSE = 0.toByte()

    /**
     * 在乐高PF的文档里，一次低位（0）由6个IR和10个pause组成
     * In LEGO Power Functions RC document, Low bit consists of 6 cycles of IR and 10 “cycles” of pause
     */
    val LOW_BIT: ArrayList<Byte> = ArrayList<Byte>().apply {
        for (i in 1..6) {
            add(IR)
        }
        for (i in 1..10) {
            add(PAUSE)
        }

    }

    /**
     * 在乐高PF的文档里，一次高位（1）由6个IR和21个pause组成
     * In LEGO Power Functions RC document, high bit of 6 cycles IR and 21 “cycles” of pause
     */
    val HIGHT_BIT: ArrayList<Byte> = ArrayList<Byte>().apply {
        for (i in 1..6) {
            add(IR)
        }
        for (i in 1..21) {
            add(PAUSE)
        }
    }

    /**
     * 在乐高PF的文档里，开始或停止位由6个IR和39个pause组成
     * In LEGO Power Functions RC document, start bit of 6 cycles IR and 39 “cycles” of pause
     */
    val START_AND_STOP_BIT: ArrayList<Byte> = ArrayList<Byte>().apply {
        for (i in 1..6) {
            add(IR)
        }
        for (i in 1..39) {
            add(PAUSE)
        }
    }
}