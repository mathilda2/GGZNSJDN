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
import classifierSaga from './sagas' ;//yzh
import classifierReducer from './reducer';
import portalSaga from '../Portal/sagas'
import portalReducer from '../Portal/reducer'
import { loadDisplays } from '../Display/actions'
const Row = require('antd/lib/row')
const Col = require('antd/lib/col')
const Breadcrumb = require('antd/lib/breadcrumb')
const utilStyles = require('../../assets/less/util.less')
import Container from '../../components/Container'
import {makeNewClassifier,makeClassifierTabsActiveKey,makeClassifierIsVisible,makeClassifierBasicLearningValue} from './selectors'
import { Button ,Modal} from 'antd';
import { Input } from 'antd';
import { Checkbox } from 'antd';
import { Select } from 'antd';
import { Card } from 'antd';
import { startClassifier,loadClassifier,changeTabActiveKeyLoad,changeBasicLearningModal,changeBasicLearningInputValue} from './actions';
import { Tabs } from 'antd';
import { Table } from 'antd';
import { InputNumber } from 'antd';
import $ from 'jquery';
import zTree from 'zTree';
import { Tree } from 'antd';
// 引入 ECharts 主模块
import * as  echartsT from 'echarts/lib/echarts';
// 引入柱状图
import  'echarts/lib/chart/bar';
// 引入提示框和标题组件
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'ztree/css/zTreeStyle/zTreeStyle.css';

interface IParams {
  pid: number
}

interface IVizProps extends RouteComponentProps<{}, IParams> {
  classifier:any //yzh
  forecasting:any//yzh
  onLoadClassifier:(projectId) =>void //yzh
  onstartClassifier:(projectId:any,basicVal:any) => void
  tabChangeCallback:(objectValue)=>void
  onChangeTabActiveKeyLoad:(key)=>void
  defaultKey:any
  isVisible:any
  onChangeBasicLearningModal:(isVisible)=>void
  basicLearningInputValue:any
  onChangeBasicLearningInputValue:(value)=>void
}

interface IVizStates {
  collapse: {dashboard: boolean, display: boolean}
  defaultKey:string
  isVisible:boolean
  basicLearningInputValue:string
}

export class Classifier extends React.Component<IVizProps, IVizStates> {

  constructor (props: IVizProps) {
    super(props)
    this.state = {
      collapse: {
        dashboard: true,
        display: true
      } ,
      defaultKey:"1",
      isVisible:false,
      basicLearningInputValue:""
    }
  }
   
