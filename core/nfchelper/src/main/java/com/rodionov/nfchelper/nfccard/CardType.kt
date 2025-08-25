package com.rodionov.nfchelper.nfccard

enum class CardType(val value: Int = 0) {
    UNKNOWN,
    ORDINARY,
    MASTER_GET_STATE(0xF9),
    MASTER_SET_TIME(0xFA),
    MASTER_SET_NUMBER(0xFB),
    MASTER_SLEEP(0xFC),
    MASTER_READ_BACKUP(0xFD),
    MASTER_CONFIG(0xFE),
    MASTER_PASSWORD(0xFF);

    companion object {
        private val BY_VALUE: MutableMap<Int, CardType> = HashMap<Int, CardType>()

        init {
            for (ct in entries) {
                BY_VALUE.put(ct.value, ct)
            }
        }

        fun byValue(value: Int): CardType {
            if (!BY_VALUE.containsKey(value)) {
                return UNKNOWN
            }
            return BY_VALUE.get(value) ?: UNKNOWN
        }
    }
}
