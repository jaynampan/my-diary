package meow.softer.mydiary

import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.junit.internal.matchers.TypeSafeMatcher
/**
 * Matches a [Root] if it's a Toast.
 * This is useful for Espresso tests to verify that a Toast message is displayed.
 * It checks the window layout parameters type for `TYPE_TOAST` or `TYPE_APPLICATION_OVERLAY` (for newer Android versions)
 * and ensures the decor view is not focusable for overlays.
 */
class SimpleToastMatcher : TypeSafeMatcher<Root>() {
    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get().type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            return true
        }
        // For overlays that might be toasts (e.g., on API 30+)
        // they are typically not focusable.
        if (type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
            return !root.decorView.isFocusable && !root.decorView.isFocusableInTouchMode
        }
        return false
    }

    override fun describeTo(description: Description?) {
        description?.appendText("is toast")
    }
}