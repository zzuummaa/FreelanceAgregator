package ru.zuma.freelanceagregator.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URL

val FREELANSIM_URL = "https://freelansim.ru"

class HtmlParser {
    private var nextURL: String? = FREELANSIM_URL

    fun isFreelansimOrdersEnd() = (nextURL == null)

    fun getFreelansimOrders(orders: MutableList<Order> = ArrayList()): List<Order> {
        val url = nextURL ?: return orders
        nextURL = null

        try {
            val response = Jsoup.connect(url).get()

            nextURL = response.getElementsByClass("next_page").attr("href")
            if (nextURL == "") nextURL = null
            else if (nextURL != null) nextURL = FREELANSIM_URL + nextURL

            val tasksList = response.getElementById("tasks_list").children()
            tasksList.forEach {
                orders.add(Order.fromFreelansimTaskListItem(it))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return orders
    }
}

class Order(
    var title: String? = null,
    var url: String? = null,
    var responses: Int = 0,
    var views: Int = 0,
    var publishAt: String? = null
) {

    companion object {
        fun fromFreelansimTaskListItem(taskListItem: Element): Order {
            val order = Order()
            with(order) {
                val taskElements = taskListItem.getElementsByClass("task__title")
                if (taskElements.size > 0) {
                    title = taskElements[0].attr("title")
                    val inner = taskElements[0].children()
                    if (inner.size > 0) {
                        url = inner[0].attr("href")
                        if (url != null) url = FREELANSIM_URL + url
                    }
                }

                val paramResponses = taskListItem.getElementsByClass("params__responses")
                if (paramResponses.size > 0) {
                    val paramsCount = paramResponses[0].getElementsByClass("params__count")
                    if (paramsCount.size > 0) responses = paramsCount[0].text().toInt()
                }


                val paramsViews = taskListItem.getElementsByClass("params__views")
                if (paramsViews.size > 0) {
                    val paramsCount = paramsViews[0].getElementsByClass("params__count")
                    if (paramsCount.size > 0) {
                        views = paramsCount[0].text().toInt()
                    }
                }

                val paramPublish = taskListItem.getElementsByClass("params__published-at")
                if (paramPublish.size > 0) publishAt = paramPublish[0].text()

            }

            return order
        }
    }

    override fun toString(): String {
        return "Order(title=$title, responses=$responses, views=$views, publishAt=$publishAt)"
    }
}