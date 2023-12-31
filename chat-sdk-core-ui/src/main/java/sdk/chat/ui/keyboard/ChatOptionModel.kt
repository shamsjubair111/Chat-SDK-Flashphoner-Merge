package sdk.chat.ui.keyboard

import sdk.chat.ui.recycler.SmartViewModel

class ChatOptionModel(val title: String, val imageRes: Int, val width: Int?, val height: Int?, val onClick: Runnable): SmartViewModel() {

    fun click() {
        onClick.run()
    }

}