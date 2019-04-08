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
  const { type, payload,isVisible} = action

  switch (type) {
    case ActionTypes.LOAD_FORECASTING_SUCCESS:
      return state.set('forecasting', payload.forecasting)
    case ActionTypes.START_CLSSIFIER_DATA_SUCCESS:
      return state.set('classifier',payload)
    case ActionTypes.CHANGE_VIEW_TREE_MODAL_SUCCESS:
        console.log(isVisible.isVisible,isVisible+"-----------------------");
        return state.set('isVisibleTree',isVisible.isVisible)
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
