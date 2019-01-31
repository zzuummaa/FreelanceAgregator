package ru.zuma.freelanceagregator.view

import javafx.scene.control.TableView
import ru.zuma.freelanceagregator.utils.Order
import tornadofx.*
import tornadofx.Stylesheet.Companion.contextMenu
import java.awt.Desktop
import java.lang.RuntimeException
import java.net.URI

class OrdersView : View("Заказы") {
    val orders = mutableListOf<Order>().observable()

    override val root = tableview(orders) {
        minWidth = 1000.0
        column("Заголовок", Order::title).remainingWidth()
        column("Просмотров", Order::views)
        column("Откликов", Order::responses)
        column("Опубликовано", Order::publishAt)
        columnResizePolicy = SmartResize.POLICY

        onUserSelect {
            openInBrowser(it.url)
        }

        contextmenu {
            item("Открыть в браузере").action {
                selectedItem?.apply { openInBrowser(url) }
            }
        }
    }

    private fun openInBrowser(url: String?) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url));
        } else {
            throw RuntimeException("Your platform not supported desktop or browser")
        }
    }
}