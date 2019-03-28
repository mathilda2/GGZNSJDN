 
import { ActionTypes } from './constants'

//yzh
export function loadForecasting(projectId){
  return {
    type:ActionTypes.LOAD_FORECASTING,
    payload:{
      projectId
    }
  }
}
export function loadForecastingLoaded (forecasting) {
  return {
    type: ActionTypes.LOAD_FORECASTING_SUCCESS,
    payload: {
      forecasting
    }
  }
}
export function loadForecastingFail(error){
  return {
    type:ActionTypes.LOAD_FORECASTING_FAILURE,
    payload:{
      error
    }
  }
}
export function startClassifierData(projectId){
  return {
    type:ActionTypes.startClassifierData,
    payload:{
      projectId
    }
  }
}
export function startClassifierDataLoaded(projectId){
  return {
    type:ActionTypes.START_CLSSIFIER_DATA_SUCCESS,
    payload:{
      projectId
    }
  }
}