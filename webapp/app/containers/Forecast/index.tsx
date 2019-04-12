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
import {makeSelectClassifier,makeClassifierIsVisible,makeClassifierGojsStr} from './selectors'
import { Button,Modal } from 'antd';
import { Input } from 'antd';
import { Checkbox } from 'antd';
import { Select } from 'antd';
import { Card } from 'antd';
import { startClassifierData,viewTree,changeViewTreeModal,updateGojsVal} from './actions';
import * as  echartsT from 'echarts/lib/echarts';
import  'echarts/lib/chart/bar';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import { GojsDiagram } from "react-gojs/dist";
import { Diagram } from "gojs/release/go";
import * as go from "gojs/release/go";
interface IParams {
  pid: number
}

interface IVizProps extends RouteComponentProps<{}, IParams> {
  classifier:any //yzh
  forecasting:any//yzh
  onLoadForecasting:(projectId) =>void //yzh
  startClassifier:(projectId) => void
  onViewTree:(projectId) => void
  isVisibleTree:any
  onChangeViewTreeModal:(isVisibleTree)=>void
  onUpdateGojsVal:(gojsStr)=>void
  gojsStr:any
}

interface IVizStates {
  collapse: {dashboard: boolean, display: boolean}
  isVisibleTree:boolean
}

export class Forecast extends React.Component<IVizProps, IVizStates> {

  constructor (props: IVizProps) {
    super(props)
    this.state = {
      collapse: {
        dashboard: true,
        display: true
      },
      isVisibleTree:false
    }
  }

  public componentWillMount () {
      
    const { params,  onLoadForecasting} = this.props
    const projectId = params.pid
    onLoadForecasting(projectId);
  }

