package com.rodionov.nfchelper

class Password {
    private var passwordArray = intArrayOf(0, 0, 0)

    constructor(array: IntArray) {
        if (array.size > 2) {
            passwordArray = array.copyOf(3)
        }
    }

    constructor(pass1: Int, pass2: Int, pass3: Int) {
        passwordArray = intArrayOf(pass1, pass2, pass3)
    }

    override fun toString(): String {
        var str = ""
        for (i in passwordArray.indices) {
            if (i > 0) {
                str += "-"
            }
            str += passwordArray[i].toString()
        }
        return str
    }

    fun toByteArray(): ByteArray {
        return byteArrayOf(
            passwordArray[2].toByte(),
            passwordArray[1].toByte(),
            passwordArray[0].toByte()
        )
    }

    fun getValue(index: Int): Int {
        if (0 <= index && index < 3) {
            return passwordArray[index]
        }
        return 0
    }

    companion object {
        fun defaultPassword(): Password {
            return Password(0, 0, 0)
        }

        fun parseValue(value: String): Int {
            try {
                return value.toInt()
            } catch (e: NumberFormatException) {
                return 0
            }
        }

        fun fromString(str: String?): Password {
            if (str != null) {
                val split: Array<String?> =
                    str.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split.size > 2) {
                    val array = IntArray(split.size)
                    for (i in split.indices) {
                        array[i] = split[i]!!.toInt()
                    }
                    return Password(array)
                }
            }
            return Password(0, 0, 0)
        }
    }
}
