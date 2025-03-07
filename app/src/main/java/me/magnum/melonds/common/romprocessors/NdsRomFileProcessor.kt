package me.magnum.melonds.common.romprocessors

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Single
import me.magnum.melonds.common.uridelegates.UriHandler
import me.magnum.melonds.domain.model.Rom
import me.magnum.melonds.domain.model.RomConfig
import me.magnum.melonds.domain.model.RomInfo
import me.magnum.melonds.extensions.isBlank
import me.magnum.melonds.extensions.nameWithoutExtension
import me.magnum.melonds.utils.RomProcessor

class NdsRomFileProcessor(private val context: Context, private val uriHandler: UriHandler) : RomFileProcessor {

    override fun getRomFromUri(romUri: Uri, parentUri: Uri): Rom? {
        return try {
            getRomName(romUri)?.let { name ->
                val romDocument = uriHandler.getUriDocument(romUri)
                val romName = name.takeUnless { it.isBlank() } ?: romDocument?.nameWithoutExtension ?: ""
                Rom(romName, romDocument?.name ?: "", romUri, parentUri, RomConfig())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getRomIcon(rom: Rom): Bitmap? {
        return try {
            context.contentResolver.openInputStream(rom.uri)?.use { inputStream ->
                RomProcessor.getRomIcon(inputStream.buffered())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getRomInfo(rom: Rom): RomInfo? {
        return try {
            context.contentResolver.openInputStream(rom.uri)?.use { inputStream ->
                RomProcessor.getRomInfo(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getRealRomUri(rom: Rom): Single<Uri> {
        return Single.just(rom.uri)
    }

    private fun getRomName(uri: Uri): String? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            RomProcessor.getRomName(inputStream.buffered())
        }
    }
}