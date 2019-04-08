import { createSelector } from 'reselect'

const selectForecasting = (state) => state.get('forecast')

const makeSelectClassifier = () => createSelector(
  selectForecasting,
  ({ present }) => present.get('classifier')
)
const makeClassifierIsVisible = () => createSelector(
  selectForecasting,
  ({ present }) => present.get('isVisibleTree')
)
export {
  selectForecasting,
  makeSelectClassifier,
  makeClassifierIsVisible
}