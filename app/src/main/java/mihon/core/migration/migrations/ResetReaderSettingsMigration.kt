package mihon.core.migration.migrations

import android.app.Application
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext
import tachiyomi.core.common.util.lang.withIOContext

class ResetReaderSettingsMigration : Migration {
    override val version: Float = 17f

    override suspend fun invoke(migrationContext: MigrationContext): Boolean = withIOContext {
        val context = migrationContext.get<Application>() ?: return@withIOContext false
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        // Migrate Rotation and Viewer values to default values for viewer_flags
        val newOrientation = when (prefs.getInt("pref_rotation_type_key", 1)) {
            1 -> ReaderOrientation.FREE.flagValue
            2 -> ReaderOrientation.PORTRAIT.flagValue
            3 -> ReaderOrientation.LANDSCAPE.flagValue
            4 -> ReaderOrientation.LOCKED_PORTRAIT.flagValue
            5 -> ReaderOrientation.LOCKED_LANDSCAPE.flagValue
            else -> ReaderOrientation.FREE.flagValue
        }

        // Reading mode flag and prefValue is the same value
        val newReadingMode = prefs.getInt("pref_default_viewer_key", 1)

        prefs.edit {
            putInt("pref_default_orientation_type_key", newOrientation)
            remove("pref_rotation_type_key")
            putInt("pref_default_reading_mode_key", newReadingMode)
            remove("pref_default_viewer_key")
        }

        return@withIOContext true
    }
}
