package com.example.vpassport.viewmodel

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import com.example.vpassport.model.data.DataGroupBundle
import com.example.vpassport.model.data.websocket.CAData
import com.example.vpassport.util.MediatorPassportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.PassportService.DEFAULT_MAX_BLOCKSIZE
import org.jmrtd.PassportService.NORMAL_MAX_TRANCEIVE_LENGTH
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.SODFile
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File
import java.security.KeyPair

class PassportReader(
    private val tag: Tag
) {

    companion object {
        private const val TAG = "PassportReader"
    }

    private val cardService: CardService
    private val passportService: MediatorPassportService

    init {
        val isoDep = IsoDep.get(tag)
        isoDep.timeout = 10000
        cardService = CardService.getInstance(isoDep)
        passportService = MediatorPassportService(
            cardService,
            NORMAL_MAX_TRANCEIVE_LENGTH,
            DEFAULT_MAX_BLOCKSIZE,
            true,
            false
        )
        cardService.open()
        passportService.open()
    }

    /**
     * Retrieves data groups needed from the detected passport.
     *
     * @param documentNumber passport document number.
     * @param dateOfBirth user's date of birth.
     * @param dateOfExpiry passport's date of expiry.
     */
    suspend fun getDataGroups(
        documentNumber: String,
        dateOfBirth: String,
        dateOfExpiry: String
    ): DataGroupBundle {
        return withContext(Dispatchers.IO) {
            try {
                val bacKey = createBACKey(documentNumber, dateOfBirth, dateOfExpiry)
                doPACEorBAC(bacKey)
                val dG1File = readDG1File(passportService)
                val dG2File = readDG2File(passportService)
                val dG14File = readDG14File(passportService)
                val sodFile = readSODFile(passportService)
                DataGroupBundle(dG1File, dG2File, dG14File, sodFile)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun createBACKey(
        documentNumber: String,
        dateOfBirth: String,
        dateOfExpiry: String
    ): BACKey {
        return BACKey(documentNumber, dateOfBirth, dateOfExpiry)
    }

    /**
     * Initiate secure communication channel with PACE or BAC Protocol.
     *
     * @param passportService current passportService instance.
     * @param bacKey static key given by the user.
     */
    private fun doPACEorBAC(bacKey: BACKeySpec) {
        var isPACESupported = false
        try {
            val cardSecurity = CardSecurityFile(
                passportService.getInputStream(
                    PassportService.EF_CARD_SECURITY,
                    DEFAULT_MAX_BLOCKSIZE
                )
            )
            for (info in cardSecurity.securityInfos) {
                if (info is PACEInfo) {
                    passportService.doPACE(
                        bacKey,
                        info.objectIdentifier,
                        PACEInfo.toParameterSpec(info.parameterId),
                        null
                    )
                    isPACESupported = true
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "PACE not supported, reverting to BAC")
        }

        passportService.sendSelectApplet(isPACESupported)
        if (!isPACESupported) {
            passportService.doBAC(bacKey)
        }
    }

    fun livelinessTest(
        pcdKeyPair: KeyPair,
        dataGroup: DataGroupBundle
    ): ByteArray? {
        try {
            val dg14 = dataGroup.dG14File
            for (securityInfo in dg14.securityInfos) {
                if (securityInfo is ChipAuthenticationPublicKeyInfo) {
                    passportService.doEACCA(
                        securityInfo.keyId,
                        ChipAuthenticationPublicKeyInfo.ID_CA_ECDH_AES_CBC_CMAC_256,
                        securityInfo.protocolOIDString,
                        securityInfo.subjectPublicKey)
                    return passportService.livelinessTest(
                        securityInfo.keyId,
                        ChipAuthenticationPublicKeyInfo.ID_CA_ECDH_AES_CBC_CMAC_256,
                        securityInfo.protocolOIDString,
                        securityInfo.subjectPublicKey,
                        pcdKeyPair
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return null
    }

    private fun readDG1File(passportService: PassportService): DG1File {
        val dg1 = passportService.getInputStream(PassportService.EF_DG1, DEFAULT_MAX_BLOCKSIZE)
        return DG1File(dg1)
    }

    private fun readDG2File(passportService: PassportService): DG2File {
        val dg2 = passportService.getInputStream(PassportService.EF_DG2, DEFAULT_MAX_BLOCKSIZE)
        return DG2File(dg2)
    }

    private fun readDG14File(passportService: PassportService): DG14File {
        val dg14 = passportService.getInputStream(PassportService.EF_DG14, DEFAULT_MAX_BLOCKSIZE)
        return DG14File(dg14)
    }

    private fun readSODFile(passportService: PassportService): SODFile {
        val sodFile = passportService.getInputStream(PassportService.EF_SOD, DEFAULT_MAX_BLOCKSIZE)
        return SODFile(sodFile)
    }
}
