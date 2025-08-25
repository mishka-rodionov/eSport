package com.rodionov.nfchelper.nfccard

import android.annotation.SuppressLint
import com.rodionov.nfchelper.R
import com.rodionov.resources.ResourceProvider
import com.rodionov.resources.R as RRes
import java.util.Calendar
import java.util.Date
import kotlin.math.abs

internal enum class Mode {
    ACTIVE,
    WAIT,
    SLEEP
}

class State(data: Array<ByteArray>, private val resourceProvider: ResourceProvider) {
    class Version(major: Int, minor: Int, patch: Int) {
        // Sportiduino version.
        private val major: Int
        private val minor: Int
        private val patch: Int

        init {
            // Initializes version by bytes from master station.
            // params: major, minor, patch: Bytes from master station.
            if (minor == 0 && patch == 0) {    // old firmwares v1.0 - v2.6
                if (major >= 100 && major <= 104) {   // v1.0 - v1.4
                    this.major = major / 100
                    this.minor = major % 100
                    this.patch = 0
                } else {
                    this.major = (major shr 6) + 1
                    this.minor = ((major shr 2) and 0x0F) + 1
                    this.patch = major and 0x03
                }

            } else {
                this.major = major
                this.minor = minor
                this.patch = patch
            }
        }

        constructor(major: Byte, minor: Byte, patch: Byte) : this(
            Util.byteToUint(major),
            Util.byteToUint(minor),
            Util.byteToUint(patch)
        )

        @SuppressLint("DefaultLocale")
        override fun toString(): String {
            // return: User friendly version string.
            var suffix = this.patch.toString()
            val MAX_PATCH_VERSION = 239
            if (this.patch > MAX_PATCH_VERSION) {
                suffix = String.format("0-beta.%d", (this.patch - MAX_PATCH_VERSION))
            }
            return String.format("v%d.%d.%s", this.major, this.minor, suffix)
        }
    }

    inner class Battery(batteryByte: Int) {
        private val lipoVoltageGraph = arrayOf<FloatArray?>(
            floatArrayOf(3.25f, 3.35f, 3.45f, 3.55f, 3.75f, 3.95f, 4.10f),
            floatArrayOf(0f, 5f, 20f, 40f, 60f, 95f, 100f)
        )
        private var voltage = 0f
        val isOk: Boolean

        private fun getChargeLevel(v: Float): Int {
            var low = 0
            val n = lipoVoltageGraph[0]!!.size - 1
            var high = n
            while (low < high) {
                val mid = (low + high) / 2
                if (v > lipoVoltageGraph[0]!![mid]) {
                    low = mid + 1
                } else {
                    high = mid
                }
            }
            if (low > 0 && low < n) {
                val x0 = lipoVoltageGraph[0]!![low - 1]
                val x1 = lipoVoltageGraph[0]!![low]
                val y0 = lipoVoltageGraph[1]!![low - 1]
                val y1 = lipoVoltageGraph[1]!![low]
                return (y0 + (v - x0) * (y1 - y0) / (x1 - x0)).toInt()
            }
            return lipoVoltageGraph[1]!![low].toInt()
        }

        init {
            if (batteryByte == 0 || batteryByte == 1) {
                // Old firmware
                this.isOk = (batteryByte > 0)
            } else {
                voltage = (batteryByte).toFloat() / 50f
                this.isOk = (voltage > 3.6f)
            }
        }

        override fun toString(): String {
            var voltageText = ""
            if (voltage > 0) {
                voltageText = " - " + String.format(
                    resourceProvider.getString(R.string.battery_volts),
                    getChargeLevel(voltage),
                    voltage
                )
            }

            if (this.isOk) {
                return Util.coloredHtmlString(
                    resourceProvider.getString(R.string.battery_ok) + voltageText,
                    resourceProvider.colorToHexCode(RRes.color.green)
                )
            }
            return Util.coloredHtmlString(
                resourceProvider.getString(R.string.battery_low) + voltageText,
                resourceProvider.colorToHexCode(RRes.color.red)
            )
        }
    }

    private var version: Version? = null
    private var config: Config? = null
    private var mode: Mode? = null
    private var battery: Battery? = null
    private var timestamp: Long = 0
    private var wakeupTimestamp: Long = 0
    private var isEmpty: Boolean = false

    init {
        try {
            if ( data.isEmpty() || data[0][0].toInt() == 0) {
                isEmpty = true
            } else {
                version = Version(data[0][0], data[0][1], data[0][2])
                config = Config.unpack(data[1])
                battery = Battery(data[2][0].toInt() and 0xFF)
                mode = Mode.entries[data[2][1].toInt()]
                timestamp = Util.toUint32(data[3])
                wakeupTimestamp = Util.toUint32(data[4])
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            isEmpty = true
        }
    }

    override fun toString(): String {
        if (isEmpty) {
            return "Empty"
        }
        var stringState: String = resourceProvider.getString(R.string.version_) + " " + version.toString()
        stringState += resourceProvider.getString(R.string.state_config_) + config.toString()
        stringState += resourceProvider.getString(R.string.state_battery_) + " " + battery.toString()
        stringState += resourceProvider.getString(R.string.state_mode_) + " " + Util.capitalize(mode!!.name)
        var clockStr = Util.dformat.format(Date(timestamp * 1000))
        val nowSec = Calendar.getInstance().getTimeInMillis() / 1000
        val delta = abs(nowSec - timestamp)
        var deltaStr = " (" + delta + " s)"
        if (delta > 60) {
            deltaStr = " (> 60 s)"
        }
        clockStr += deltaStr
        if (timestamp < (nowSec - 20) || timestamp > (nowSec + 17)) {
            clockStr = Util.coloredHtmlString(clockStr, resourceProvider.colorToHexCode(RRes.color.red))
        } else if (timestamp < (nowSec - 5) || timestamp > nowSec) {
            clockStr = Util.coloredHtmlString(clockStr, resourceProvider.colorToHexCode(RRes.color.yellow))
        }
        stringState += resourceProvider.getString(R.string.state_clock_) + " " + clockStr
        stringState += resourceProvider.getString(R.string.state_alarm_) + " " + Util.dformat.format(
            Date(
                wakeupTimestamp * 1000
            )
        )
        return stringState
    }
}

