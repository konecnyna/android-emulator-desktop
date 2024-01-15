import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.defkon.androidemulator.sdkmanager.SdkManager
import com.defkon.androidemulator.ui.App
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect



fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Android Emulator"
    ) {
        App()
    }
}

