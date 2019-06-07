import { Injectable, Type } from '@angular/core';
import { DashboardItem } from './dashboard-item';
import { CameraComponent } from '@scadalts/scadalts-dashbord-components/camera/camera.component';
import { IncrementatorComponent } from '@scadalts/scadalts-dashbord-components/incrementator/incrementator.component';
import { IframeComponent } from '@scadalts/scadalts-dashbord-components/iframe/iframe.component';

/**
 * This is the major class to be changed when user created a new component
 * Add necessary data to all methods included inside this class.
 */
@Injectable()
export class ItemService {

  /**
   * get Dashboard Item Factory
   * Factory pattern to create custom components based on the type
   * defined in recived data.
   *
   * @Important - When a new Custom Component has been created
   * add a new checking condition to this method to create this custom
   * component. In sinilar way as it is presented below.
   *
   * Remember to add this components to EntryComponents array in
   * item.module.ts file inside this directory.
   *
   * !!! Also add this component to item.module.file as entryComponent !!!
   *
   * @param componentType - Component type which has to be created
   * @param componentData - Component data.
   */
  static getDashboardItem(componentType: string, componentData: any): DashboardItem {
    if (componentType === 'incrementator') {
      return new DashboardItem(IncrementatorComponent, componentData);
    }
    if (componentType === 'camera') {
      return new DashboardItem(CameraComponent, componentData);
    }
    if (componentType === 'iframe') {
      return new DashboardItem(IframeComponent, componentData);
    }

    /* Place for another component */
    // if (componentType == '<your_code>'){
    // return new DashboadItem(<yourComponent>, componentData);
    // }

  }

  /**
   * Get Component Type Code
   *
   * When saving to JSON object the type of the component must be specified
   * so this method returns the component 'type' which has to be saved.
   * @param component - Component varialbe
   */
  static getComponentType(component: Type<any>) {
    if (component.name === 'IncrementatorComponent') {
      return 'incrementator';
    }
    if (component.name === 'CameraComponent') {
      return 'camera';
    }
    if (component.name === 'IframeComponent') {
      return 'iframe';
    }

  }

  /**
   * get Dashboard Components List
   *
   * Static method which returns list of all the exisitng components
   * If user has created a new component it should be implemented here.
   * Returns an array of objects which contains the type-code,
   * human readable label of this component and data Object template.
   *
   * Remember to set up a template for 'data' variable. If it won't be set
   * User won't be able to edit this component in Edit Dialog!
   */
  static getDashboardComponentsList() {
    return [
      { id: 0, type: 'incrementator', label: 'SLTS Visit Counter', data: { number: 0, label: '', datapointXid: '' } },
      { id: 1, type: 'camera', label: 'SLTS Image Component', data: { number: 0, label: '', imageLocation: '' } },
      // { id: 2, type: 'iframe', label: 'SLTS Inlie Frame', data: { number: 0, label: '', frameLocation: '' } }
    ];
  }

}
