import { takeLatest, takeEvery } from 'redux-saga'
import { call, fork, put, all } from 'redux-saga/effects'
import request from '../../utils/request'
import api from '../../utils/api'
import {
  ActionTypes
} from './constants'
import {
  loadForecastingFail,
  loadForecastingLoaded,
  startClassifierDataLoaded,
  changeViewTreeModalSuccess
} from './actions'
//yzh
export function* getForecasting(action): IterableIterator<any>{
  const {projectId} = action.payload
try {
    const asyncData = yield call(request, `${api.forecasting}?projectId=${projectId}`)
   const forecasting = asyncData.payload
    yield put(loadForecastingLoaded(forecasting))
  } catch (err) {
    yield put(loadForecastingFail(err))
  }
}
export function* startClassifierData(action): IterableIterator<any>{
try {
    const asyncData = yield call(request, `${api.forecasting}/startClassifier`)
    const startCla = asyncData.payload
    yield put(startClassifierDataLoaded(asyncData))
  } catch (err) {
    yield put(loadForecastingFail(err))
  }
}
export function* changeViewTreeModal(value): IterableIterator<any>{
    yield put(changeViewTreeModalSuccess(value))
}
export default function* rootForecaseSaga (): IterableIterator<any> {
  yield [
    takeLatest(ActionTypes.LOAD_FORECASTING, getForecasting),//yzh
    takeEvery(ActionTypes.startClassifierData,startClassifierData),
    takeEvery(ActionTypes.CHANGE_VIEW_TREE_MODAL,changeViewTreeModal)
    
  ]
}