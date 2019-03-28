import { takeLatest, takeEvery } from 'redux-saga'
import { call, fork, put, all } from 'redux-saga/effects'
import request from '../../utils/request'
import api from '../../utils/api'
import { ActionTypes} from './constants'
import {
  loadClassifierFail,
  loadClassifierLoaded,
  startClassifierLoaded,
  changeTabActiveKeySuccess,
  changeBasicLearningModalSuccess,
  changeBasicLearningInputValueSuccess
} from './actions'
//yzh
export function* getClassifier(action): IterableIterator<any>{
  const {projectId} = action.payload
try {
    const asyncData = yield call(request, `${api.classifier}?projectId=${projectId}`)
   const classifier = asyncData.payload
    yield put(loadClassifierLoaded(classifier))
  } catch (err) {
    yield put(loadClassifierFail(err))
  }
}
export function* startClassifier(action): IterableIterator<any>{
    
try {
    const asyncData = yield call(request, {
      method: 'post',
      url: `${api.classifier}/startClassifier`,
      data: action.payload//{"projectId":1}//action.payload.payload
    }) 

   // const asyncData = yield call(request, `${api.classifier}/startClassifier`)
    console.log(action.payload+"-----------action");
    const startCla = asyncData.payload
    yield put(startClassifierLoaded(asyncData))
    yield put(changeTabActiveKeySuccess("1"))
  } catch (err) {
    yield put(loadClassifierFail(err))
  }
}
export function* changeTabActiveKey(defaultKey): IterableIterator<any>{
    yield put(changeTabActiveKeySuccess(defaultKey))
}
export function* changeBasicLearningModal(defaultKey): IterableIterator<any>{
    yield put(changeBasicLearningModalSuccess(defaultKey))
}
export function* changeBasicLearningInputValue(value): IterableIterator<any>{
    yield put(changeBasicLearningInputValueSuccess(value))
}
export default function* rootClassifierSaga (): IterableIterator<any> {
  yield [
    takeLatest(ActionTypes.LOAD_CLASSIFIER, getClassifier),//yzh
    takeEvery(ActionTypes.START_CLASSIFIER,startClassifier),
    takeEvery(ActionTypes.CHANGE_TAB_ACTIVE_KEY,changeTabActiveKey),
    takeEvery(ActionTypes.CHANGE_BASIC_LEARNING_MODAL,changeBasicLearningModal),
    takeEvery(ActionTypes.CHANGE_BASIC_LEARNING_INPUTVALUE,changeBasicLearningInputValue)
  ]
}
