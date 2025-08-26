package com.rodionov.nfchelper.nfccard

import com.rodionov.nfchelper.Password
import com.rodionov.nfchelper.R
import com.rodionov.resources.ResourceProvider

class Config(
    private val resourceProvider: ResourceProvider
) {

    enum class AntennaGain(val label: String, val value: Int) {
        ANTENNA_GAIN_UNKNOWN("неизв.", 0),
        ANTENNA_GAIN_18DB("18 дБ", 0x02),
        ANTENNA_GAIN_23DB("23 дБ", 0x03),
        ANTENNA_GAIN_33DB("33 дБ", 0x04),
        ANTENNA_GAIN_38DB("38 дБ", 0x05),
        ANTENNA_GAIN_43DB("43 дБ", 0x06),
        ANTENNA_GAIN_48DB("48 дБ", 0x07);

        override fun toString(): String {
            return label
        }

        companion object {
            private val BY_VALUE: MutableMap<Int?, AntennaGain?> = HashMap<Int?, AntennaGain?>()

            init {
                for (ag in entries) {
                    BY_VALUE.put(ag.value, ag)
                }
            }

            fun byValue(value: Int): AntennaGain? {
                return BY_VALUE.get(value)
            }

//            fun realValues(): Array<AntennaGain?> {
//                return entries.copyOfRange<AntennaGain?>(1, entries.toTypedArray().size)
//            }
        }
    }

    enum class ActiveModeDuration(private val label: String, val value: Int) {
        ACTIVE_MODE_1H("1 ч", 0),
        ACTIVE_MODE_2H("2 ч", 1),
        ACTIVE_MODE_4H("4 ч", 2),
        ACTIVE_MODE_8H("8 ч", 3),
        ACTIVE_MODE_16H("16 ч", 4),
        ACTIVE_MODE_32H("32 ч", 5),
        ACTIVE_MODE_ALWAYS("всегда", 6),
        ACTIVE_MODE_NEVER("никогда", 7);

        override fun toString(): String {
            return label
        }

        companion object {
            private val BY_VALUE: MutableMap<Int, ActiveModeDuration> =
                HashMap<Int, ActiveModeDuration>()

            init {
                for (e in entries) {
                    BY_VALUE.put(e.value, e)
                }
            }

            fun byValue(value: Int): ActiveModeDuration {
                return BY_VALUE[value] ?: ACTIVE_MODE_2H
            }
        }
    }

    var stationCode: Int = 0
    var activeModeDuration: ActiveModeDuration = ActiveModeDuration.ACTIVE_MODE_2H
    var startAsCheck: Boolean = false
    var checkCardInitTime: Boolean = false
    var autoSleep: Boolean = false
    var enableFastPunchForCard: Boolean = false
    var antennaGain: AntennaGain? = AntennaGain.ANTENNA_GAIN_33DB
    var password: Password = Password.defaultPassword()

    fun pack(): Array<ByteArray?>? {
        //ArrayList<Byte> configData = new ArrayList<>();

        var flags = activeModeDuration.value.toByte()

        if (startAsCheck) {
            flags = (flags.toInt() or 0x08).toByte()
        }
        if (checkCardInitTime) {
            flags = (flags.toInt() or 0x10).toByte()
        }
        if (autoSleep) {
            flags = (flags.toInt() or 0x20).toByte()
        }
        if (enableFastPunchForCard) {
            flags = (flags.toInt() or 0x80).toByte()
        }

        return arrayOf<ByteArray?>(
            byteArrayOf(
                stationCode.toByte(),
                flags,
                antennaGain!!.value.toByte(),
                password.getValue(2).toByte()
            ),
            byteArrayOf(password.getValue(1).toByte(), password.getValue(0).toByte(), 0, 0)
        )
    }

    override fun toString(): String {
        var str: String =
            resourceProvider.getString(R.string.config_station_no) + String.format(" <b>%s</b>", stationCode)
        when (stationCode) {
            START_STATION -> str += " " + resourceProvider.getString(R.string.config_start)
            FINISH_STATION -> str += " " + resourceProvider.getString(R.string.config_finish)
            CHECK_STATION -> str += " " + resourceProvider.getString(R.string.config_check)
            CLEAR_STATION -> str += " " + resourceProvider.getString(R.string.config_clear)
        }
        str += "\n\t" + resourceProvider.getString(R.string.config_active_time) + " " + activeModeDuration.toString()
        str += "\n\t" + resourceProvider.getString(R.string.config_flags)
        if (startAsCheck) {
            str += "\n\t\t" + resourceProvider.getString(R.string.config_start_as_check)
        }
        if (checkCardInitTime) {
            str += "\n\t\t" + resourceProvider.getString(R.string.config_check_init_time)
        }
        if (autoSleep) {
            str += "\n\t\t" + resourceProvider.getString(R.string.config_auto_sleep_flag)
        }
        if (enableFastPunchForCard) {
            str += "\n\t\t" + resourceProvider.getString(R.string.config_fast_punch_flag)
        }
        str += "\n\t" + resourceProvider.getString(R.string.config_antenna_gain_) + " " + antennaGain!!.label
        return str
    }

    companion object {
        const val START_STATION: Int = 240
        const val FINISH_STATION: Int = 245
        const val CHECK_STATION: Int = 248
        const val CLEAR_STATION: Int = 249

        fun unpack(configData: ByteArray, resourceProvider: ResourceProvider): Config {
            val config = Config(resourceProvider)
            config.stationCode = configData[0].toInt() and 0xFF

            config.activeModeDuration =
                ActiveModeDuration.Companion.byValue(configData[1].toInt() and 0x7)

            config.startAsCheck = (configData[1].toInt() and 0x08) > 0
            config.checkCardInitTime = (configData[1].toInt() and 0x10) > 0
            config.autoSleep = (configData[1].toInt() and 0x20) > 0
            config.enableFastPunchForCard = (configData[1].toInt() and 0x80) > 0

            config.antennaGain = AntennaGain.Companion.byValue(configData[2].toInt() and 0xFF)
            return config
        }
    }
}

