package vue.vuex.preference

import android.content.*

fun intPreferenceState(
      context: Context,
      file: PreferenceState.PreferenceFile,
      key: String,
      default: Int
) = PreferenceStateDelegate(IntPreferenceLoader, context, file, key, default)

fun intPreferenceState(
      context: Context,
      file: PreferenceState.PreferenceFile,
      default: Int
) = PreferenceStateDelegate(IntPreferenceLoader, context, file, null, default)

object IntPreferenceLoader : PreferenceState.Loader<Int> {
   override fun get(sharedPreferences: SharedPreferences,
                    key: String,
                    default: Int): Int
   {
      return sharedPreferences.getInt(key, default)
   }

   override fun put(sharedPreferences: SharedPreferences,
                    key: String,
                    value: Int)
   {
      sharedPreferences.edit()
            .putInt(key, value)
            .apply()
   }
}
