
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
import portalSaga from '../Portal/sagas'
import portalReducer from '../Portal/reducer'

import { loadDisplays, addDisplay, editDisplay, deleteDisplay,loadForecasting } from '../Display/actions'
import { loadPortals, addPortal, editPortal, deletePortal } from '../Portal/actions'
import { makeSelectDisplays } from '../Display/selectors'
import { makeSelectPortals } from '../Portal/selectors'
import { checkNameUniqueAction } from '../App/actions'

const Icon = require('antd/lib/icon')
const Collapse = require('antd/lib/collapse')
import * as classnames from 'classnames'
const Row = require('antd/lib/row')
const Col = require('antd/lib/col')
const Breadcrumb = require('antd/lib/breadcrumb')
const Panel = Collapse.Panel
const styles = require('./Viz.less')
const utilStyles = require('../../assets/less/util.less')
import Container from '../../components/Container'
import DisplayList, { IDisplay } from '../Display/components/DisplayList'
import { Portal } from '../Portal'
import {makeSelectCurrentProject} from '../Projects/selectors'
import ModulePermission from '../Account/components/checkModulePermission'
import {IProject} from '../Projects'

import { Button } from 'antd';
import { Input } from 'antd';
import { Checkbox } from 'antd';
import { Select } from 'antd';
import { Card } from 'antd';

interface IParams {
  pid: number
}

interface IVizProps extends RouteComponentProps<{}, IParams> {
  displays: any[]
  portals: any[]
  currentProject: IProject
  onLoadDisplays: (projectId) => void
  onLoadForecasting:(projectId) =>void //yzh
  onAddDisplay: (display: IDisplay, resolve: () => void) => void
  onEditDisplay: (display: IDisplay, resolve: () => void) => void
  onDeleteDisplay: (displayId: number) => void
  onLoadPortals: (projectId) => void
  onAddPortal: (portal, resolve) => void
  onEditPortal: (portal, resolve) => void
  onDeletePortal: (portalId: number) => void
  onCheckUniqueName: (pathname: string, data: any, resolve: () => any, reject: (error: string) => any) => any
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
    const { params, onLoadDisplays, onLoadPortals ,onLoadForecasting} = this.props
    console.log(this.props);
    const projectId = params.pid
    //onLoadDisplays(projectId)
    //onLoadPortals(projectId)
    onLoadForecasting(projectId)
  }

  public render () {
    const {
      displays, params, onAddDisplay, onEditDisplay, onDeleteDisplay,
      portals, onAddPortal, onEditPortal, onDeletePortal, currentProject, onCheckUniqueName
    } = this.props
    const projectId = params.pid
    const isHideDashboardStyle = classnames({
      [styles.listPadding]: true,
      [utilStyles.hide]: !this.state.collapse.dashboard
    })
    const isHideDisplayStyle = classnames({
      [styles.listPadding]: true,
      [utilStyles.hide]: !this.state.collapse.display
    })
    const Option = Select.Option;
    return (
      <Container>
        <Helmet title="预测" />
        <Container.Title>
          <Row>
            <Col span={24}>
              <Breadcrumb className={utilStyles.breadcrumb}>
                <Breadcrumb.Item>
                  <Link to="">预测</Link>
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
                        <Button type="primary" size="small" style={{width:'100%'}} onClick={this.startClassifier}>Start</Button>
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
                  <textarea name="" style={{minHeight:'260px',minWidth:'100%',border:'1px solid #e9e9e9'}}></textarea>
                </Card>
            </Col>
          </Row>
        </Container.Body>
      </Container>
    )
  }
  public startClassifier(){
      console.log(123);
    }
}

const mapStateToProps = createStructuredSelector({
  displays: makeSelectDisplays(),
  portals: makeSelectPortals(),
  currentProject: makeSelectCurrentProject()
})

export function mapDispatchToProps (dispatch) {
  return {
    onLoadDisplays: (projectId) => dispatch(loadDisplays(projectId)),
    onAddDisplay: (display: IDisplay, resolve) => dispatch(addDisplay(display, resolve)),
    onEditDisplay: (display: IDisplay, resolve) => dispatch(editDisplay(display, resolve)),
    onDeleteDisplay: (id) => dispatch(deleteDisplay(id)),
    onLoadPortals: (projectId) => dispatch(loadPortals(projectId)),
    onAddPortal: (portal, resolve) => dispatch(addPortal(portal, resolve)),
    onEditPortal: (portal, resolve) => dispatch(editPortal(portal, resolve)),
    onDeletePortal: (id) => dispatch(deletePortal(id)),
    onCheckUniqueName: (pathname, data, resolve, reject) => dispatch(checkNameUniqueAction(pathname, data, resolve, reject)),
     //yzh
    onLoadForecasting:(projectId)=>dispatch(loadForecasting(projectId))
  }
}

const withConnect = connect(mapStateToProps, mapDispatchToProps)
const withDisplayReducer = injectReducer({ key: 'display', reducer: displayReducer })
const withDisplaySaga = injectSaga({ key: 'display', saga: displaySaga })
const withPortalReducer = injectReducer({ key: 'portal', reducer: portalReducer })
const withPortalSaga = injectSaga({ key: 'portal', saga: portalSaga })

export default compose(
  withDisplayReducer,
  withDisplaySaga,
  withPortalReducer,
  withPortalSaga,
  withConnect
)(Forecast)
