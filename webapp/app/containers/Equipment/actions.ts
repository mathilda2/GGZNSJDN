 
import { ActionTypes } from './constants'

//yzh
export function loadClassifier(projectId){
  return {
    type:ActionTypes.LOAD_CLASSIFIER,
    payload:{
      projectId
    }
  }
}
export function loadClassifierLoaded (classifier) {
  return {
    type: ActionTypes.LOAD_CLASSIFIER_SUCCESS,
    payload: {
      classifier
    }
  }
}
export function loadClassifierFail(error){
  return {
    type:ActionTypes.LOAD_CLASSIFIER_FAILURE,
    payload:{
      error
    }
  }
}
export function startClassifier(projectId,basicLearningInputValue,inputNumberVal,periodicityVal){
  return {
    type:ActionTypes.START_CLASSIFIER,
    payload:{
      projectId,
      basicLearningInputValue,
      inputNumberVal,
      periodicityVal
    }
  }
}
export function startClassifierLoaded(projectId){
  return {
    type:ActionTypes.START_CLSSIFIER_SUCCESS,
    payload:{
      projectId
    }
  }
}
export function changeTabActiveKeyLoad(defaultKey){
  return {
    type:ActionTypes.CHANGE_TAB_ACTIVE_KEY,
    payload:{
      defaultKey
    }
  }
}
export function changeTabActiveKeySuccess(defaultKey){
  return {
    type:ActionTypes.CHANGE_TAB_ACTIVE_KEY_SUCCESS,
    payload:{
      defaultKey
    }
  }
}
export function changeBasicLearningModal(isVisible){
  return {
    type:ActionTypes.CHANGE_BASIC_LEARNING_MODAL,
    isVisible:isVisible
  }
}
export function changeBasicLearningModalSuccess(isVisible){
  return {
    type:ActionTypes.CHANGE_BASIC_LEARNING_MODAL_SUCCESS,
    isVisible:isVisible
  }
}
export function changeBasicLearningInputValue(value){
  return {
    type:ActionTypes.CHANGE_BASIC_LEARNING_INPUTVALUE,
    basicLearningValue:value
  }
}
export function changeBasicLearningInputValueSuccess(value){
  return {
    type:ActionTypes.CHANGE_BASIC_LEARNING_INPUTVALUE_SUCCESS,
    basicLearningValue:value
  }
}
export function changeInputNumber(value){
    return {
      type:ActionTypes.CHANGE_INPUT_NUMBER,
      inputNumberVal:value
    }
}
export function changeInputNumberSuccess(value){
    return {
      type:ActionTypes.CHANGE_INPUT_NUMBER_SUCCESS,
      inputNumberVal:value
    }
}
export function handlePeriodicity(value){
    return {
      type:ActionTypes.HANDLE_PERIODI_CITY,
      periodicityVal:value
    }
}
export function handlePeriodicitySuccess(value){
    return {
      type:ActionTypes.HANDLE_PERIODI_CITY_SUCCESS,
      periodicityVal:value
    }
}
