import { fromJS } from 'immutable'
import undoable, { includeAction } from 'redux-undo'

import { ActionTypes } from './constants'

const initialState = fromJS({
  displays: [],
  displayLoading: false,
  clipboardLayers: [],
  lastOperationType: '',
  lastLayers: [],
  classifier:'', //yzh
  periodicityVal:'Monthly',
  inputNumberVal:1
})

function classifierReducer (state = initialState, action) {
  const { type, payload, defaultKey,isVisible,basicLearningValue,inputNumberVal,periodicityVal} = action

  switch (type) {
    case ActionTypes.LOAD_CLASSIFIER_SUCCESS:
      return state.set('classifier', payload.classifier)
    case ActionTypes.START_CLSSIFIER_SUCCESS:
      return state.set('classifier',payload)
    case ActionTypes.CHANGE_TAB_ACTIVE_KEY_SUCCESS:
      return state.set('defaultKey',payload.defaultKey)
    case ActionTypes.CHANGE_BASIC_LEARNING_MODAL_SUCCESS:
      return state.set('isVisible',isVisible.isVisible)
    case ActionTypes.CHANGE_BASIC_LEARNING_INPUTVALUE_SUCCESS:
      return state.set("basicLearningInputValue",basicLearningValue.basicLearningValue)
    case ActionTypes.CHANGE_INPUT_NUMBER_SUCCESS:
        return state.set("inputNumberVal",inputNumberVal.inputNumberVal)
    case ActionTypes.HANDLE_PERIODI_CITY_SUCCESS:
        return state.set("periodicityVal",periodicityVal.periodicityVal)
    default:
      return state
  }
}
export default undoable(classifierReducer, {
  filter: includeAction([
     ActionTypes.EDIT_CLASSIFIER_SUCCESS
  ]),
  undoType: ActionTypes.UNDO_OPERATION_SUCCESS,
  redoType: ActionTypes.REDO_OPERATION_SUCCESS
})
