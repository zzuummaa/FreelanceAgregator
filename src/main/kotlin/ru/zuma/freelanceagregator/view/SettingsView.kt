package ru.zuma.freelanceagregator.view

import javafx.application.Platform
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.stage.Modality
import ru.zuma.freelanceagregator.utils.HtmlParser
import tornadofx.*
import kotlin.concurrent.thread

class SettingsView : View("Настройки поиска") {
    val ordersView = OrdersView()
    lateinit var progressBar: ProgressBar

    override val root = form {
        hbox(20) {
            fieldset("Биржи фриланса") {
                field { checkbox("freelansim.ru") { isSelected = true } }
            }
            fieldset("Специальность") {
                field { checkbox("Веб разработчик") { isSelected = true } }
                field { checkbox("Инженер электроники") { isSelected = true } }
                field { checkbox("Разработчик мобильных приложений") { isSelected = true } }
            }
        }
        hbox {
            alignment = Pos.CENTER
            button("Найти заказы") { setOnMouseClicked{ onSearchOrdersClicked() } }
            progressbar(0.0){
                hide()
                progressBar = this@progressbar
            }
        }
    }

    private fun Button.onSearchOrdersClicked() {
        thread {
            Platform.runLater { hide(); progressBar.show() }
            val htmlParser = HtmlParser()
            var i = 0;
            while (!htmlParser.isFreelansimOrdersEnd()) {
                htmlParser.getFreelansimOrders(ordersView.orders)
                Platform.runLater { progressBar.progress = (++i).toDouble() * 2.7 / 100.0 }
            }
            Platform.runLater { show(); progressBar.hide() }
            Platform.runLater { ordersView.openWindow(modality = Modality.WINDOW_MODAL) }
        }
    }
}