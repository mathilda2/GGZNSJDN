import * as React from 'react'
import {IOrganization} from '../../Organizations/Organization'
import {IProject} from '../../Projects'

interface IModulePermissionProps {
  size?: string
  type?: string
  icon?: string
  onClick?: any
  className?: string
  permission?: IOrganization
  route?: any[]
  active?: any
  params?: any
}

export default (project?: IProject, item?: any) => (WrapperComponent) => {
  class MenuPermission extends React.PureComponent<IModulePermissionProps, {}> {
    private getPermissionByCurrentProject = () => {
      let permission = ''
      //console.log(item);
      if (project) {
        const projectPermission = project.permission
        //console.log(projectPermission);
        for (const attr in projectPermission) {
          if (attr) {
            const pStr = attr.slice(0, -10)
            if (pStr === item) {
              permission = projectPermission [attr]
            }
          }
        }
      }
     // console.log(permission);
      return permission
    }

    private computePermission = () => {
      const permission = this.getPermissionByCurrentProject()
      const defaultComponent = <span/>
      if (!project) {
        return defaultComponent
      }
      if (permission) {
        switch (Number(permission)) {
          case 0:
            return defaultComponent
          default:
            return <WrapperComponent {...this.props}>{this.props.children}</WrapperComponent>
        }
      } else {
        return defaultComponent
      }
    }

    public render () {
      const result = this.computePermission()
      return result
    }
  }
  return MenuPermission
}





