import { fromJS } from 'immutable'
import undoable, { includeAction } from 'redux-undo'

import { ActionTypes } from './constants'

const initialState = fromJS({
  displays: [],
  displayLoading: false,
  clipboardLayers: [],
  lastOperationType: '',
  lastLayers: [],
  classifier:'' //yzh
})

function forecastingReducer (state = initialState, action) {
  const { type, payload } = action

  switch (type) {
    case ActionTypes.LOAD_FORECASTING_SUCCESS:
      return state.set('forecasting', payload.forecasting)
    case ActionTypes.START_CLSSIFIER_DATA_SUCCESS:
      return state.set('classifier',payload)
    default:
      return state
  }
}
export default undoable(forecastingReducer, {
  filter: includeAction([
     ActionTypes.EDIT_FORECASTING_SUCCESS
  ]),
  undoType: ActionTypes.UNDO_OPERATION_SUCCESS,
  redoType: ActionTypes.REDO_OPERATION_SUCCESS
})
