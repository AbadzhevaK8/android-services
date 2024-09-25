package com.abadzheva.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log

class MyIntentService2 : IntentService(NAME) {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent?) {
        log("onHandleIntent")
        val page = intent?.getIntExtra(PAGE, 0) ?: 0
        for (i in 0 until 5) {
            Thread.sleep(1000)
            log("Timer $i $page")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyIntentService: $message")
    }

    companion object {
        const val NAME = "MyIntentService"
        const val PAGE = "page"

        fun newIntent(
            context: Context,
            page: Int,
        ): Intent =
            Intent(context, MyIntentService2::class.java).apply {
                putExtra(PAGE, page)
            }
    }
}
