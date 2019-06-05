package com.zhhl.entry_exit.tour

import androidx.core.content.FileProvider

class PhotoProvider : FileProvider() {
    companion object{
        const val authentication="com.zhhl.entry_exit.tour.photoProvider"
    }
}