  public componentWillMount () {
    const { params,  onLoadClassifier} = this.props
    const projectId = params.pid
    onLoadClassifier(projectId);
  }
  public render () {
    const { params,classifier,defaultKey,isVisible,basicLearningInputValue} = this.props
    const projectId = params.pid
    const Option = Select.Option;
    const TabPane = Tabs.TabPane;
    var defaultKeyTab = "";
    if(defaultKey==undefined || defaultKey=="1"){
      defaultKeyTab = "1";
    }else{
      defaultKeyTab = defaultKey.payload.defaultKey;
    }
    const basicLearningInputVal =basicLearningInputValue;
    var content = "";
    if(classifier.projectId!=undefined){
      content = "xList:"+classifier.projectId.xList +"\n ylist:"+ classifier.projectId.ylist;
    }
    const columns = [{
      title: '名称',
      dataIndex: 'name'
    }];
    const data = [{key: '1',name: '乘客数量'}];
    const { TreeNode } = Tree;
    const rowSelection = {
      onChange: (selectedRowKeys, selectedRows) => {
        console.log(`selectedRowKeys1: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      }
    }
    
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
             <Tabs   type="card">
            <TabPane tab="基础配置" key="1">
                <Row>
                  <Col span={18}>
                      <Card title="目标选择"  style={{ width: '100%' }}>
                        <Row  gutter={16}>
                          <Col span={6}><Button type="primary" style={{ width: '100%' }}>全选</Button></Col>
                          <Col span={6}><Button type="primary" style={{ width: '100%' }}>全不选</Button></Col>
                          <Col span={6}><Button type="primary" style={{ width: '100%' }}>反选</Button></Col>
                          <Col span={6}><Button type="primary" style={{ width: '100%' }}>正则匹配</Button></Col>
                        </Row>
                        <Row>
                            <Table rowSelection={rowSelection} columns={columns} dataSource={data} />
                        </Row>        
                      </Card>
                  </Col>
                  <Col span={6}>
                        <Card title="基本参数"  style={{ width: '100%' }}>
                          <table>
                             <tbody>
                              <tr>
                                <td>要预测的时间单位数</td>
                                <td>
                                  <InputNumber size="small" min={1} max={100} defaultValue={1}  />
                                </td>
                              </tr>
                              <tr>
                                <td>时间戳</td>
                                <td>
                                  <Select defaultValue="lucy" style={{ width: '100%' }} >
                                    <Option value="jack">时间</Option>
                                    <Option value="lucy">乘客数量</Option>
                                  </Select>
                                </td>
                              </tr>
                              <tr>
                                <td>周期性</td>
                                <td>
                                  <Select defaultValue="lucy" style={{ width: '100%' }} >
                                    <Option value="jack">Hourly</Option>
                                    <Option value="lucy">Daily</Option>
                                    <Option value="lucy">Weekly</Option>
                                    <Option value="lucy">Monthly</Option>
                                    <Option value="lucy">Quarterly</Option>
                                    <Option value="lucy">Yearly</Option>
                                  </Select>
                                </td>
                              </tr>
                              <tr>
                                <td>跳过清单</td>
                                <td>
                                  <Input   placeholder="" />
                                </td> 
                              </tr>
                              <tr>
                                <td>置信区间</td>
                                <td>
                                  <Checkbox ></Checkbox>
                                </td> 
                              </tr>
                              <tr>
                                <td></td>
                                <td>
                                                                    级别%<InputNumber size="small" min={1} max={100} defaultValue={1}  />
                                </td> 
                              </tr>
                              <tr>
                                <td> 进行评估</td>
                                <td>
                                  <Checkbox ></Checkbox>
                                </td> 
                              </tr>
                            </tbody>
                          </table>
                        </Card>
                  </Col>
                </Row>
                
            </TabPane>
            <TabPane tab="高级配置" key="2">
            <Tabs type="card">
              <TabPane tab="基础学习者" key="21">
                  <Card title="基础学习者配置"  style={{ width: '100%' }}>
                     <table  style={{width: '100%'}}>
                       <tbody>
                        <tr>
                          <td style={{width:'10%'}}><Button type="primary" onClick={this.showModal}>Choose</Button></td>
                          <td style={{width:'90%'}}><Input placeholder="Basic usage" value={basicLearningInputVal} /></td>
                        </tr>
                       </tbody>
                    </table>
                    <ul id='tree' className="ztree"></ul>
                  </Card>
                  <Modal
                    title="算法分类"
                    visible={isVisible}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                  >
                    <Tree
                        showLine
                        defaultExpandedKeys={['0-0-0']}
                        onSelect={this.onSelect}
                      >
                        <TreeNode title="weka" key="0-0">
                          <TreeNode title="functions" key="0-0-0" selectable="false">
                            <TreeNode title="GaussianProcesses" key="0-0-0-0" />
                            <TreeNode title="LinearRegression" key="0-0-0-1" />
                            <TreeNode title="MultilayerPerceptron" key="0-0-0-2" />
                          </TreeNode>
                          <TreeNode title="lazy" key="0-0-1" selectable="false">
                            <TreeNode title="ibk" key="0-0-1-0" />
                            <TreeNode title="kstr" key="0-0-1-1" />
                            <TreeNode title="lwl" key="0-0-1-2" />
                          </TreeNode>
                          <TreeNode title="rules" key="0-0-2" selectable="false">
                            <TreeNode title="DecisionTable" key="0-0-2-0" />
                            <TreeNode title="M5Rules" key="0-0-2-1" />
                            <TreeNode title="ZeroR" key="0-0-2-2" />
                          </TreeNode>
                        </TreeNode>
                      </Tree>
                  </Modal>
              </TabPane>
              <TabPane tab="Lag creation" key="22">
                <Row>
                  <Col>
                    <Card title="滞后长度"  style={{ width: '100%' }}>
                            <table  style={{width: '100%'}}>
                            <tbody>
                              <tr>
                                <td style={{width:'10%'}}><Checkbox ></Checkbox>Use custom lag lengths</td>
                              </tr>
                            </tbody>
                          </table>
                      </Card>
                  </Col>
                  <Col>
                    
                  </Col>
                  </Row>
              </TabPane>
              <TabPane tab="Periodic attribute" key="23">
                  123123
              </TabPane>
              <TabPane tab="Overlay data" key="24">
                  12312ss
              </TabPane>
              <TabPane tab="Evalution" key="25">
                  312312
              </TabPane>
              <TabPane tab="Output" key="26">
                  3123
              </TabPane>
            </Tabs>
            </TabPane>
          </Tabs>
          </Row>
          <Row>
            <Row>
                  {/* <Col span={6}>
                         <Card title="结果集"  style={{ width: '100%' }}>
                              <Row gutter={16}>
                                <Col span={8}>
                                  <Button type="primary" style={{ width: '100%' }} onClick={this.startClassifierOk}>开始</Button>
                                </Col>
                                <Col span={8}>
                                  <Button type="primary" style={{ width: '100%' }}>停止</Button>
                                </Col>
                                <Col span={8}>
                                  <Button type="primary" style={{ width: '100%' }}>帮助</Button>
                                </Col>
                              </Row>
                              <Row>
                                <Input style={{minHeight:'260px',minWidth:'100%',marginTop:'3px' }}  type="textarea" />
                              </Row>
                        </Card>
                  </Col> */}
                  <Col span={24}>
                         <Card title="输出/可视化"  style={{ width: '100%'}}>
                          <Button type="primary" style={{ width: '100%',marginBottom:'3px'}} onClick={this.startClassifierOk}>开始</Button>
                         <Tabs activeKey={defaultKeyTab} type="card" onChange={this.tabChangeCallback.bind(this)}>
                          <TabPane tab="输出" key="1"  >
                            
                              <Input style={{minHeight:'260px',minWidth:'100%' }}  type="textarea" value={content} />
                          </TabPane>
                          <TabPane tab="训练未来预测" key="2" >
                          <div id="main" ref="lineEchart" style={{ width:'100%', height: '300px' }}></div>
                          </TabPane>
                        </Tabs>
                        </Card>
                  </Col>
                </Row>
          </Row>
        </Container.Body>
      </Container>
    )
  }
  startClassifierOk=(e)=>{
    console.log(this.props+"--------------------this.props");
    const { onstartClassifier,params,basicLearningInputValue} = this.props;
    const projectId = params.pid;
    onstartClassifier(projectId,basicLearningInputValue);
  }
  handleOk = (e) => {
     const {onChangeBasicLearningModal} = this.props;
    onChangeBasicLearningModal(false);
  }
  handleCancel = (e) => {
    const {onChangeBasicLearningModal} = this.props;
    onChangeBasicLearningModal(false);
  }
  showModal = () => {
    const {onChangeBasicLearningModal} = this.props;
    onChangeBasicLearningModal(true);
  }
   tabChangeCallback1=(e)=>{
        console.log(e);
        setTimeout(() => {
        console.log(this.refs.lineEchart+"---");
        },0);//通过延时处理
    }
    public onSelect = (selectedKeys, info) => {
      const {onChangeBasicLearningModal,onChangeBasicLearningInputValue} = this.props;
      console.log( selectedKeys[0], info.node.props.title);
      onChangeBasicLearningModal(false);
      onChangeBasicLearningInputValue(info.node.props.title);
    }
  public tabChangeCallback(key) {
    const {classifier,onChangeTabActiveKeyLoad} = this.props;
    onChangeTabActiveKeyLoad(key);
    if(classifier.projectId==undefined){
      return;
    }
    var xlist = classifier.projectId.xList;
    var ylist = classifier.projectId.ylist;
      if(key == 2){
        setTimeout(() => {
          var myChart = echartsT.init(document.getElementById('main') as HTMLDivElement);
          var option = {
            title: {
                 text: '预测属性：乘客数量',
                },
            grid:{
                    x:35,
                    y:45,
                    x2:35,
                    y2:20,
                    borderWidth:1
                },
            tooltip: {},
            xAxis: {
                data: ylist //["2008-01-01", "2008-02-01", "2008-03-01", "2008-04-01", "2008-05-01"] //////ylist //["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
            },
            yAxis: {},
            series: [
              // {
              //   name: '销量',
              //   type: 'line',
              //   data: xlist//["112", "118", "132", "129", "121"]//xlist//
              // },
              {
                name: '销量',
                type: 'line',
                data: []//xlist//
              },
              {
                name: '销量',
                type: 'line',
                data: []//xlist//
              }
            ]
          };
          if (option && typeof option === "object") {
            var nowarr = [];
            var futurearr = [];
            var xlist1  = JSON.parse(JSON.stringify(xlist));
            var xlist2 = JSON.parse(JSON.stringify(xlist));
            nowarr = xlist1.splice(0,xlist1.length-1);
            nowarr.push("-");
            for(var i = 0 ; i < xlist2.length-1 ; i ++){
              futurearr.push("-");
            }
            futurearr.push(xlist2.splice(xlist2.length-1)[0]);
            option.series[0].data =futurearr// [4, 8,64,56,'-'];
            option.series[1].data =nowarr// ['-','-','-','-',256];
            myChart.setOption(option, true);
        }
           myChart.setOption(option);
        },5);
       // 通过延时处理
      }
  }
}

const mapStateToProps = createStructuredSelector({
  classifier : makeNewClassifier(),
  defaultKey :makeClassifierTabsActiveKey(),
  isVisible:makeClassifierIsVisible(),
  basicLearningInputValue:makeClassifierBasicLearningValue()
})

export function mapDispatchToProps (dispatch) {
  return {
    onLoadDisplays: (projectId) => dispatch(loadDisplays(projectId)),
     //yzh
    onLoadClassifier:(projectId)=>dispatch(loadClassifier(projectId)),
    onstartClassifier:(projectId,basicLearningInputValue)=>dispatch(startClassifier(projectId,basicLearningInputValue)),
    onChangeTabActiveKeyLoad:(key)=>dispatch(changeTabActiveKeyLoad(key)),
    onChangeBasicLearningModal:(isVisible)=>dispatch(changeBasicLearningModal(isVisible)),
    onChangeBasicLearningInputValue:(value)=>dispatch(changeBasicLearningInputValue(value))
  }
}

const withConnect = connect(mapStateToProps, mapDispatchToProps)
const withDisplayReducer = injectReducer({ key: 'display', reducer: displayReducer })
const withDisplaySaga = injectSaga({ key: 'display', saga: displaySaga })
const withClassifierSaga= injectSaga({ key: 'classifier', saga: classifierSaga })//yzh
const withClassifierReducer = injectReducer({key:'classifier',reducer:classifierReducer})
const withPortalReducer = injectReducer({ key: 'portal', reducer: portalReducer })
const withPortalSaga = injectSaga({ key: 'portal', saga: portalSaga })

export default compose(
  withClassifierSaga,
  withClassifierReducer,
  withDisplayReducer,
  withDisplaySaga,
  withPortalReducer,
  withPortalSaga,
  withConnect
)(Classifier)
