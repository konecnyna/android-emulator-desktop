import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.defkon.androidemulator.sdkmanager.SdkManager
import com.defkon.androidemulator.ui.App
import kotlinx.coroutines.flow.collect


val sdkManager = SdkManager()

fun main() = application {
    run(sdkManager)
    Window(onCloseRequest = ::exitApplication, title = "Android Emulator") {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}



fun run(sdkManager: SdkManager) {
    sdkManager.setup()
}
