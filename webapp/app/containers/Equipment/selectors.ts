import { createSelector } from 'reselect'

const selectClassifier = (state) => state.get('classifier')

const makeNewClassifier = () => createSelector(
  selectClassifier,
  ({ present }) => present.get('classifier') //classifier
)
const makeClassifierTabsActiveKey = () => createSelector(
  selectClassifier,
  ({ present }) => present.get('defaultKey') //classifier
)
const makeClassifierIsVisible = () => createSelector(
  selectClassifier,
  ({ present }) => present.get('isVisible') 
)
const makeClassifierBasicLearningValue= () => createSelector(
  selectClassifier,
  ({ present }) => present.get('basicLearningInputValue') 
)
const makeClassifierInputNumberVal= () => createSelector(
  selectClassifier,
  ({ present }) => present.get('inputNumberVal') 
)
const makePeriodicityVal= () => createSelector(
  selectClassifier,
  ({ present }) => present.get('periodicityVal') 
)

export {
  selectClassifier,
  makeNewClassifier,
  makeClassifierTabsActiveKey,
  makeClassifierIsVisible,
  makeClassifierBasicLearningValue,
  makeClassifierInputNumberVal,
  makePeriodicityVal
}