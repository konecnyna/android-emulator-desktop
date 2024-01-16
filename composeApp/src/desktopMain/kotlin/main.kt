import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.defkon.androidemulator.ui.App


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Android Emulator"
    ) {
        App()
    }
}

