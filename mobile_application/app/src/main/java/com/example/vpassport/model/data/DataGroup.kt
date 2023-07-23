package com.example.vpassport.model.data

import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File

data class DataGroup(
    val dG1File: DG1File,
    val dG2File: DG2File,
    val dG14File: DG14File
)
