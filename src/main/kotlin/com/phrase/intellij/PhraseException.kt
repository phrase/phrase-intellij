package com.phrase.intellij

abstract class PhraseException:Exception{
    constructor():super()
    constructor(message: String):super(message)
    constructor(cause: Throwable):super(cause)
}

class PhraseClientNotFoundException:PhraseException()
class PhraseConfigurationNotFoundException:PhraseException()
class PhraseLoadConfigurationException(cause: Throwable):PhraseException(cause)
class PhraseSaveConfigurationException(cause: Throwable):PhraseException(cause)
class PhraseApiException(message: String):PhraseException(message)