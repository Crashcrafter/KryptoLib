package dev.crash.tx.signed

import dev.crash.tx.raw.RawEthTransaction

class SignedEthTransaction internal constructor(rawTx: RawEthTransaction): SignedTransaction(rawTx) {

}