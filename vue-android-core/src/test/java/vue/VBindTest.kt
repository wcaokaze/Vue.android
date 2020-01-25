package vue

import androidx.test.core.app.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*
import kotlin.test.*

import android.content.*
import android.view.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class VBindTest {
   private val testContext
      get() = ApplicationProvider.getApplicationContext<Context>()

   @Test fun bind() {
      val view = View(testContext)
      val state = StateField(false)

      view.visibility = View.VISIBLE
      view.vBind.isVisible(state)
      assertEquals(View.INVISIBLE, view.visibility)
   }
}
