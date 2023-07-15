package com.example.vpassport.model.data

import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File

data class DataGroup(
    val dG1File: DG1File,
    val dG2File: DG2File
)
