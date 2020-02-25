package com.codingwithmitch.espressodaggerexamples.util

import com.codingwithmitch.espressodaggerexamples.util.Constants.NETWORK_ERROR

abstract class ApiResponseHandler <ViewState, Data>(
    response: ApiResult<Data>,
    stateEvent: StateEvent
){
    val result: DataState<ViewState> = when(response){

        is ApiResult.GenericError -> {
            DataState.error(
                errorMessage = stateEvent.errorInfo()
                        + "\n\nReason: " + response.errorMessage,
                stateEvent = stateEvent
            )
        }

        is ApiResult.NetworkError -> {
            DataState.error(
                errorMessage = stateEvent.errorInfo()
                    + "\n\nReason: " + NETWORK_ERROR,
                stateEvent = stateEvent
            )
        }

        is ApiResult.Success -> {
            handleSuccess(resultObj = response.value)
        }
    }

    abstract fun handleSuccess(resultObj: Data): DataState<ViewState>

}