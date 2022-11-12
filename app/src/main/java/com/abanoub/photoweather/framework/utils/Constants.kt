package com.abanoub.photoweather.framework.utils

interface Constants {
    object Constants {
        const val MEDIA_TYPE_IMAGE = 1
    }

    interface Location {
        object RECEIVER {
            const val INTENT_LOCATION: String = "INTENT_LOCATION"
        }
    }

    interface Network {
        object EndPoints {
            const val FIND = "find"
            const val COUNT = 1
            const val API_KEY = "e2a184cdb6b3cd8caf38d34f8401425f"
        }
    }
}