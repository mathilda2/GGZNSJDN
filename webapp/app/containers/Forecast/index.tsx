import * as React from 'react'
import Helmet from 'react-helmet'
import { Link, RouteComponentProps } from 'react-router'
import { compose } from 'redux'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'
import injectReducer from 'utils/injectReducer'
import injectSaga from 'utils/injectSaga'
import displayReducer from '../Display/reducer'
import displaySaga from '../Display/sagas'
import forecaseSaga from './sagas' ;//yzh
import forecastingReducer from './reducer';
import portalSaga from '../Portal/sagas'
import portalReducer from '../Portal/reducer'
import { loadDisplays, loadForecasting } from '../Display/actions'
const Row = require('antd/lib/row')
const Col = require('antd/lib/col')
const Breadcrumb = require('antd/lib/breadcrumb')
const utilStyles = require('../../assets/less/util.less')
import Container from '../../components/Container'
import {makeSelectClassifier} from './selectors'
import { Button } from 'antd';
import { Input } from 'antd';
import { Checkbox } from 'antd';
import { Select } from 'antd';
import { Card } from 'antd';
import { startClassifierData} from './actions';

interface IParams {
  pid: number
}

interface IVizProps extends RouteComponentProps<{}, IParams> {
  classifier:any //yzh
  forecasting:any//yzh
  onLoadForecasting:(projectId) =>void //yzh
  startClassifier:(projectId) => void
}

interface IVizStates {
  collapse: {dashboard: boolean, display: boolean}
}

export class Forecast extends React.Component<IVizProps, IVizStates> {

  constructor (props: IVizProps) {
    super(props)
    this.state = {
      collapse: {
        dashboard: true,
        display: true
      }
    }
  }

  public componentWillMount () {
    const { params,  onLoadForecasting} = this.props
    console.log(this.props.params.pid+"-----compddone");
    const projectId = params.pid
    onLoadForecasting(projectId);
  }

  public render () {
    const { params,  forecasting,classifier} = this.props
    console.log(this.props);
    const projectId = params.pid
    const Option = Select.Option;
    return (
      <Container>
        <Helmet title="分类" />
        <Container.Title>
          <Row>
            <Col span={24}>
              <Breadcrumb className={utilStyles.breadcrumb}>
                <Breadcrumb.Item>
                  <Link to="">分类</Link>
                </Breadcrumb.Item>
              </Breadcrumb>
            </Col>
          </Row>
        </Container.Title>
        <Container.Body>
          <Row>
            <Card title="Test options"   style={{ width: '100%',marginBottom:'3px'}}>
                  <table  style={{width: '100%'}}>
                       <tbody>
                        <tr>
                          <td style={{width:'10%'}}><Button type="primary">Choose</Button></td>
                          <td style={{width:'90%'}}><Input placeholder="Basic usage" /></td>
                        </tr>
                       </tbody>
                    </table>
              </Card>
          </Row>
          <Row>
            <Col span={8}>
             <Card title="Test options"   style={{marginRight:'3px' }}>
                  <table style={{width:'100%'}}>
                  <tbody>
                  <tr>
                    <td>
                         <Checkbox >Use training set</Checkbox>
                    </td>
                    <td></td>
                  </tr>
                  <tr>
                    <td>
                        <Checkbox >Supplied test set</Checkbox>
                    </td>
                    <td>
                      <Button type="primary" size="small" style={{padding:'0 60px'}}>set</Button>
                    </td>
                  </tr>
                  <tr>
                    <td>
                        <Checkbox >Cross-validation</Checkbox>
                    </td>
                    <td>
                       Folds &nbsp;&nbsp; <Input placeholder="" size="small" style={{ width:'100px',display:'inline-block'}}/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                        <Checkbox >Perxcentage split</Checkbox>
                    </td>
                    <td>
                       % &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Input placeholder="" size="small" style={{ width:'100px',display:'inline-block'}}/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                        <Button type="primary" size="small" style={{width:'100%'}}>More options</Button>
                    </td>
                  </tr>
                  <tr>
                    <td>
                       <Select
                        showSearch
                         style={{ width:'100%'}}>
                          <Option value="0">（Num）乘客数量</Option> 
                          <Option value="1">(Dat)时间</Option>
                        </Select>
                    </td>
                  </tr>
                  <tr>
                    <td>
                        <Button type="primary" size="small" style={{width:'100%'}} onClick={this.props.startClassifier}>Start</Button>
                    </td>
                    <td>
                       <Button type="primary" size="small" style={{width:'100%'}}>Stop</Button>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </Card>
            </Col>
            <Col span={16}>
                <Card title="Classifier output"   style={{ width: '100%' }}>
                      <Input style={{minHeight:'260px',minWidth:'100%' }}  type="textarea"  value={classifier.projectId}  />
                </Card>
            </Col>
          </Row>
        </Container.Body>
      </Container>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  classifier : makeSelectClassifier()
})

export function mapDispatchToProps (dispatch) {
  return {
    onLoadDisplays: (projectId) => dispatch(loadDisplays(projectId)),
     //yzh
    onLoadForecasting:(projectId)=>dispatch(loadForecasting(projectId)),
    startClassifier:(projectId)=>dispatch(startClassifierData(projectId))
  }
}

const withConnect = connect(mapStateToProps, mapDispatchToProps)
const withDisplayReducer = injectReducer({ key: 'display', reducer: displayReducer })
const withDisplaySaga = injectSaga({ key: 'display', saga: displaySaga })
const withForecaseSaga= injectSaga({ key: 'forecast', saga: forecaseSaga })//yzh
const withForecaseReducer = injectReducer({key:'forecast',reducer:forecastingReducer})
const withPortalReducer = injectReducer({ key: 'portal', reducer: portalReducer })
const withPortalSaga = injectSaga({ key: 'portal', saga: portalSaga })

export default compose(
  withForecaseSaga,
  withForecaseReducer,
  withDisplayReducer,
  withDisplaySaga,
  withPortalReducer,
  withPortalSaga,
  withConnect
)(Forecast)
