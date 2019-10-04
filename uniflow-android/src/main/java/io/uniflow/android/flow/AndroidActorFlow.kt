/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed launchOn an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.uniflow.android.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.ActorFlow
import io.uniflow.core.flow.StateAction
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onIO
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.isActive

/**
 * AndroidDataFlow
 * Unidirectional dataflow with states & events
 *
 * @author Arnaud Giuliani
 */
abstract class AndroidActorFlow : AndroidDataFlow(), ActorFlow {

    override val actorFlow = coroutineScope.actor<StateAction>(UniFlowDispatcher.dispatcher.default(), capacity = 10) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                UniFlowLogger.log("AndroidActorFlow run action $action")
                onIO {
                    proceedAction(action)
                }
            } else {
                UniFlowLogger.log("AndroidActorFlow action cancelled")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        actorFlow.close()
    }
}