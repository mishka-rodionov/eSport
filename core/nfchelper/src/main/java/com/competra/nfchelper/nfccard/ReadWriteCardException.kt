package com.competra.nfchelper.nfccard

class ReadWriteCardException : Exception {
    constructor(errorMsg: String?) : super(errorMsg)

    constructor() : super()
}