  public render () {
    const { params,  forecasting,classifier,isVisibleTree} = this.props
    const projectId = classifier.projectId;
    console.log(isVisibleTree);
    var resultClassifier = "";
    if(projectId != "" && projectId !=null){
        resultClassifier = projectId.resultClassifier;
    }
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
                        <Button type="primary" size="small" style={{width:'100%'}} onClick={this.props.startClassifier}>开始分类</Button>
                    </td>
                    <td>
                       <Button type="primary" size="small" style={{width:'100%'}} onClick={this.onViewTreeGojs.bind(this)}>可视化结果</Button>
                       <Modal
                           title="分类决策树"
                           visible={isVisibleTree}
                           onOk={this.handleOk}
                           onCancel={this.handleCancel}
                           width={'1300px'}
                           style={{marginTop:'-80px'}}
                         >
                      
                       <div id="myDiagramDivOfgoAgain" style={{ width:'1280px', height:'465px' }}>
                          
                       </div>
                       
                     </Modal>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </Card>
            </Col>
            <Col span={16}>
                <Card title="Classifier output"   style={{ width: '100%' }}>
                      <Input style={{minHeight:'260px',minWidth:'100%' }}  type="textarea"  value={resultClassifier}  />
                </Card>
            </Col>
          </Row>
        </Container.Body>
      </Container>
    )
  }
  onViewTreeGojs = ()=>{
      const {classifier,onChangeViewTreeModal,onUpdateGojsVal,gojsStr} = this.props;
      if(classifier.projectId==undefined){
          return;
      }
      var gojs = classifier.projectId.gojs;
      var gojsStrNew = JSON.stringify(gojs);
      var gojsStrOld = gojsStr.gojsStr;
      console.log(gojsStrNew==gojsStrOld);
      onChangeViewTreeModal(true);
      if(gojsStrOld==gojsStrNew){
          console.log("same ok");
          return ;
      }else{
          onUpdateGojsVal(gojsStrNew);
      }
      
      setTimeout(() => {
          var $ = go.GraphObject.make;
          var myDiagram =
            $(go.Diagram, "myDiagramDivOfgoAgain",
              {
                initialContentAlignment: go.Spot.Center, // 居中显示内容
                 "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom,
                "undoManager.isEnabled": true, // 打开 Ctrl-Z 和 Ctrl-Y 撤销重做功能
                layout: $(go.TreeLayout, // 1个特殊的树形排列 Diagram.layout布局
                          { angle: 90, layerSpacing: 35 }),
                allowMove:false
              });
          
          // 我们早先定义的模板
          myDiagram.nodeTemplate =
            $(go.Node, "Auto",
            // define the node's outer shape, which will surround the TextBlock
            $(go.Shape, "RoundedRectangle",
              {
                parameter1: 20,  // the corner has a large radius
                fill: $(go.Brush, "Linear", { 0: "rgb(254, 201, 0)", 1: "rgb(254, 162, 0)" }),
                stroke: null,
                portId: "",  // this Shape is the Node's port, not the whole Node
              }),
            $(go.TextBlock,
              {
                font: "bold 11pt helvetica, bold arial, sans-serif"
              },
              new go.Binding("text").makeTwoWay()),
          );
          
          
          myDiagram.linkTemplate =
          $(go.Link,  // the whole link panel
            {
              toShortLength: 3
            },
            new go.Binding("points").makeTwoWay(),
            new go.Binding("curviness"),
            $(go.Shape,  // the link shape
              { strokeWidth: 1.5 }),
            $(go.Shape,  // the arrowhead
              { toArrow: "standard", stroke: null }),
            $(go.Panel, "Auto",
              $(go.Shape,  // the label background, which becomes transparent around the edges
                {
                  fill: $(go.Brush, "Radial",
                    { 0: "rgb(255, 255, 255)", 0.9: "rgb(255, 255, 255)", 1: "rgba(255, 255, 255, 0)" }),
                  stroke: null
                }),
              $(go.TextBlock, "transition",  // the label text
                {
                  textAlign: "center",
                  font: "9pt helvetica, arial, sans-serif",
                  margin: 4,
                },
                // editing the text automatically updates the model data
                new go.Binding("text","linkText").makeTwoWay())
            )
          );
          
          
          var model = $(go.TreeModel);
          model.nodeDataArray =gojs.nodeDataArray;
         // model.fromJson(gojs);
          console.log(gojs.nodeDataArray);
         // go.Model.fromJson(gojs.nodeDataArray);
         // myDiagram.model = go.Model.fromJson(gojs.nodeDataArray);
          //myDiagram.layoutDiagram(true);
          myDiagram.model = model;
              
      },200)
  }
  onViewTree = () => { 
     
      const {classifier,onChangeViewTreeModal} = this.props;
      if(classifier.projectId==undefined){
          return;
      }
      onChangeViewTreeModal(true);
      var data = classifier.projectId.data;
      var links = classifier.projectId.links.link;
      setTimeout(() => {
          var myChart = echartsT.init(document.getElementById('main') as HTMLDivElement);
          var option = {
                  title: {
                      text: '分类决策树'
                  },
                  tooltip: {},
                  animationDurationUpdate: 1500,
                  animationEasingUpdate: 'quinticInOut',
                  series : [
                      {
                          type: 'graph',
                          layout: 'none',
                          symbol:'circle',
                          symbolSize: [120,31],
                          roam: true,
                          label: {
                              normal: {
                                  show: true
                              }
                          },
                          edgeSymbol: ['circle', 'arrow'],
                          edgeSymbolSize: [5, 15],
                          edgeLabel: {
                              normal: {
                                  textStyle: {
                                      fontSize: 14
                                  }
                              }
                          },
                          data: data,
                          links: links,
                          lineStyle: {
                              normal: {
                                  opacity: 0.9,
                                  width: 2,
                                  curveness: 0
                              }
                          }
                      }
                  ]
              };
           myChart.setOption(option);
        },5);
      
     /*      <div id="main" ref="lineEchart" style={{ width:'100%', height: '465px' }}></div>*/
  }
  handleOk = (e) => {
      const {onChangeViewTreeModal} = this.props;
      onChangeViewTreeModal(false);
   }
  handleCancel = (e) => {
      const {onChangeViewTreeModal} = this.props;
      onChangeViewTreeModal(false);
  }
}

const mapStateToProps = createStructuredSelector({
  classifier : makeSelectClassifier(),
  isVisibleTree:makeClassifierIsVisible(),
  gojsStr:makeClassifierGojsStr()
})

export function mapDispatchToProps (dispatch) {
  return {
    onLoadDisplays: (projectId) => dispatch(loadDisplays(projectId)),
     //yzh
    onLoadForecasting:(projectId)=>dispatch(loadForecasting(projectId)),
    startClassifier:(projectId)=>dispatch(startClassifierData(projectId)),
    onChangeViewTreeModal:(isVisibleTree)=>dispatch(changeViewTreeModal(isVisibleTree)),
    onUpdateGojsVal:(gojsStr)=>dispatch(updateGojsVal(gojsStr)),
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
