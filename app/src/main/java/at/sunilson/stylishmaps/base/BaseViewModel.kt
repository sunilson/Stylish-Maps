package at.sunilson.stylishmaps.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.sunilson.stylishmaps.utils.NonNullLiveData
import at.sunilson.stylishmaps.utils.SingleLiveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.util.*

abstract class BaseViewModel<Command, State>(initialState: State) : ViewModel() {

    private val _state: NonNullLiveData<State> = NonNullLiveData(initialState)

    /**
     * The state of this ViewModel. Observe to get changes or use [getState] to get a snapshot (don't use [LiveData.getValue])
     */
    val state: LiveData<State> get() = _state

    /**
     * You probably don't want to use this value is not updated in sync,
     * only use the state available to you in [setState] or [getState] or subscribe to [state]
     */
    val currentState: State
        get() = _state.value

    /**
     * Can be used to command the view to navigate or show toasts etc.
     */
    val commands: SingleLiveData<Command> = SingleLiveData()

    /**
     * Channel used to queue all actions so they are execute sequentially
     */
    private val stateChannel = Channel<StateAction<State>>(Channel.UNLIMITED)

    init {
        // Conume the channel and handle state updates/requests
        viewModelScope.launch {
            val getQueue = ArrayDeque<StateAction.GetState<State>>()
            stateChannel.consumeEach {
                when (it) {
                    // If we have a `Get` action we queue it
                    is StateAction.GetState -> getQueue.add(it)
                    // If we have a `Set` action we execute it
                    is StateAction.SetState -> it.block.let {
                        val newState = _state.value.it()
                        if (_state.value != newState) _state.value = newState
                    }
                }

                // We only execute `Get` actions when all `Set` actions are done so we don't
                // get race conditions between getting and setting data
                while (stateChannel.isEmpty) {
                    val block = getQueue.poll()?.block ?: break
                    block(_state.value)
                }
            }
        }
    }

    /**
     * @param block In this suspending block the current state can be accessed
     */
    fun getState(block: suspend (State) -> Unit) {
        stateChannel.offer(StateAction.GetState(block))
    }

    /**
     * @param block Use this suspending block to manipulate the current state by copying and returning it
     */
    protected fun setState(block: suspend State.() -> State) =
        stateChannel.offer(StateAction.SetState(block))

    private sealed class StateAction<State> {
        data class GetState<State>(val block: suspend (State) -> Unit) : StateAction<State>()
        data class SetState<State>(val block: suspend State.() -> State) : StateAction<State>()
    }
}