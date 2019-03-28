import { createSelector } from 'reselect'

const selectForecasting = (state) => state.get('forecast')

const makeSelectClassifier = () => createSelector(
  selectForecasting,
  ({ present }) => present.get('classifier')
)

// const makeSelectNextState = () => createSelector(
//   selectForecasting,
//   ({ future }) => {
//     if (future.length === 0) { return {} }
//     const item = future[0]
//     return {
//       displayId: item.get('currentDisplay').id,
//       slide: item.get('currentSlide'),
//       layers: item.get('currentLayers'),
//       lastOperationType: item.get('lastOperationType'),
//       lastLayers: item.get('lastLayers')
//     }
//   }
// )
export {
  selectForecasting,
  makeSelectClassifier,
  //makeSelectNextState,
}