package com.example.vpassport.viewmodel

import android.content.ContentValues.TAG
import android.nfc.tech.IsoDep
import android.util.Log
import com.example.vpassport.model.data.DataGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.PassportService.DEFAULT_MAX_BLOCKSIZE
import org.jmrtd.PassportService.NORMAL_MAX_TRANCEIVE_LENGTH
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File

class PassportReader() {

    suspend fun getDataGroup(isoDep: IsoDep, documentNumber : String, dateOfBirth : String, dateOfExpiry : String) : DataGroup {
        return withContext(Dispatchers.IO) {
            try {
                val bacKey = createBACKey(documentNumber, dateOfBirth, dateOfExpiry)
                val cardService = CardService.getInstance(isoDep)
                cardService.open()
                val passportService = PassportService(
                    cardService,
                    NORMAL_MAX_TRANCEIVE_LENGTH,
                    DEFAULT_MAX_BLOCKSIZE,
                    true,
                    false
                )
                passportService.open()
                doPACEorBAC(passportService = passportService, bacKey)
                val dG1File = readDG1File(passportService)
                val dG2File = readDG2File(passportService)
                DataGroup(dG1File, dG2File)
            } catch (e: Exception) {
                throw e
            }
        }
    }



    private fun createBACKey(documentNumber : String, dateOfBirth : String, dateOfExpiry : String) : BACKey {
        return BACKey(documentNumber, dateOfBirth, dateOfExpiry)
    }

    private suspend fun doPACEorBAC(passportService: PassportService, bacKey: BACKeySpec) {
        var doPACE = false
        try {
            val cardSecurity = CardSecurityFile(passportService.getInputStream(PassportService.EF_CARD_SECURITY, DEFAULT_MAX_BLOCKSIZE))
            for (info in cardSecurity.getSecurityInfos()) {
                if (info is PACEInfo) {
                    passportService.doPACE(bacKey, info.objectIdentifier, PACEInfo.toParameterSpec(info.parameterId), null)
                    doPACE = true
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }

        passportService.sendSelectApplet(doPACE)

        if (!doPACE) {
            try {
                passportService.getInputStream(PassportService.EF_COM).read()
            } catch (e: Exception) {
                passportService.doBAC(bacKey)
            }
        }
    }

    private suspend fun readDG1File(passportService: PassportService) : DG1File {
        val dg1  = passportService.getInputStream(PassportService.EF_DG1, DEFAULT_MAX_BLOCKSIZE)
        return DG1File(dg1)
    }
    private suspend fun readDG2File(passportService: PassportService) : DG2File {
        val dg2  = passportService.getInputStream(PassportService.EF_DG2, DEFAULT_MAX_BLOCKSIZE)
        return DG2File(dg2)
    }
}
